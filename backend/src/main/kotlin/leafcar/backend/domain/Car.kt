package leafcar.backend.domain

import kotlinx.serialization.Contextual
import java.util.UUID
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

//import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
//import java.util.UUID

// Custom UUID serializer
//object UUIDSerializer : KSerializer<UUID> {
//    override val descriptor: SerialDescriptor =
//        PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)
//
//    override fun serialize(encoder: Encoder, value: UUID) {
//        encoder.encodeString(value.toString())
//    }
//
//    override fun deserialize(decoder: Decoder): UUID {
//        return UUID.fromString(decoder.decodeString())
//    }
//}

@Serializable
data class Car(
//    @Serializable(with = UUIDSerializer::class)
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