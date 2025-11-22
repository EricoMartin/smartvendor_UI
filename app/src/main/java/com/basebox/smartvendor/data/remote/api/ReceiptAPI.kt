package com.basebox.smartvendor.data.remote.api

import com.basebox.smartvendor.data.remote.model.ParseRequest
import com.basebox.smartvendor.data.remote.model.ParseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ReceiptAPI {
    @POST("parse-receipt")
    suspend fun parseReceipt(@Body request: ParseRequest): Response<ParseResponse>
}