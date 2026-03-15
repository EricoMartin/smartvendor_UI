package com.basebox.smartvendor.data.local.dto

import androidx.room.TypeConverter
import com.basebox.smartvendor.data.local.model.ReceiptItem
import com.google.firebase.Timestamp
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class ReceiptConverter {

    private val moshi = Moshi.Builder().build()
    private val listType = Types.newParameterizedType(List::class.java, ReceiptItem::class.java)
    private val adapter = moshi.adapter<List<ReceiptItem>>(listType)

    // Convert List<ReceiptItem> to Json
    @TypeConverter
    fun fromReceiptItemList(items: List<ReceiptItem>?): String {
        return adapter.toJson(items ?: emptyList())
    }

    // Convert Json to List<ReceiptItem>
    @TypeConverter
    fun toReceiptItemList(json: String?): List<ReceiptItem> {
        return json?.let { adapter.fromJson(it) } ?: emptyList()
    }

    // Timestamp → Long
    @TypeConverter
    fun fromTimestamp(timestamp: Timestamp?): Long? {
        return timestamp?.toDate()?.time
    }

    // Long → Timestamp
    @TypeConverter
    fun toTimestamp(value: Long?): Timestamp? {
        return value?.let { Timestamp(it / 1000, ((it % 1000) * 1_000_000).toInt()) }
    }
}
