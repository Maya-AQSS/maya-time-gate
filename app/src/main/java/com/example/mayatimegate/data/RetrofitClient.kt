package com.example.mayatimegate.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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

            // Creamos el interceptor para añadir Bearer automaticamente
            val authInterceptor = okhttp3.Interceptor { chain ->
                val requestOriginal = chain.request()
                val tokenOriginal = requestOriginal.header("Authorization")

                val nuevaPeticion = if (tokenOriginal != null && !tokenOriginal.startsWith("Bearer ")) {
                    requestOriginal.newBuilder()
                        .header("Authorization", "Bearer $tokenOriginal")
                        .build()
                } else {
                    requestOriginal
                }
                chain.proceed(nuevaPeticion)
            }

            // Creamos un interceptor de logs para ver las cabeceras en el Logcat
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            }

            // Configuramos OkHttpClient con los interceptores
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()

            // Construimos Retrofit pasándole nuestro cliente personalizado
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(OdooApi::class.java)
    }
}