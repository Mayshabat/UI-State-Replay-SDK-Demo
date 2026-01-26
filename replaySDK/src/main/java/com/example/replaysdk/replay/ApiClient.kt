package com.example.replaysdk.replay

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object ApiClient {

    private const val JSON = "application/json; charset=utf-8"

    // ✅ HTTPS default
    private var baseUrl: String = "https://ui-state-replay-sdk.onrender.com/"
    private var api: ApiService? = null

    fun init(baseUrl: String) {
        this.baseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        api = Retrofit.Builder()
            .baseUrl(this.baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun jsonBody(json: String) = json.toRequestBody(JSON.toMediaType())

    fun service(): ApiService {
        return api ?: error("ApiClient not initialized. Call ApiClient.init(baseUrl) first.")
    }
}
