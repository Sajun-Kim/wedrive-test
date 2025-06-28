package com.namuplanet.base.json

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.io.IOException
import java.math.BigDecimal

class BigDecimalJsonAdapter : JsonAdapter<BigDecimal>() {
    @Throws(IOException::class)
    @FromJson
    override fun fromJson(reader: JsonReader): BigDecimal? {
        try {
            if (reader.peek() == JsonReader.Token.NULL) return reader.nextNull()
            val nextString = reader.nextString()
            if (nextString.isBlank()) return null
            return BigDecimal(nextString)
        } catch (e: JsonDataException) {
            return null
        }
    }

    @Throws(IOException::class)
    @ToJson
    override fun toJson(writer: JsonWriter, value: BigDecimal?) {
        if (value == null) {
            val nullString: String? = null
            writer.value(nullString)
        } else {
            writer.value(value.toPlainString())
        }
    }
}
