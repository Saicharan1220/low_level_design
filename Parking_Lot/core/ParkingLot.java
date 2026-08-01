package core;

import model.ParkingFloor;
import model.ParkingSpot;
import model.ParkingTicket;
import model.Vehicle;
import model.enums.SpotType;
import strategy.SpotAssignmentStrategy;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central orchestrator of the parking lot system.
 *
 * DESIGN PATTERNS:
 * - Singleton (double-checked locking + volatile)
 * - Strategy (SpotAssignmentStrategy injected at initialization)
 *
 * WHY double-checked locking here instead of Holder pattern?
 * The Holder pattern works perfectly for zero-argument singletons.
 * But ParkingLot needs external config at construction time:
 * name, floors, and strategy. The Holder class would be initialized
 * at class-load time — before we have that config.
 * Double-checked locking with volatile lets us delay construction
 * until initialize() is called with the real config.
 *
 * CONCURRENCY DESIGN — THE CORE CHALLENGE:
 *
 * Problem: Multiple entry gates (threads) call park() simultaneously.
 * Two threads could both find the same spot available, then both
 * try to park in it — double booking.
 *
 * Solution: synchronized(this) wraps the find+park as ONE atomic unit.
 *   Thread A: finds spot F1-C-001 → parks in it → releases lock
 *   Thread B: waited → finds F1-C-001 occupied → finds F1-C-002 → parks
 *
 * WHY not synchronize the whole park() method?
 * The duplicate-plate check and null validation don't need the lock.
 * Narrowing synchronized to just find+park reduces contention time —
 * threads wait less, throughput improves.
 *
 * WHY ConcurrentHashMap for activeTickets?
 * Multiple threads may read/write tickets concurrently.
 * ConcurrentHashMap gives thread-safe get/put/remove operations
 * at segment level — no full map lock on every operation.
 * HashMap would require external synchronization on every access.
 *
 * SCALING TO MANY GATES:
 * Current: one lot-level lock — correct, simple, good for most cases.
 * Next level: per-floor ReentrantLock — Gate 1 and Gate 2 can park
 * on different floors simultaneously. We'd lock floor by floor in
 * findSpot() and release as soon as a spot is committed.
 * This is the right evolution once profiling shows lock contention.
 */
public class ParkingLot {

    private final String name;
    private final List<ParkingFloor> floors;
    private final SpotAssignmentStrategy assignmentStrategy;

    // ticketId → ParkingTicket for all currently active (parked) sessions
    private final ConcurrentHashMap<String, ParkingTicket> activeTickets;

    // spotId → ParkingSpot — built once at construction, O(1) lookup during unpark
    // Spots never change after construction so this map never needs updating.
    // Unmodifiable + final = safe to read from any thread without synchronization.
    private final Map<String, ParkingSpot> spotRegistry;

    // -------------------------------------------------------------------------
    // Singleton — double-checked locking with volatile
    // volatile ensures the fully-constructed ParkingLot is visible to all
    // threads before the reference is published. Without volatile, a thread
    // could see a non-null reference to a partially-constructed object.
    // -------------------------------------------------------------------------
    private static volatile ParkingLot instance;

    private ParkingLot(String name, List<ParkingFloor> floors,
                       SpotAssignmentStrategy assignmentStrategy) {
        this.name               = name;
        this.floors             = Collections.unmodifiableList(floors);
        this.assignmentStrategy = assignmentStrategy;
        this.activeTickets      = new ConcurrentHashMap<>();

        // Build spot registry once — O(total spots) paid at startup,
        // every subsequent findSpotById() call is O(1) forever.
        Map<String, ParkingSpot> registry = new HashMap<>();

        for (ParkingFloor floor : floors) {
            for (SpotType type : SpotType.values()) {
                for (ParkingSpot spot : floor.getAllSpots(type)) {
                    registry.put(spot.getSpotId(), spot);
                }
            }
        }
        this.spotRegistry = Collections.unmodifiableMap(registry);
    }

    /**
     * One-time initialization — call once at application startup.
     * Subsequent calls return the existing instance unchanged.
     *
     * @throws IllegalArgumentException if name or floors are invalid
     */
    public static ParkingLot initialize(String name,
                                        List<ParkingFloor> floors,
                                        SpotAssignmentStrategy strategy) {
        if (instance == null) {
            synchronized (ParkingLot.class) {
                if (instance == null) {
                    if (name == null || name.isBlank())
                        throw new IllegalArgumentException("Lot name cannot be blank");
                    if (floors == null || floors.isEmpty())
                        throw new IllegalArgumentException("Lot must have at least one floor");

                    instance = new ParkingLot(name, floors, strategy);
                    System.out.println("[ParkingLot] '" + name + "' initialised — "
                            + floors.size() + " floor(s).");
                }
            }
        }
        return instance;
    }

