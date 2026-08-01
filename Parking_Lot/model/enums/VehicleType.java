package model.enums;

import java.util.List;
import model.enums.SpotType;

/**
 * VehicleType carries its own compatible spot list directly.
 *
 * WHY enum with behavior instead of a plain enum?
 * - Removes the need for if-else / switch in strategy classes.
 * - Adding a new vehicle type = add one enum constant here.
 *   Zero changes in NearestSpotStrategy or ParkingLot.
 * - This is the Open/Closed principle applied to an enum.
 *
 * Order in the List matters — strategy tries them left to right.
 * First entry = most preferred spot type.
 */
public enum VehicleType {

    CAR(List.of(SpotType.COMPACT, SpotType.LARGE)),

    TRUCK(List.of(SpotType.LARGE)),

    MOTORCYCLE(List.of(SpotType.MOTORCYCLE, SpotType.COMPACT)),

    ELECTRIC_CAR(List.of(SpotType.ELECTRIC, SpotType.COMPACT));

    private final List<SpotType> compatibleSpots;

    VehicleType(List<SpotType> compatibleSpots) {
        this.compatibleSpots = compatibleSpots;
    }

    /**
     * Returns the ordered list of spot types this vehicle can use.
     * Strategy reads this — no if-else chains needed anywhere.
     */
    public List<SpotType> getCompatibleSpots() {
        return compatibleSpots;
    }
}
