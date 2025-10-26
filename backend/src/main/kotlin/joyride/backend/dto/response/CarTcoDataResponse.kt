package joyride.backend.dto.response

import kotlinx.serialization.Serializable

/**
 * Response DTO representing the Total Cost of Ownership (TCO) for a car.
 *
 * Provides input parameters used for the TCO calculation and the resulting
 * total ownership cost over the specified usage period.
 *
 * @property id Identifier of the car.
 * @property purchasePrice Initial purchase price of the car (in the application's currency).
 * @property residualValue Estimated residual (sale) value of the car at the end of the usage period (same currency as `purchasePrice`).
 * @property usageYears Number of years the car is expected to be used.
 * @property annualKm Expected kilometers driven per year.
 * @property energyCostPerKm Energy (fuel/electricity) cost expressed as currency per kilometer.
 * @property maintenanceCostPerKm Maintenance cost expressed as currency per kilometer.
 * @property result Calculated total cost of ownership over the full usage period (same currency as `purchasePrice`).
 */

@Serializable
data class CarTcoDataResponse(
    val id: String,
    val purchasePrice: Double,
    val residualValue: Double,
    val usageYears: Int,
    val annualKm: Int,
    val energyCostPerKm: Double,
    val maintenanceCostPerKm: Double,
    val result: Double
)
