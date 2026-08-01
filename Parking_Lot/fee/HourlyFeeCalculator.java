package fee;

import model.ParkingTicket;
import model.enums.VehicleType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Calculates fees based on vehicle type × hours parked.
 *
 * RULES:
 * - Duration = exitTime - entryTime, converted to hours (ceiling)
 * - Minimum charge: 1 hour (even if parked for 5 minutes)
 * - Partial hours round UP: 1hr 10min → charged as 2 hours
 *
 * WHY Map<VehicleType, Double> for rates instead of switch/if-else?
 * - Adding a new VehicleType means adding one map entry here.
 *   No conditional logic to update anywhere else.
 * - Rates can be loaded from external config / database in production.
 * - Easy to override in tests by injecting a custom rate map.
 *
 * INTERVIEW FOLLOW-UP: "How would you add weekend rates?"
 * → Create WeekendFeeCalculator implementing FeeCalculator.
 *   Check exitTime.getDayOfWeek() and apply a multiplier.
 *   Inject it into ExitGate on weekends. Zero changes to existing code.
 *
 * INTERVIEW FOLLOW-UP: "How would you add a first-hour-free promo?"
 * → Create PromotionalFeeCalculator that wraps HourlyFeeCalculator
 *   (Decorator pattern) and subtracts one hour if duration > 1hr.
 */
public class HourlyFeeCalculator implements FeeCalculator {

    private static final Map<VehicleType, Double> DEFAULT_RATES = Map.of(
            VehicleType.MOTORCYCLE,   20.0,
            VehicleType.CAR,          40.0,
            VehicleType.TRUCK,        80.0,
            VehicleType.ELECTRIC_CAR, 50.0  // premium — charging infrastructure cost
    );

    private final Map<VehicleType, Double> ratesPerHour;

    /** Uses default built-in rates. */
    public HourlyFeeCalculator() {
        this.ratesPerHour = DEFAULT_RATES;
    }

    /**
     * Custom rates — useful for testing or config-driven setup.
     * Map.copyOf() makes it immutable — rates cannot change after construction.
     */
    public HourlyFeeCalculator(Map<VehicleType, Double> customRates) {
        this.ratesPerHour = Map.copyOf(customRates);
    }

    @Override
    public double calculateFee(ParkingTicket ticket, LocalDateTime exitTime) {
        Duration duration     = Duration.between(ticket.entryTime(), exitTime);
        long minutes          = duration.toMinutes();
        long hoursCharged     = Math.max(1, (long) Math.ceil(minutes / 60.0));
        double ratePerHour    = ratesPerHour.getOrDefault(ticket.vehicleType(), 40.0);
        double fee            = hoursCharged * ratePerHour;

        System.out.printf("  [FeeCalc] %s | %d min → %d hr(s) @ %.1f/hr = %.2f%n",
                ticket.vehicleType(), minutes, hoursCharged, ratePerHour, fee);

        return fee;
    }
}
