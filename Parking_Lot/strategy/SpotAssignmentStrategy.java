package strategy;

import model.ParkingFloor;
import model.ParkingSpot;
import model.Vehicle;

import java.util.List;
import java.util.Optional;

/**
 * Strategy interface for finding an available parking spot.
 *
 * DESIGN PATTERN: Strategy
 * Defines the CONTRACT for spot assignment.
 * ParkingLot depends on this interface — never on a concrete implementation.
 *
 * WHY a separate interface for this?
 * Spot assignment logic is likely to change:
 *   - Today: nearest-first (lowest floor, first available)
 *   - Tomorrow: reserved spots for members
 *   - Later: load-balanced across floors, EV priority, handicapped priority
 *
 * Each of these is a new class implementing this interface.
 * ParkingLot and all other classes remain unchanged.
 *
 * SOLID — Open/Closed:
 * ParkingLot is closed for modification but open for extension
 * via new SpotAssignmentStrategy implementations.
 *
 * SOLID — Dependency Inversion:
 * ParkingLot (high-level) depends on this abstraction.
 * NearestSpotStrategy (low-level detail) implements it.
 *
 * Returns Optional<ParkingSpot> — clean contract:
 * - Present  → a spot was found
 * - Empty    → lot is full for this vehicle type
 * No null returns, no exceptions for the "full" case.
 */
public interface SpotAssignmentStrategy {

    /**
     * Finds the best available spot for the given vehicle.
     *
     * IMPORTANT: This method only FINDS a spot — it does NOT park the vehicle.
     * The caller (ParkingLot) is responsible for the atomic find+park operation.
     * This keeps the strategy focused on a single concern: selection logic.
     *
     * @param floors  all floors in order (index 0 = floor 1 = nearest entrance)
     * @param vehicle the vehicle needing a spot
     * @return Optional containing the chosen spot, or empty if none available
     */
    Optional<ParkingSpot> findSpot(List<ParkingFloor> floors, Vehicle vehicle);
}
