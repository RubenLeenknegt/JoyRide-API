package leafcar.backend.domain

import kotlinx.serialization.Serializable

/**
 * Domeinmodel dat een auto in het systeem voorstelt.
 *
 * Deze klasse wordt gemarkeerd met `@Serializable` zodat instanties automatisch
 * (de)serialiseerbaar zijn via Kotlinx Serialization, bijvoorbeeld voor JSON
 * in API-responses of -requests.
 *
 * @property id Unieke identificatie van de auto (bijv. UUID als `String`).
 * @property brand Merk van de auto (bijv. "Toyota").
 * @property model Modelnaam (bijv. "Yaris").
 * @property buildYear Bouwjaar van de auto (bijv. 2019).
 * @property transmissionType Type transmissie, zie `TransmissionType` (bijv. AUTOMATIC of MANUAL).
 * @property color Kleur van de auto, zie `Color` enum.
 * @property fuelType Brandstoftype, zie `FuelType` (bijv. ELECTRIC, HYBRID, DIESEL, PETROL).
 * @property length Lengte van de auto in millimeters (praktisch voor parkeerruimte).
 * @property width Breedte van de auto in millimeters (praktisch voor parkeerruimte).
 * @property seats Aantal zitplaatsen.
 * @property isofixCompatible Of de auto ISOFIX-bevestigingspunten voor kinderzitjes heeft.
 * @property phoneMount Of er een telefoonhouder aanwezig is.
 * @property luggageSpace Bagageruimte in liters (kan ook als kubieke decimeters geïnterpreteerd worden).
 * @property parkingSensors Of de auto parkeersensoren heeft (voor/achter afhankelijk van implementatie).
 * @property locationX X-coördinaat van de huidige of laatst bekende locatie (bijv. in een lokaal grid of map).
 * @property locationY Y-coördinaat van de huidige of laatst bekende locatie.
 */
@Serializable
data class Car(
    val id: String,
    val brand: String,
    val model: String,
    val buildYear: Int,
    val transmissionType: TransmissionType,
    val color: Color,
    val fuelType: FuelType,
    val length: Int,
    val width: Int,
    val seats: Int,
    val isofixCompatible: Boolean,
    val phoneMount: Boolean,
    val luggageSpace: Double,
    val parkingSensors: Boolean,
    val locationX: Float,
    val locationY: Float
)