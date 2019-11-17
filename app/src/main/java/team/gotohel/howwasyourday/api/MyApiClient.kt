package team.gotohel.howwasyourday.api

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import team.gotohel.howwasyourday.BuildConfig
import team.gotohel.howwasyourday.model.*
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

/**
 * fields -> @Field (메소드 앞에 @FormUrlEncoded 를 붙여야 한다.)
 * parameters -> @Query
 * path -> @Path
 */

class MyApiClient {

    companion object {
        private val BASE_URL_API_SERVER_DEFAULT = "http://env-test.cqemmcnhtr.us-west-2.elasticbeanstalk.com/"

        private const val READ_TIMEOUT_SECONDS = 20
        private const val WRITE_TIMEOUT_SECONDS = 10
        private const val CONNECTION_TIMEOUT_SECONDS = 30

        private var apiClient: MyApiClient? = null
        fun getInstance(): MyApiClient {
            if (apiClient == null) {
                apiClient = MyApiClient()
            }
            return apiClient!!
        }
    }

    interface APIService {
        @GET("search/something")
        fun searchSomething(@Query("key") key: String): Single<String>

        @POST("user/register")
        fun registerUser(@Body postUserRegister: PostUserRegister): Single<User>

        @POST("user/login")
        fun login(@Body postLogin: PostLogin): Single<User>

        @POST("dailylog/upload")
        fun uploadDailyLog(@Body postDailyLog: PostDailyLog): Single<ResDailyLog>

        @POST("dailylog/share")
        fun shareDailyLog(@Body dailyLogSimple: DailyLogSimple): Completable

        @POST("dailylog/analyze")
        fun analyzeDailyLog(@Body dailyLogSimple: DailyLogSimple): Completable

        @GET("dailylog")
        fun getDailyLogs(
            @Query("user_id") user_id: String,
            @Query("date") date: String,
            @Query("page") page: Int,
            @Query("size") size: Int
        ): Single<ResDailyLogList>
    }

    var retrofit: Retrofit
    val call: APIService
    val okHttpClient : OkHttpClient.Builder

    init {
        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(AuthenticationInterceptor())

        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) { // development build
            logging.level = HttpLoggingInterceptor.Level.BODY
        } else { // production build
            logging.level = HttpLoggingInterceptor.Level.BASIC
        }

        okHttpClient.addInterceptor(logging)

        okHttpClient
            .readTimeout(READ_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .connectTimeout(CONNECTION_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)

        //set normal rest adapter
        retrofit = Retrofit.Builder()
            .client(okHttpClient.build())
            .baseUrl(BASE_URL_API_SERVER_DEFAULT)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        call = retrofit.create(APIService::class.java)
    }

    internal class AuthenticationInterceptor : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            try {
                val raw = chain.request()
                val authorized = raw.newBuilder()
                    .addHeader("Sample-Host", "sample_host")
                    .addHeader("Sample-Key", "sample_key")
                    .addHeader("Accept", "application/json")
                    .build()

                val response = chain.proceed(authorized)

                when (response.code()) {
                    401 -> {
//                    Log.d("인증 에러", "authKey : $authKey")
                    }
                    404 -> {
                        // do something
                    }
                }

                return response

            } catch (e: Exception) {

                //공통적인 오류 상황에 대한 안내 처리를 이곳에서 해준다.

                if (e is ConnectException || e is SocketTimeoutException) {
                    //기기가 네트워크에 연결되지 않거나, 서버가 완전히 죽어버린 상황
                    e.printStackTrace()
                    Log.e("APIClient", "Connection Problem")

                } else {
                    e.printStackTrace()
                }

                throw e
            }
        }
    }
}
