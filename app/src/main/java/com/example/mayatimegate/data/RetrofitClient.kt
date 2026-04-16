package com.example.mayatimegate.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//    //10.42.0.1 URL por si usas wifi
//    //10.0.2.2 URL si usas cable usb
object RetrofitClient {

    private var retrofit: Retrofit? = null
    private var currentUrl: String? = null

    fun getOdooApi(baseUrl: String): OdooApi {
        // Solo creamos una nueva instancia si la URL ha cambiado o es la primera vez
        if (retrofit == null || currentUrl != baseUrl) {
            currentUrl = baseUrl
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl) // Aquí usamos la URL que viene del ViewModel/DataStore
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(OdooApi::class.java)
    }
}