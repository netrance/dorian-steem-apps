package lee.dorian.steem_data.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SteemWorldClient {

    const val BASE_URL = "https://sds.steemworld.org/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(SteemWorldService::class.java)

}
