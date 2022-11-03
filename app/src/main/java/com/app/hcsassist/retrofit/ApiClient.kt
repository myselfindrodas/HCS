package com.app.hcsassist.retrofit

import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://developer.shyamfuture.in/hll/public/api/"
    private var retrofit: Retrofit? = null
    val retrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }


    private fun getRetrofit(): Retrofit {
        val httpClient =
            OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        httpClient.addInterceptor(interceptor)
        return Retrofit.Builder()
            .baseUrl(BASE_URL).client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build() //Doesn't require the adapter
    }

    val apiService: ApiInterface = getRetrofit().create(ApiInterface::class.java)

}