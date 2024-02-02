package kg.devcats.processflowengine.online.util

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResponseConverterFactory(gson: Gson) : Converter.Factory() {
    private val gsonConverterFactory: GsonConverterFactory = GsonConverterFactory.create(gson)

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        val wrappedType = object : ParameterizedType {
            override fun getActualTypeArguments(): Array<Type> = arrayOf(type)
            override fun getOwnerType(): Type? = null
            override fun getRawType(): Type = Response::class.java
        }
        val gsonConverter: Converter<ResponseBody, *>? = gsonConverterFactory.responseBodyConverter(wrappedType, annotations, retrofit)
        return ResponseBodyConverter(gsonConverter as Converter<ResponseBody, Response<Any?>>)
    }

    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<Annotation>,
                                      methodAnnotations: Array<Annotation>, retrofit: Retrofit): Converter<*, RequestBody>? {
        return gsonConverterFactory.requestBodyConverter(type!!, parameterAnnotations, methodAnnotations, retrofit)
    }


    inner class ResponseBodyConverter<T>(private val converter: Converter<ResponseBody, Response<T?>>) : Converter<ResponseBody, T?> {

        @Throws(IOException::class)
        override fun convert(responseBody: ResponseBody): T? {
            val responseStr = responseBody.string()
            val contentType = responseBody.contentType()
            val response = converter.convert(ResponseBody.create(contentType, responseStr))
            return response?.result as? T
        }
    }
}

class Response<T> {
    @SerializedName("resultCode")
    var status: Status? = null
    @SerializedName("details", alternate = ["detail"])
    var message: String? = null
    @SerializedName("detailCode")
    var detailCode: Int? = null
    @SerializedName("message")
    var notification: String? = null
    var result: T? = null
    var errorCode: Int? = null

    enum class Status(var status: String) {
        SUCCESS("SUCCESS"), FAIL("FAIL"), IN_PROGRESS("IN_PROGRESS")
    }
}
