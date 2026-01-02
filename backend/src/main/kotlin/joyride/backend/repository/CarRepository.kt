package joyride.backend.repository

import joyride.backend.dao.CarEntity
import joyride.backend.dao.CarsTable
import joyride.backend.domain.Car
import joyride.backend.dto.request.CarCpkDataRequest
import joyride.backend.dto.request.CarLocationRequest
import joyride.backend.dto.request.CarTcoDataRequest
import joyride.backend.dto.response.CarListItemResponse
import joyride.backend.mappers.CarMapper
import joyride.backend.mappers.CarMapper.fromDomain
import joyride.backend.mappers.CarMapper.toCarCpkDataRequest
import joyride.backend.mappers.CarMapper.toCarListItemResponse
import joyride.backend.mappers.CarMapper.toCarLocationRequest
import joyride.backend.mappers.CarMapper.toCarTcoDataRequest
import joyride.backend.mappers.CarMapper.toDomain
import joyride.backend.utils.getCoverPhotoUrl
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Repository providing database operations for [Car] entities.
 *
 * Extends [SharedRepository] to reuse common CRUD behavior and adds
 * specific queries and data mappings for Car-related requests.
 */
class CarRepository : SharedRepository<Car>(CarsTable, CarMapper::toCar) {

    // getAll() is inherited from SharedRepository

    /**
     * Retrieves a car by its unique ID.
     *
     * @param id the unique identifier of the car
     * @return a list containing the car if found, or an empty list otherwise
     */
    fun getById(id: String): List<Car> = transaction {
        val car = CarEntity.findById(id)
        if (car != null) listOf(car.toDomain()) else emptyList()
    }

    /**
     * Retrieves location data for all cars.
     *
     * @return a list of [CarLocationRequest] containing basic car location details
     */
    fun getLocations(): List<CarLocationRequest> = transaction {
        CarEntity.all().map { it.toCarLocationRequest() }
    }

    /**
     * Creates a new [Car] record in the database.
     *
     * @param car the car domain object to be persisted
     * @return the created [CarEntity], or null if creation fails
     */
    fun create(car: Car): CarEntity? {
        return try {
            transaction {
                CarEntity.new(car.id) {
                    this.fromDomain(car)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Updates an existing [Car] record.
     *
     * @param car the car domain object with updated values
     * @return the updated [CarEntity], or null if update fails
     */
    fun update(car: Car): CarEntity? {
        return try {
            transaction {
                CarEntity[car.id].apply {
                    this.fromDomain(car)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Deletes a car record by its unique ID.
     *
     * @param id the unique identifier of the car to delete
     */
    fun delete(id: String) = transaction {
        CarEntity[id].delete()
    }

    /**
     * Retrieves total cost of ownership (TCO) data for a car.
     *
     * @param id the car ID
     * @return a [CarTcoDataRequest] object, or null if not found
     */
    fun getTcoData(id: String): CarTcoDataRequest? {
        return try {
            transaction {
                CarEntity.findById(id)?.toCarTcoDataRequest()
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Retrieves cost per kilometer (CPK) data for a car.
     *
     * @param id the car ID
     * @return a [CarCpkDataRequest] object, or null if not found
     */
    fun getCpkData(id: String): CarCpkDataRequest? {
        return try {
            transaction {
                CarEntity.findById(id)?.toCarCpkDataRequest()
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Retrieves a list of cars based on the provided filter parameters.
     *
     * @param params a map where the keys represent filter criteria (e.g., column names) and
     *               the values are lists of strings used as filter values
     * @param baseUrl the base URL used to construct the full URL for the car's cover photo
     * @return a list of car details in the form of [CarListItemResponse], including optional
     *         cover photo URLs
     */
    fun getCarList(
        params: Map<String, List<String>>,
        baseUrl: String
    ): List<CarListItemResponse> =
        transaction {
            findWithFilters(params).map { car ->
                val photoPath = getCoverPhotoUrl(car.id)
                val coverPhotoUrl = photoPath?.let { "$baseUrl/$it" }

                car.toCarListItemResponse(coverPhotoUrl)
            }
        }

    /**
     * Retrieves a list of cars associated with a specific user ID.
     *
     * @param userId the unique identifier of the user whose cars are being retrieved
     * @param baseUrl the base URL used to construct cover photo URLs for the cars
     * @return a list of [CarListItemResponse] objects containing the details of the cars associated with the user,
     *         or an empty list if no cars are found
     */
    fun getCarByUserId(
        userId: String, baseUrl: String
    ): List<CarListItemResponse> = transaction {
        val cars = CarEntity.find { CarsTable.ownerId eq userId }.toList()

        cars.map { car ->
            val photoPath = getCoverPhotoUrl(car.id.value)
            val coverPhotoUrl = photoPath?.let { "$baseUrl/$it" }
            car.toDomain().toCarListItemResponse(coverPhotoUrl)
        }
    }
}
