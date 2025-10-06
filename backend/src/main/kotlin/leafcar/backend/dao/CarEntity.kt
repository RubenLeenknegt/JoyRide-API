package leafcar.backend.dao

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass

import leafcar.backend.domain.Car

/**
 * Exposed DAO-entity die één rij in de tabel `Car` (zie `CarsTable`) vertegenwoordigt.
 *
 * Wat is een Exposed `Entity`?
 * - Een `Entity` is een objectgeoriënteerde representatie van een database-rij. Het is
 *   gekoppeld aan een `Table`/`IdTable` (hier: `CarsTable`) en biedt property-toegang
 *   tot kolommen via Kotlin-delegates.
 * - Voordeel: je werkt tegen eigenschappen i.p.v. handmatig SQL te schrijven, terwijl
 *   Exposed de database-interactie en object-cache afhandelt binnen een transactie.
 *
 * Ontwerpkeuzes in deze `CarEntity`:
 * - De properties zijn als `val` gedeclareerd, waardoor deze entity read‑only is vanuit
 *   de applicatielaag. Wil je updates via de DAO doen, gebruik dan `var` voor de velden
 *   die mutabel moeten zijn.
 * - Gebruik `toDomain()` om databasekennis niet te laten lekken naar het domein; de rest
 *   van de applicatie spreekt bij voorkeur in termen van `Car` (domeinmodel).
 */
class CarEntity(id: EntityID<String>) : Entity<String>(id) {
    /** Statische helper van Exposed om te kunnen query’en (bijv. `CarEntity.all()`). */
    companion object : EntityClass<String, CarEntity>(CarsTable)

    /** Merk van de auto (kolom `brand`). */
    val brand by CarsTable.brand

    /** Modelnaam (kolom `model`). */
    val model by CarsTable.model

    /** Bouwjaar (kolom `buildYear`). */
    val buildYear by CarsTable.buildYear

    /** Transmissietype als enum (kolom `transmissionType`). */
    val transmissionType by CarsTable.transmissionType

    /** Kleur als enum (kolom `color`). */
    val color by CarsTable.color

    /** Brandstoftype als enum (kolom `fuelType`). */
    val fuelType by CarsTable.fuelType

    /** Lengte in millimeters (kolom `length`). */
    val length by CarsTable.length

    /** Breedte in millimeters (kolom `width`). */
    val width by CarsTable.width

    /** Aantal zitplaatsen (kolom `seats`). */
    val seats by CarsTable.seats

    /** Of ISOFIX-bevestigingspunten aanwezig zijn (kolom `isofixCompatible`). */
    val isofixCompatible by CarsTable.isofixCompatible

    /** Of er een telefoonhouder aanwezig is (kolom `phoneMount`). */
    val phoneMount by CarsTable.phoneMount

    /** Bagageruimte in liters (kolom `luggageSpace`). */
    val luggageSpace by CarsTable.luggageSpace

    /** Of parkeersensoren aanwezig zijn (kolom `parkingSensors`). */
    val parkingSensors by CarsTable.parkingSensors

    /** X‑coördinaat (kolom `locationX`). */
    val locationX by CarsTable.locationX

    /** Y‑coördinaat (kolom `locationY`). */
    val locationY by CarsTable.locationY

    val licensePlate by CarsTable.licensePlate
}

/**
 * Converteert deze DAO-entity naar het domeinmodel `Car`.
 *
 * Waarom een mapper?
 * - Scheidt database-/Exposed-specifieke details van het domein.
 * - Maakt het eenvoudiger om testdata te bouwen en API’s te consumeren zonder afhankelijk
 *   te zijn van Exposed in de hogere lagen (services/controllers).
 */
fun CarEntity.toDomain(): Car = Car(
    id = this.id.value,
    brand = this.brand,
    model = this.model,
    buildYear = this.buildYear,
    transmissionType = this.transmissionType,
    color = this.color,
    fuelType = this.fuelType,
    length = this.length,
    width = this.width,
    seats = this.seats,
    isofixCompatible = this.isofixCompatible,
    phoneMount = this.phoneMount,
    luggageSpace = this.luggageSpace,
    parkingSensors = this.parkingSensors,
    locationX = this.locationX,
    locationY = this.locationY
)

//#### Tips en vervolgstappen
//- Mutaties: als je create/update use‑cases wilt ondersteunen via de DAO-laag, maak betreffende velden `var` en voeg methoden of service‑logica toe binnen transacties (`transaction { ... }`). Documenteer daarbij validatieregels in de KDoc.
//- Alternatieve mapping: als je van/naar JSON wilt werken in de DAO‑laag, houd dan `toDomain()` lichtgewicht en verplaats serialisatie naar de web-/API‑laag zodat databasecode schoon blijft.
//- Performance: bij bulklezingen kan je gebruikmaken van Exposed DSL met `slice`/`select` indien je niet alle kolommen nodig hebt; documenteer eventuele afwijkende projector‑functies zoals `toSummary()` of `toListItem()` indien je die toevoegt.