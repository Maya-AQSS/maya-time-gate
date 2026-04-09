package com.example.mayatimegate.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitClient { // Parte que se conecta a la api
    private const val URL = "http://10.42.0.1:8069/" //url de la api

    val odooApi: OdooApi by lazy{
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OdooApi::class.java)
    }
}