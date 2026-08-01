package model;

import model.enums.SpotType;

/**
 * Represents a single physical parking spot.
 *
 * RESPONSIBILITIES (Single Responsibility Principle):
 * - Know its identity: spotId, floor, type
 * - Know and manage its occupancy state
 * - Park and vacate a vehicle safely
 *
 * CONCURRENCY — WHY synchronized here even though ParkingLot also locks?
 *
 * ParkingLot.park() wraps find+park in synchronized(this).
 * So for the normal parking flow, ParkingLot's lock already protects us.
 *
 * BUT ParkingSpot is a public class. Two scenarios bypass ParkingLot's lock:
 *   1. Someone calls spot.park() directly (bypassing ParkingLot entirely)
 *   2. Display threads call spot.isAvailable() concurrently while a park() is happening
 *
 * synchronized on the spot instance handles both.
 * Each spot locks only itself — spots on different floors never contend with each other.
 * This is fine-grained locking: Floor 1 Spot A and Floor 2 Spot B can be
 * parked into simultaneously with zero contention.
 *
 * RULE OF THUMB: A class that manages mutable shared state should protect
 * that state itself — not rely on callers to hold a lock before using it.
 */
public class ParkingSpot {

    private final String spotId;       // e.g. "F1-C-001"
    private final int floorNumber;
    private final SpotType spotType;

    // Mutable state — must be protected
    private boolean isOccupied;
    private Vehicle parkedVehicle;

    public ParkingSpot(String spotId, int floorNumber, SpotType spotType) {
        this.spotId = spotId;
        this.floorNumber = floorNumber;
        this.spotType = spotType;
        this.isOccupied = false;
        this.parkedVehicle = null;
    }

    /**
     * Parks a vehicle. synchronized — prevents two threads double-booking this spot.
     */
    public synchronized void park(Vehicle vehicle) {
        if (isOccupied) {
            throw new IllegalStateException(
                "Spot " + spotId + " already occupied by " + parkedVehicle);
        }
        this.parkedVehicle = vehicle;
        this.isOccupied = true;
    }

    /**
     * Vacates this spot and returns the vehicle that was parked.
     * synchronized — pairs with park() for consistent state transitions.
     */
    public synchronized Vehicle vacate() {
        if (!isOccupied) {
            throw new IllegalStateException("Spot " + spotId + " is already empty");
        }
        Vehicle vehicle = this.parkedVehicle;
        this.parkedVehicle = null;
        this.isOccupied = false;
        return vehicle;
    }

    /**
     * Thread-safe availability check.
     * NOTE: isAvailable() alone is not sufficient for a park decision —
     * spot could be taken between this check and park().
     * ParkingLot handles atomicity of the combined find+park.
     */
    public synchronized boolean isAvailable() {
        return !isOccupied;
    }

    // --- Getters ---
    public String getSpotId()        { return spotId; }
    public int getFloorNumber()      { return floorNumber; }
    public SpotType getSpotType()    { return spotType; }

    public synchronized Vehicle getParkedVehicle() { return parkedVehicle; }

    @Override
    public String toString() {
        return spotId + "(" + spotType + ")" +
               (isOccupied ? "[OCCUPIED:" + parkedVehicle + "]" : "[FREE]");
    }
}
