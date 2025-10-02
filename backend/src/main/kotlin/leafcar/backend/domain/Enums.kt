package leafcar.backend.domain

import kotlinx.serialization.Serializable

/**
 * Enumeratie van mogelijke transmissietypen voor een auto.
 *
 * Gemarkeerd met `@Serializable` zodat waarden (de)serialiseerbaar zijn via
 * Kotlinx Serialization (bijv. JSON in API-responses en -requests).
 */
@Serializable
enum class TransmissionType {
    /** Handgeschakelde transmissie (koppeling + versnellingspook). */
    MANUAL,

    /** Volledige automaat; schakelt zelf zonder koppeling. */
    AUTOMATIC,

    /** Semi-automaat/DSG; combineert eigenschappen van handmatig en automatisch. */
    SEMI_AUTOMATIC
}

/**
 * Basiskleuren die aan een auto kunnen worden toegekend.
 *
 * Gemarkeerd met `@Serializable` voor eenvoudige (de)serialisatie via Kotlinx Serialization.
 */
@Serializable
enum class Color {
    /** Rood. */
    RED,

    /** Blauw. */
    BLUE,

    /** Zwart. */
    BLACK,

    /** Wit. */
    WHITE,

    /** Overige/ongeclassificeerde kleur (bijv. custom lak of tweekleurig). */
    OTHER
}

/**
 * Brandstoftypen die door het systeem worden herkend.
 *
 * `@Serializable` maakt het mogelijk om enumwaarden direct te (de)serialiseren
 * in transportformaten zoals JSON.
 */
@Serializable
enum class FuelType {
    /** Benzine (gasoline/petrol). */
    PETROL,

    /** Diesel. */
    DIESEL,

    /** Volledig elektrisch (batterij-elektrisch). */
    ELECTRIC,

    /** Hybride (bijv. HEV/PHEV). */
    HYBRID
}