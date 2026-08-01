import core.ParkingLot;
import fee.HourlyFeeCalculator;
import gate.EntryGate;
import gate.ExitGate;
import model.ParkingFloor;
import model.ParkingSpot;
import model.ParkingTicket;
import model.Vehicle;
import model.enums.SpotType;
import model.enums.VehicleType;
import strategy.NearestSpotStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Runs all key scenarios end-to-end.
 *
 * LOT LAYOUT — 2 floors, each with:
 *   2 MOTORCYCLE spots, 3 COMPACT spots, 1 LARGE spot, 1 ELECTRIC spot
 *
 * SCENARIOS:
 *  1. Basic park — Car takes COMPACT (preferred over LARGE)
 *  2. Motorcycle parks in MOTORCYCLE spot
 *  3. Truck parks in LARGE spot
 *  4. Electric car parks in ELECTRIC spot
 *  5. Duplicate entry — same plate rejected
 *  6. Lot full for Truck — both LARGE spots taken, third truck rejected
 *  7. Motorcycle fallback — MOTORCYCLE spots full, falls back to COMPACT
 *  8. Normal exit — car pays and spot is freed
 *  9. Invalid ticket at exit — graceful error, no crash
 * 10. Display availability before and after key operations
 */
public class Main {

    public static void main(String[] args) {

        // Reset singleton so this can be re-run cleanly in the same JVM
        ParkingLot.reset();

        // ------------------------------------------------------------------
        // BUILD THE LOT
        // ------------------------------------------------------------------
        ParkingFloor floor1 = buildFloor(1, 2, 3, 1, 1);
        ParkingFloor floor2 = buildFloor(2, 2, 3, 1, 1);

        ParkingLot lot = ParkingLot.initialize(
                "MallPark Central",
                List.of(floor1, floor2),
                new NearestSpotStrategy()
        );

        HourlyFeeCalculator fees = new HourlyFeeCalculator();
        EntryGate entry1 = new EntryGate(1, lot);
        EntryGate entry2 = new EntryGate(2, lot);
        ExitGate  exit1  = new ExitGate(1, lot, fees);

        lot.displayAvailability();

        // ------------------------------------------------------------------
        // SCENARIO 1: Car — should take COMPACT (preferred over LARGE)
        // ------------------------------------------------------------------
        System.out.println("===== SCENARIO 1: Car parks in COMPACT =====");
        Vehicle car1 = new Vehicle("KA-01-AB-1234", VehicleType.CAR);
        Optional<ParkingTicket> ticket1 = entry1.enter(car1);

        // ------------------------------------------------------------------
        // SCENARIO 2: Motorcycle — should take MOTORCYCLE spot
        // ------------------------------------------------------------------
        System.out.println("\n===== SCENARIO 2: Motorcycle parks =====");
        Vehicle moto1 = new Vehicle("KA-02-MC-5678", VehicleType.MOTORCYCLE);
        Optional<ParkingTicket> ticket2 = entry1.enter(moto1);

        // ------------------------------------------------------------------
        // SCENARIO 3: Truck — should take LARGE spot
        // ------------------------------------------------------------------
        System.out.println("\n===== SCENARIO 3: Truck parks =====");
        Vehicle truck1 = new Vehicle("KA-03-TR-9999", VehicleType.TRUCK);
        Optional<ParkingTicket> ticket3 = entry2.enter(truck1);

        // ------------------------------------------------------------------
        // SCENARIO 4: Electric car — should take ELECTRIC spot
        // ------------------------------------------------------------------
        System.out.println("\n===== SCENARIO 4: Electric car parks =====");
        Vehicle ev1 = new Vehicle("KA-04-EV-1111", VehicleType.ELECTRIC_CAR);
        Optional<ParkingTicket> ticket4 = entry1.enter(ev1);

        lot.displayAvailability();

        // ------------------------------------------------------------------
        // SCENARIO 5: Duplicate entry — same plate as car1
        // ------------------------------------------------------------------
        System.out.println("===== SCENARIO 5: Duplicate plate rejected =====");
        Vehicle car1Again = new Vehicle("KA-01-AB-1234", VehicleType.CAR);
        Optional<ParkingTicket> dupTicket = entry1.enter(car1Again);
        System.out.println("  Duplicate parked: " + dupTicket.isPresent()); // false

        // ------------------------------------------------------------------
        // SCENARIO 6: Lot full for Trucks — fill floor2 LARGE, then reject
        // ------------------------------------------------------------------
        System.out.println("\n===== SCENARIO 6: Lot full for Trucks =====");
        Vehicle truck2 = new Vehicle("KA-05-TR-2222", VehicleType.TRUCK);
        entry2.enter(truck2); // fills floor2 LARGE — both floors now full for trucks

        Vehicle truck3 = new Vehicle("KA-06-TR-3333", VehicleType.TRUCK);
        Optional<ParkingTicket> rejectedTruck = entry1.enter(truck3);
        System.out.println("  Truck3 parked: " + rejectedTruck.isPresent()); // false

        // ------------------------------------------------------------------
        // SCENARIO 7: Motorcycle fallback to COMPACT
        //   Fill both MOTORCYCLE spots on floor1, then try a third motorcycle
        // ------------------------------------------------------------------
        System.out.println("\n===== SCENARIO 7: Motorcycle fallback to COMPACT =====");
        Vehicle moto2 = new Vehicle("KA-07-MC-4444", VehicleType.MOTORCYCLE);
        entry1.enter(moto2); // takes 2nd MOTORCYCLE spot on floor1

        // Both floor1 MOTORCYCLE spots now taken
        // floor2 still has MOTORCYCLE spots — NearestSpotStrategy will use floor2 first
        // To force COMPACT fallback, fill floor2 MOTORCYCLE spots too
        Vehicle moto3 = new Vehicle("KA-08-MC-5555", VehicleType.MOTORCYCLE);
        entry1.enter(moto3); // takes floor2 MOTORCYCLE spot 1
        Vehicle moto4 = new Vehicle("KA-09-MC-6666", VehicleType.MOTORCYCLE);
        entry1.enter(moto4); // takes floor2 MOTORCYCLE spot 2 — all MOTORCYCLE spots full

        Vehicle moto5 = new Vehicle("KA-10-MC-7777", VehicleType.MOTORCYCLE);
        Optional<ParkingTicket> moto5Ticket = entry1.enter(moto5); // must fall back to COMPACT
        moto5Ticket.ifPresentOrElse(
            t -> System.out.println("  Moto5 fallback spot: " + t.spotId()
                    + " (expect COMPACT type)"),
            () -> System.out.println("  Moto5 rejected — no spot available")
        );

        lot.displayAvailability();

        // ------------------------------------------------------------------
        // SCENARIO 8: Normal exit — car1 pays and leaves
        // ------------------------------------------------------------------
        System.out.println("===== SCENARIO 8: Car exits and pays =====");
        ticket1.ifPresent(t -> {
            double fee = exit1.exit(t);
            System.out.printf("  Total paid by %s: %.2f%n", car1.licensePlate(), fee);
        });

        // Verify the spot was freed
        lot.displayAvailability();

        // ------------------------------------------------------------------
        // SCENARIO 9: Invalid ticket (already processed)
        // ------------------------------------------------------------------
        System.out.println("===== SCENARIO 9: Invalid ticket at exit =====");
        ticket1.ifPresent(t -> {
            try {
                exit1.exit(t); // ticket already removed from activeTickets
            } catch (IllegalArgumentException e) {
                System.out.println("  Caught expected error: " + e.getMessage());
            }
        });

        System.out.println("\n===== ALL SCENARIOS COMPLETE =====");
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Builds a floor with the specified number of each spot type.
     * Spot ID format: F{floor}-{TypeInitial}-{seq}   e.g. F1-C-001
     */
    private static ParkingFloor buildFloor(int floorNum,
                                            int motorcycleCount,
                                            int compactCount,
                                            int largeCount,
                                            int electricCount) {
        ParkingFloor floor = new ParkingFloor(floorNum);
        addSpots(floor, floorNum, SpotType.MOTORCYCLE, "M", motorcycleCount);
        addSpots(floor, floorNum, SpotType.COMPACT,    "C", compactCount);
        addSpots(floor, floorNum, SpotType.LARGE,      "L", largeCount);
        addSpots(floor, floorNum, SpotType.ELECTRIC,   "E", electricCount);
        return floor;
    }

    private static void addSpots(ParkingFloor floor, int floorNum,
                                  SpotType type, String prefix, int count) {
        for (int i = 1; i <= count; i++) {
            String id = String.format("F%d-%s-%03d", floorNum, prefix, i);
            floor.addSpot(new ParkingSpot(id, floorNum, type));
        }
    }
}
