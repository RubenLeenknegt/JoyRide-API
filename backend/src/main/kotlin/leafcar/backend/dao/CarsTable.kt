package leafcar.backend.dao

import leafcar.backend.domain.Color
import leafcar.backend.domain.FuelType
import leafcar.backend.domain.TransmissionType
import org.jetbrains.exposed.dao.id.IdTable

/**
 * Exposed DAO-tabeldefinitie voor het opslaan van auto’s.
 *
 * Wat is een `Table`/`IdTable` in Exposed?
 * - Een Exposed `Table` beschrijft de fysieke databasekolommen (namen, types, beperkingen).
 * - `IdTable<String>` is een variant met een expliciete primaire sleutelkolom (`id`) van type `String`.
 * - De applicatie codeert tegen deze tabel via Exposed DSL/DAO, terwijl het domeinmodel (`Car`) database-vrij blijft.
 *
 * Belangrijke keuzes in deze tabel:
 * - Tabelnaam: "Car" (exacte case kan relevant zijn per database; bij voorkeur consistent houden met migraties/DB-conventies).
 * - `id` is een `VARCHAR(36)` geschikt voor UUID’s in stringvorm.
 * - Enums worden opgeslagen via `enumerationByName` met een maximale lengte van 20 tekens. Zorg dat enum-namen niet langer zijn.
 * - Numerieke kolommen en eenheden:
 *   - `length`, `width` in millimeters (zie domein KDoc).
 *   - `luggageSpace` als `DOUBLE` (liters).
 *   - `locationX`, `locationY` als `FLOAT` (coördinaten in een lokaal grid).
 *
 * Tip: Overweeg indexen op vaak gefilterde kolommen (bijv. `brand`, `model`, `buildYear`) en documenteer migraties apart.
 */
object CarsTable : IdTable<String>("Cars") { // Expliciete tabelnaam "Cars" meegegeven, hoeft eigenlijk niet maar voor duidelijkheid
    /** Primaire sleutel van de auto (UUID als string, max 36 tekens). */
    override val id = varchar("id", 36).entityId()

    /** Merk van de auto; vrije tekst tot 255 tekens. */
    val brand = varchar("brand", 255)

    /** Modelnaam; vrije tekst tot 255 tekens. */
    val model = varchar("model", 255)

    /** Bouwjaar (bijv. 2019). */
    val buildYear = integer("buildYear")

    val transmissionType = varchar("transmissionType", 20)

    val color = varchar("color", 20, )

    val fuelType = varchar("fuelType", 20, )

    /** Lengte in millimeters. */
    val length = integer("length")

    /** Breedte in millimeters. */
    val width = integer("width")

    /** Aantal zitplaatsen. */
    val seats = integer("seats")

    /** Of ISOFIX-bevestigingspunten aanwezig zijn. */
    val isofixCompatible = bool("isofixCompatible")

    /** Of er een telefoonhouder aanwezig is. */
    val phoneMount = bool("phoneMount")

    /** Bagageruimte in liters (double-precisie). */
    val luggageSpace = double("luggageSpace")

    /** Of parkeersensoren aanwezig zijn. */
    val parkingSensors = bool("parkingSensors")

    /** X-coördinaat (float). */
    val locationX = float("locationX")

    /** Y-coördinaat (float). */
    val locationY = float("locationY")

    val licensePlate = varchar("licensePlate", 10)
}

//### Toelichting en vervolgstappen
//- Waarom `enumerationByName`: leest/geschreven worden leesbare enum-namen i.p.v. ordinalen; veiliger bij het herschikken van enumwaarden. Controleer dat de langste enumnaam ≤ 20 tekens blijft.
//- Indexen: als je vaak zoekt op `brand`, `model`, `buildYear` of combinaties daarvan, voeg indexen toe en documenteer ze naast deze KDoc of in je migraties.
//- Migraties: beheer schemawijzigingen via migratietools (Flyway/Liquibase). Deze KDoc beschrijft intentie; de daadwerkelijke aanpassing hoort in een migratiebestand.
//- Consistentie met domein: dit schema correspondeert 1-op-1 met `Car`. Zorg dat mapping in `CarEntity` en `toDomain()` bijgewerkt blijft als kolommen veranderen.