package com.example.replaysdk.replay

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val JSON = "application/json; charset=utf-8"

    private var baseUrl: String = "https://ui-state-replay-sdk.onrender.com/"
    private var api: ApiService? = null

    /**
     * Initialize the SDK networking layer.
     *
     * @param baseUrl Example:
     *  - Emulator: http://10.0.2.2:5000/
     *  - Device on same network: http://<your-ip>:5000/
     *  - Cloud: https://....onrender.com/
     * @param client Optional custom OkHttpClient for logging/interceptors/headers.
     */
    fun init(baseUrl: String, client: OkHttpClient? = null) {
        this.baseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

        val okHttp = client ?: OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        api = Retrofit.Builder()
            .baseUrl(this.baseUrl)
            .client(okHttp)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun jsonBody(json: String) = json.toRequestBody(JSON.toMediaType())

    fun service(): ApiService {
        return api ?: error("ApiClient not initialized. Call ApiClient.init(baseUrl) first.")
    }
}
