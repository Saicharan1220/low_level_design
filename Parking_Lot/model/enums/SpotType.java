package model.enums;

/**
 * Physical category of a parking spot.
 *
 * SpotType and VehicleType are intentionally separate:
 * - VehicleType = what the vehicle IS
 * - SpotType    = what the spot IS
 *
 * The compatibility mapping (which vehicle fits which spot)
 * lives in VehicleType.getCompatibleSpots().
 * SpotType stays a pure classification label — no behavior.
 *
 * WHY not merge them?
 * A SpotType has physical attributes (size, equipment).
 * A VehicleType has vehicle attributes (weight, power source).
 * They evolve independently — a new spot type (e.g., HANDICAPPED)
 * doesn't mean a new vehicle type exists.
 */
public enum SpotType {

    MOTORCYCLE,   // smallest — motorcycles only (or as fallback compact)
    COMPACT,      // standard car size
    LARGE,        // trucks and large SUVs
    ELECTRIC      // charging-equipped — electric vehicles preferred
}
