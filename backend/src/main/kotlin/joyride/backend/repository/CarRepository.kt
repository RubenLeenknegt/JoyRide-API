package joyride.backend.repository

import joyride.backend.dao.CarEntity
import joyride.backend.domain.Car
import joyride.backend.dao.CarsTable
import joyride.backend.repository.SharedRepository
import org.jetbrains.exposed.sql.transactions.transaction
import joyride.backend.mappers.CarMapper.toDomain
import joyride.backend.dto.request.*
import joyride.backend.mappers.CarMapper
import joyride.backend.mappers.CarMapper.fromDomain
import joyride.backend.mappers.CarMapper.toCarCpkDataRequest
import joyride.backend.mappers.CarMapper.toCarLocationRequest
import joyride.backend.mappers.CarMapper.toCarTcoDataRequest

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
}
