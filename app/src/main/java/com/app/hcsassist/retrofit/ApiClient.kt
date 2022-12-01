package com.app.hcsassist.retrofit

import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
//    private const val BASE_URL = "https://developer.shyamfuture.in/hll/public/api/"
    private const val BASE_URL = "https://hcsassist.com/public/api/"
    //private const val FACE_BASE_URL = "http://43.205.105.142:8000/"
    private const val FACE_BASE_URL = "https://api.hcsassist.com/"
    private var retrofit: Retrofit? = null
    val retrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(FACE_BASE_URL)
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



    private fun getRetrofit2(): Retrofit {
        val protocols: ArrayList<Protocol?> = object : ArrayList<Protocol?>() {
            init {
                add(Protocol.HTTP_1_1) // <-- The only protocol used
                //add(Protocol.HTTP_2);
            }
        }
        val httpClient =
            OkHttpClient.Builder()

                .connectTimeout(20L, TimeUnit.SECONDS)
                .writeTimeout  (20L, TimeUnit.SECONDS)
                .readTimeout   (20L, TimeUnit.SECONDS)
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        httpClient.addInterceptor(interceptor)
        return Retrofit.Builder()
            .baseUrl(FACE_BASE_URL).client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build() //Doesn't require the adapter
    }

    val apiService: ApiInterface = getRetrofit().create(ApiInterface::class.java)
    val apiService2: ApiInterface2 = getRetrofit2().create(ApiInterface2::class.java)

}