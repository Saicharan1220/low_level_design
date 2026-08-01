package model;

import model.enums.SpotType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Represents one floor of the parking lot.
 *
 * RESPONSIBILITIES:
 * - Own and organise all ParkingSpots on this floor grouped by SpotType
 * - Answer availability queries per SpotType
 * - Provide display summary
 *
 * DATA STRUCTURE: EnumMap<SpotType, List<ParkingSpot>>
 *
 * WHY EnumMap over HashMap?
 * EnumMap is internally backed by a plain array indexed by enum ordinal.
 * - O(1) get/put with zero hash collision (enum ordinals are unique integers)
 * - More memory-efficient than HashMap for small fixed key sets
 * - Iterates in enum declaration order — predictable, consistent display
 *
 * CONCURRENCY NOTE:
 * ParkingFloor does not need its own lock.
 * - addSpot() is called only once during lot construction (single-threaded setup)
 * - getAvailableSpots() reads spot state — each spot is independently synchronized
 * - The returned snapshot list is safe; callers (strategy) confirm availability
 *   by actually calling spot.park() which is synchronized at the spot level
 */
public class ParkingFloor {

    private final int floorNumber;
    private final Map<SpotType, List<ParkingSpot>> spotsByType;

    public ParkingFloor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.spotsByType = new EnumMap<>(SpotType.class);
        for (SpotType type : SpotType.values()) {
            spotsByType.put(type, new ArrayList<>());
        }
    }

    /**
     * Adds a spot during lot construction. Not called after setup.
     */
    public void addSpot(ParkingSpot spot) {
        spotsByType.get(spot.getSpotType()).add(spot);
    }

    /**
     * Returns a snapshot of currently available spots of the given type.
     * The list is a new ArrayList — safe for callers to iterate even if
     * spot states change concurrently after this call.
     */
    public List<ParkingSpot> getAvailableSpots(SpotType spotType) {
        List<ParkingSpot> available = new ArrayList<>();
        for (ParkingSpot spot : spotsByType.getOrDefault(spotType, Collections.emptyList())) {
            if (spot.isAvailable()) {
                available.add(spot);
            }
        }
        return available;
    }

    /**
     * Returns ALL spots of a type (occupied + available).
     * Used by ParkingLot.findSpotById() during unpark.
     */
    public List<ParkingSpot> getAllSpots(SpotType spotType) {
        return Collections.unmodifiableList(
            spotsByType.getOrDefault(spotType, Collections.emptyList())
        );
    }

    public int getAvailableCount(SpotType spotType) {
        return getAvailableSpots(spotType).size();
    }

    public int getTotalCount(SpotType spotType) {
        return spotsByType.getOrDefault(spotType, Collections.emptyList()).size();
    }

    public int getFloorNumber() { return floorNumber; }

    public void printAvailability() {
        System.out.println("  Floor " + floorNumber + ":");
        for (SpotType type : SpotType.values()) {
            int available = getAvailableCount(type);
            int total     = getTotalCount(type);
            System.out.printf("    %-12s : %d / %d available%n", type, available, total);
        }
    }
}
