package com.example.mayatimegate.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitClient { // Parte que se conecta a la api


    //10.42.0.1 URL por si usas wifi
    //10.0.2.2 URL si usas cable usb
    private const val URL = "http://10.42.0.1:8069/" //url de la api


    val odooApi: OdooApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OdooApi::class.java)
    }
}