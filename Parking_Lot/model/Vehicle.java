package model;

import model.enums.VehicleType;

/**
 * Represents a vehicle attempting to park.
 *
 * DESIGN DECISION: Java Record instead of abstract class hierarchy.
 *
 * PREVIOUS DESIGN had: abstract Vehicle → Car, Truck, Motorcycle, ElectricCar
 * WHY WE SIMPLIFIED:
 *   - The only thing subclasses did was return compatible spot types.
 *   - That behavior now lives in VehicleType enum itself via getCompatibleSpots().
 *   - With that moved out, there is ZERO difference between Car and Truck
 *     as Java classes — both just held a licensePlate and a type.
 *   - Four nearly-identical subclasses for no behavioral difference = unnecessary complexity.
 *
 * RECORD gives us:
 *   - Immutability by default (all fields final)
 *   - Auto-generated constructor, getters, equals(), hashCode(), toString()
 *   - Validation in the compact constructor
 *   - No boilerplate
 *
 * INTERVIEW ANSWER if asked "why record over abstract class?":
 *   "Vehicle is pure data — license plate and type. It has no polymorphic behavior
 *    after we moved compatibility logic into VehicleType. A record signals that
 *    intent clearly and eliminates boilerplate. If vehicles needed distinct
 *    behavior (e.g., electric vehicles needing a charge check before parking),
 *    I'd reintroduce subclasses at that point — not before."
 */
public record Vehicle(String licensePlate, VehicleType type) {

    // Compact constructor — runs validation before fields are assigned
    public Vehicle {
        if (licensePlate == null || licensePlate.isBlank()) {
            throw new IllegalArgumentException("License plate cannot be blank");
        }
        licensePlate = licensePlate.toUpperCase().trim();
    }

    // Convenience: delegate to VehicleType for compatible spots
    // Keeps callers clean — they ask the vehicle, not the enum directly
    public java.util.List<model.enums.SpotType> compatibleSpotTypes() {
        return type.getCompatibleSpots();
    }

    @Override
    public String toString() {
        return type + "[" + licensePlate + "]";
    }
}
