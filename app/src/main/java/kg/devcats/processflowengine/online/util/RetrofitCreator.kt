package kg.devcats.processflowengine.online.util

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

object RetrofitCreator {

    fun create(token: String, baseUrl: String): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor {
                val requestBuilder = it.request().newBuilder()
                val newRequest = requestBuilder
                    .header("Authorization", token)
                    .header("Additional-Information", "{\"imei\":\"custom-123\", \"imsi\":\"\" , \"platform\":\"android\", \"msisdn\":\"\"}")
                    .build()
                it.proceed(newRequest)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ResponseConverterFactory(Gson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
    }
}