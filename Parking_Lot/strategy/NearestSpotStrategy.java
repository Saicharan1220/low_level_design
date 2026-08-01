package strategy;

import model.ParkingFloor;
import model.ParkingSpot;
import model.Vehicle;
import model.enums.SpotType;

import java.util.List;
import java.util.Optional;

/**
 * Assigns the nearest available spot:
 *   - Lowest floor number first (floor 1 = nearest to entrance)
 *   - Within each floor, preferred spot type first (vehicle defines preference order)
 *
 * ALGORITHM:
 *   for each floor (in order, floor 1 first):
 *     for each spotType in vehicle.compatibleSpotTypes() (preference order):
 *       if any available spot exists on this floor for this type:
 *         return it immediately
 *   return empty (lot is full for this vehicle)
 *
 * WHY preference order matters:
 *   Car compatible types: [COMPACT, LARGE]
 *   We try COMPACT first — don't waste a LARGE spot on a Car
 *   when COMPACT spots are still available.
 *
 * KEY DESIGN POINT: This method only SELECTS a spot.
 * It does NOT call spot.park(). That responsibility stays in ParkingLot
 * which wraps find+park in a synchronized block to make them atomic.
 *
 * If findSpot() also parked the vehicle, it would:
 *   1. Violate Single Responsibility (finding + side-effecting)
 *   2. Make the strategy harder to test
 *   3. Tie concurrency control to the strategy — wrong layer
 *
 * MULTIPLE ENTRY GATES — how this scales:
 * With many gates, multiple threads call findSpot() simultaneously.
 * findSpot() itself is stateless and read-only — it can run concurrently
 * without issues. The write (spot.park()) is protected at ParkingLot level.
 * This means findSpot() never blocks — only the final park() step does.
 */
public class NearestSpotStrategy implements SpotAssignmentStrategy {

    @Override
    public Optional<ParkingSpot> findSpot(List<ParkingFloor> floors, Vehicle vehicle) {
        List<SpotType> compatibleTypes = vehicle.compatibleSpotTypes();

        for (ParkingFloor floor : floors) {
            for (SpotType spotType : compatibleTypes) {
                List<ParkingSpot> available = floor.getAvailableSpots(spotType);
                if (!available.isEmpty()) {
                    return Optional.of(available.get(0));
                }
            }
        }

        return Optional.empty();
    }
}
