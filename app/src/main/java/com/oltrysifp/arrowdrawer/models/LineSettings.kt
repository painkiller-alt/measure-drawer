package com.oltrysifp.arrowdrawer.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColor
import com.oltrysifp.arrowdrawer.util.log
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class LineSettings (
    var thickness: Float = 5f,
    @Serializable(with = ColorSerializer::class)
    var color: Color = Color.Green,
    val fontSize: Float = 11f,

    var customCoefficient: Float? = null,
    var customSize: Int? = null,
    var customUnit: String? = null
)

object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Color) {
        val string = value.toArgb().toString()
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): Color {
        val string = decoder.decodeString()
        return Color(string.toInt())
    }
}