package leafcar.backend.repository

import leafcar.backend.dao.CarEntity
import leafcar.backend.dao.toDomain
import leafcar.backend.domain.Car
import leafcar.backend.dao.CarsTable
import leafcar.backend.dao.CarsTable.brand
import leafcar.backend.dao.CarsTable.transmissionType
import leafcar.backend.domain.Color
import leafcar.backend.domain.FuelType
import leafcar.backend.domain.TransmissionType
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.javatime.JavaLocalDateColumnType
import org.jetbrains.exposed.sql.javatime.JavaLocalDateTimeColumnType
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Repository voor het ophalen en (in de toekomst) opslaan van `Car`-domeinobjecten.
 *
 * Wat is een Repository?
 * - Een Repository is een laag (patroon) die toegang tot data afschermt van de rest van de applicatie.
 *   Code in de service- of use‑case‑laag spreekt tegen een Repository in termen van domeinobjecten (zoals `Car`),
 *   zonder te weten hoe of waar de data werkelijk wordt opgeslagen (database, API, file, enz.).
 * - Voordelen: scheiding van verantwoordelijkheden, makkelijker testen (mocken/stubben), en flexibiliteit om de
 *   onderliggende opslag te vervangen zonder de rest van de code aan te passen.
 *
 * Implementatiedetails van deze `CarRepository`:
 * - Gebruikt JetBrains Exposed (DAO) onder de motorkap: `CarEntity` is de database-entity.
 * - Binnen een database-transactie (`transaction { ... }`) worden entities opgehaald en via `toDomain()` gemapt
 *   naar het domeinmodel `Car`. Zo lekt databasekennis niet door naar het domein.
 * - Deze klasse is bewust klein gehouden; extra methoden zoals `getById`, `create`, `update`, `delete` kunnen
 *   later worden toegevoegd.
 */
class CarRepository {

    /**
     * Haalt alle auto’s op uit de database en geeft ze terug als een lijst van `Car`-domeinobjecten.
     *
     * - Transactie: de query en mapping worden binnen een Exposed `transaction` uitgevoerd.
     * - Mapping: elke `CarEntity` wordt met `toDomain()` vertaald naar een `Car` zodat de rest van de applicatie
     *   geen directe afhankelijkheid heeft van database-entities.
     */
    fun getAll(): List<Car> = transaction {
        CarEntity.all().map { it.toDomain() }
    }

    fun getById(id: String): List<Car> = transaction {
        val car = CarEntity.findById(id)
        if (car != null) listOf(car.toDomain()) else emptyList()
    }

    fun getByBrand(brand: String): List<Car> = transaction {
        CarEntity.find { CarsTable.brand eq brand }.map { it.toDomain()}
    }

    fun findWithFilters(params: Map<String, String>): List<Car> = transaction {
        val conditions = params.mapNotNull { (key, value) ->
            CarsTable.columns.find { it.name == key }?.let { column ->
                when (column.columnType) {
                    is VarCharColumnType, is TextColumnType ->
                        (column as Column<String>) like "%$value%"
                    is IntegerColumnType ->
                        value.toIntOrNull()?.let { (column as Column<Int>) eq it }
                    is LongColumnType ->
                        value.toLongOrNull()?.let { (column as Column<Long>) eq it }
                    is DecimalColumnType ->
                        value.toBigDecimalOrNull()?.let { (column as Column<java.math.BigDecimal>) eq it }
                    is DoubleColumnType ->
                        value.toDoubleOrNull()?.let { (column as Column<Double>) eq it }
                    is FloatColumnType ->
                        value.toFloatOrNull()?.let { (column as Column<Float>) eq it }
                    is BooleanColumnType ->
                        value.toBooleanStrictOrNull()?.let { (column as Column<Boolean>) eq it }
                    is JavaLocalDateColumnType ->
                        runCatching { (column as Column<LocalDate>) eq LocalDate.parse(value) }.getOrNull()
                    is JavaLocalDateTimeColumnType ->
                        runCatching { (column as Column<LocalDateTime>) eq LocalDateTime.parse(value) }.getOrNull()
                    else -> null
                }
            }
        }

        CarsTable
            .select { conditions.reduceOrNull { acc, expr -> acc and expr } ?: Op.TRUE }
            .map { row ->
                Car(
                    id = row[CarsTable.id].value,
                    brand = row[CarsTable.brand],
                    model = row[CarsTable.model],
                    buildYear = row[CarsTable.buildYear],
                    color = Color.valueOf(row[CarsTable.color]),
                    fuelType = FuelType.valueOf(row[CarsTable.fuelType]),
                    length = row[CarsTable.length],
                    width = row[CarsTable.width],
                    seats = row[CarsTable.seats],
                    isofixCompatible = row[CarsTable.isofixCompatible],
                    phoneMount = row[CarsTable.phoneMount],
                    luggageSpace = row[CarsTable.luggageSpace],
                    parkingSensors = row[CarsTable.parkingSensors],
                    transmissionType = TransmissionType.valueOf(row[CarsTable.transmissionType]),
                    locationX = row[CarsTable.locationX],
                    locationY = row[CarsTable.locationY],
                    licensePlate = row[CarsTable.licensePlate],
                    pricePerDay = row[CarsTable.pricePerDay].toDouble(),
                    purchasePrice = row[CarsTable.purchasePrice].toDouble(),
                    residualValue = row[CarsTable.residualValue].toDouble(),
                    usageYears = row[CarsTable.usageYears],
                    annualKm = row[CarsTable.annualKm],
                    energyCostPerKm = row[CarsTable.energyCostPerKm].toDouble(),
                    maintenanceCostPerKm = row[CarsTable.maintenanceCostPerKm].toDouble(),
                )
            }

    }
}

//#### Tips en vervolgstappen
//- Overweeg om een interface te introduceren (bijv. `interface CarRepository`) en deze implementatie `ExposedCarRepository` te noemen. Dat maakt testen (mocks) en wisselen van opslagtechnologie eenvoudiger.
//- Veelgebruikte extra methoden:
//- `fun getById(id: String): Car?`
//- `fun create(car: Car): Car`
//- `fun update(id: String, update: Car): Boolean`
//- `fun delete(id: String): Boolean`
//- Als je soft deletes of paginering nodig hebt, documenteer die contracten ook in de KDoc zodat het gedrag duidelijk is voor consumers van de repository.
//- Als je buiten de repository transactiebeheer wilt doen (bijv. in de service‑laag), kun je Exposed’s `transaction` daar plaatsen en de repository puur de query/mapping laten doen; documenteer dan in de KDoc waar de transactiegrenzen liggen.