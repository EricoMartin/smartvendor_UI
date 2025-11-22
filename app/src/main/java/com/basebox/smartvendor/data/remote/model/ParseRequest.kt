package com.basebox.smartvendor.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ParseRequest(
    val imageUrl: String,
    val vendorId: String,
    val date: String? = null
)

@JsonClass(generateAdapter = true)
data class ParseResponse(
    val status: String,
    val message: String,
    val data: Map<String, Any>? = null
)