    /**
     * Returns the singleton instance.
     *
     * @throws IllegalStateException if initialize() has not been called
     */
    public static ParkingLot getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                "ParkingLot not initialized. Call ParkingLot.initialize() first.");
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // Core Operations
    // -------------------------------------------------------------------------

    /**
     * Parks a vehicle and returns a ticket.
     *
     * CONCURRENCY: The synchronized block makes find+park atomic.
     * No two threads can interleave between finding a spot and parking in it.
     *
     * Steps outside the lock (safe to run concurrently):
     *   1. Null check
     *   2. Duplicate plate check (reads ConcurrentHashMap — thread-safe)
     *
     * Steps inside the lock (must be atomic):
     *   3. Find a spot via strategy
     *   4. Call spot.park(vehicle)
     *   5. Create and register the ticket
     *
     * @return Optional ticket — present if parked, empty if lot is full
     */
    public Optional<ParkingTicket> park(Vehicle vehicle) {
        if (vehicle == null) throw new IllegalArgumentException("Vehicle cannot be null");

        // Duplicate check — ConcurrentHashMap.values() is safe to iterate
        boolean alreadyParked = activeTickets.values().stream()
                .anyMatch(t -> t.licensePlate().equals(vehicle.licensePlate()));
        if (alreadyParked) {
            System.out.println("  [ParkingLot] " + vehicle.licensePlate()
                    + " is already parked.");
            return Optional.empty();
        }

        // CRITICAL SECTION — find and park must be one atomic operation
        synchronized (this) {
            Optional<ParkingSpot> spotOpt = assignmentStrategy.findSpot(floors, vehicle);

            if (spotOpt.isEmpty()) {
                System.out.println("  [ParkingLot] No spot available for " + vehicle);
                return Optional.empty();
            }

            ParkingSpot spot = spotOpt.get();
            spot.park(vehicle);                              // spot-level sync (defensive)

            ParkingTicket ticket = ParkingTicket.of(spot, vehicle);
            activeTickets.put(ticket.ticketId(), ticket);   // ConcurrentHashMap — safe

            System.out.println("  [ParkingLot] Parked " + vehicle
                    + " → spot " + spot.getSpotId()
                    + " (Floor " + spot.getFloorNumber() + ")");

            return Optional.of(ticket);
        }
    }

    /**
     * Releases the spot for a given ticket.
     *
     * unpark does NOT need the lot-level synchronized block because:
     * - Each ticket maps to exactly one unique spot.
     * - ConcurrentHashMap.remove() is atomic — only one thread can
     *   successfully remove a given ticketId (others get null back).
     * - That null check acts as the guard: if two threads somehow
     *   try to unpark the same ticket, only one succeeds.
     * - spot.vacate() is synchronized at the spot level.
     *
     * @throws IllegalArgumentException if ticket is unknown or already used
     */
    public void unpark(ParkingTicket ticket) {
        if (ticket == null) throw new IllegalArgumentException("Ticket cannot be null");

        // remove() is atomic — returns null if ticketId not present
        ParkingTicket active = activeTickets.remove(ticket.ticketId());
        if (active == null) {
            throw new IllegalArgumentException(
                "Invalid or already-processed ticket: " + ticket.ticketId());
        }

        ParkingSpot spot = findSpotById(active.spotId());
        if (spot == null) {
            throw new IllegalStateException(
                "Data inconsistency: spot not found for ticketId " + ticket.ticketId());
        }

        spot.vacate();
        System.out.println("  [ParkingLot] Spot " + active.spotId() + " is now free.");
    }

    /**
     * Prints current availability across all floors.
     */
    public void displayAvailability() {
        System.out.println("\n===== " + name + " — Availability =====");
        for (ParkingFloor floor : floors) {
            floor.printAvailability();
        }
        System.out.println("  Active sessions: " + activeTickets.size());
        System.out.println("==========================================\n");
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    /**
     * O(1) spot lookup via pre-built registry.
     * Registry is built once at construction — spots never added/removed after.
     * Safe to call from any thread without synchronization (read-only map).
     */
    private ParkingSpot findSpotById(String spotId) {
        return spotRegistry.get(spotId);
    }

    public String getName()            { return name; }
    public List<ParkingFloor> getFloors() { return floors; }
    public int getActiveSessionCount() { return activeTickets.size(); }

    /**
     * Resets the singleton — for test scenarios only.
     * Never call this in production code.
     */
    public static void reset() { instance = null; }
}
