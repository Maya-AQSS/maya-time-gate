package com.example.mayatimegate.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitClient {
    private const val URL = "http://10.0.2.2:8069/"

    val odooApi: OdooApi by lazy{
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OdooApi::class.java)
    }
}