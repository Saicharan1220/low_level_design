package gate;

import core.ParkingLot;
import fee.FeeCalculator;
import model.ParkingTicket;

import java.time.LocalDateTime;

/**
 * Physical exit gate — processes vehicles leaving the lot.
 *
 * RESPONSIBILITIES (Single Responsibility):
 * - Accept a ticket from an exiting vehicle
 * - Calculate the fee via FeeCalculator (Strategy)
 * - Delegate spot release to ParkingLot
 * - Print receipt and return fee amount
 *
 * WHY does ExitGate hold FeeCalculator, not ParkingLot?
 * Fee calculation is an exit concern — it happens at the gate.
 * ParkingLot's job is managing spots, not billing.
 * Different exit gates could have different fee strategies
 * (e.g., a VIP exit with flat rate, staff exit with zero fee).
 * Injecting FeeCalculator per gate makes this easy.
 *
 * TIMING: exitTime is captured as the FIRST thing in exit().
 * Any processing delay (DB writes, printing) should not inflate
 * the duration the vehicle is charged for.
 */
public class ExitGate {

    private final int gateId;
    private final ParkingLot parkingLot;
    private final FeeCalculator feeCalculator;

    public ExitGate(int gateId, ParkingLot parkingLot, FeeCalculator feeCalculator) {
        this.gateId        = gateId;
        this.parkingLot    = parkingLot;
        this.feeCalculator = feeCalculator;
    }

    /**
     * Processes the exit of a vehicle.
     *
     * @param ticket the ticket issued at entry
     * @return fee charged
     * @throws IllegalArgumentException if ticket is invalid or already processed
     */
    public double exit(ParkingTicket ticket) {
        // Capture exit time FIRST — before any processing inflates the duration
        LocalDateTime exitTime = LocalDateTime.now();

        System.out.println("\n[Exit Gate " + gateId + "] Exiting: "
                + ticket.licensePlate()
                + " | Ticket: " + ticket.ticketId().substring(0, 8) + "...");

        double fee = feeCalculator.calculateFee(ticket, exitTime);
        parkingLot.unpark(ticket);

        System.out.printf("[Exit Gate %d] Spot %s released | Fee charged: %.2f%n",
                gateId, ticket.spotId(), fee);

        return fee;
    }

    public int getGateId() { return gateId; }
}
