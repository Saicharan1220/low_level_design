package fee;

import model.ParkingTicket;

import java.time.LocalDateTime;

/**
 * Strategy interface for parking fee calculation.
 *
 * DESIGN PATTERN: Strategy
 * Defines the contract for fee calculation.
 * ExitGate depends on this interface — not on HourlyFeeCalculator.
 *
 * WHY Strategy here?
 * Fee rules change frequently in real systems:
 *   - Weekday vs weekend rates
 *   - Member discounts
 *   - First-hour-free promotions
 *   - Flat rates for events
 *   - Peak hour surcharges
 *
 * Each variation = a new class implementing FeeCalculator.
 * ExitGate and ParkingLot never change.
 *
 * In Spring Boot, you'd inject the right implementation via
 * @ConditionalOnProperty or a @Bean factory method that reads config.
 */
public interface FeeCalculator {

    /**
     * Calculates the fee for a parking session.
     *
     * @param ticket   issued at entry — contains entryTime and vehicleType
     * @param exitTime when the vehicle is leaving
     * @return fee amount (in currency units)
     */
    double calculateFee(ParkingTicket ticket, LocalDateTime exitTime);
}
