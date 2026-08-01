package model;

import model.enums.VehicleType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Immutable ticket issued when a vehicle enters the lot.
 *
 * DESIGN: Java Record
 * A ticket is a fact — it captures what happened at entry.
 * It must never change after issuance. Record enforces this.
 *
 * Contains vehicleType so FeeCalculator can look up the hourly rate
 * without needing a reference back to the Vehicle object.
 * This is intentional: the vehicle may have left the system by the
 * time the ticket is processed at exit.
 *
 * STATIC FACTORY METHOD: ParkingTicket.of(spot, vehicle)
 * - Hides UUID generation and LocalDateTime.now() from callers
 * - Named factory is more readable than raw constructor
 * - UUID generation strategy can change without touching callers
 */
public record ParkingTicket(
        String ticketId,
        String spotId,
        String licensePlate,
        VehicleType vehicleType,
        LocalDateTime entryTime,
        int floorNumber
) {

    /**
     * Compact constructor — validates before fields are assigned.
     */
    public ParkingTicket {
        if (spotId == null || spotId.isBlank())
            throw new IllegalArgumentException("Ticket must have a valid spotId");
        if (entryTime == null)
            throw new IllegalArgumentException("Ticket must have an entry time");
        if (licensePlate == null || licensePlate.isBlank())
            throw new IllegalArgumentException("Ticket must have a license plate");
    }

    /**
     * Factory method — preferred way to create a ticket.
     */
    public static ParkingTicket of(ParkingSpot spot, Vehicle vehicle) {
        return new ParkingTicket(
                UUID.randomUUID().toString(),
                spot.getSpotId(),
                vehicle.licensePlate(),
                vehicle.type(),
                LocalDateTime.now(),
                spot.getFloorNumber()
        );
    }
}
