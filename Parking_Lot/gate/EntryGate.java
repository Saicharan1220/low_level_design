package gate;

import core.ParkingLot;
import model.ParkingTicket;
import model.Vehicle;

import java.util.Optional;

/**
 * Physical entry gate — the boundary between outside and the parking lot.
 *
 * RESPONSIBILITIES (Single Responsibility):
 * - Accept an incoming vehicle
 * - Delegate spot-finding and parking to ParkingLot
 * - Return a ticket or inform the driver the lot is full
 *
 * DESIGN: Intentionally thin.
 * EntryGate contains ZERO business logic.
 * It is a facade that formats input, calls ParkingLot, and formats output.
 * All decisions live in ParkingLot + SpotAssignmentStrategy.
 *
 * MULTIPLE GATES:
 * Each gate is a separate object with its own gateId.
 * All gates share the same ParkingLot singleton reference.
 * Concurrency safety is handled inside ParkingLot.park() —
 * gates themselves do not need any synchronization.
 *
 * In a real system, each gate would also have:
 * - A physical ticket printer (hardware abstraction)
 * - A barrier/boom gate controller
 * - A camera for license plate recognition
 * These would be injected as dependencies, not hardcoded.
 */
public class EntryGate {

    private final int gateId;
    private final ParkingLot parkingLot;

    public EntryGate(int gateId, ParkingLot parkingLot) {
        this.gateId     = gateId;
        this.parkingLot = parkingLot;
    }

    /**
     * Processes a vehicle arriving at this gate.
     *
     * @param vehicle the incoming vehicle
     * @return Optional ticket — present if parked, empty if lot is full
     */
    public Optional<ParkingTicket> enter(Vehicle vehicle) {
        System.out.println("\n[Entry Gate " + gateId + "] Arriving: " + vehicle);

        Optional<ParkingTicket> ticket = parkingLot.park(vehicle);

        if (ticket.isPresent()) {
            ParkingTicket t = ticket.get();
            System.out.printf("[Entry Gate %d] Ticket: %s | Spot: %s | Floor: %d%n",
                    gateId, t.ticketId().substring(0, 8) + "...",
                    t.spotId(), t.floorNumber());
        } else {
            System.out.println("[Entry Gate " + gateId + "] No spot available for " + vehicle);
        }

        return ticket;
    }

    public int getGateId() { return gateId; }
}
