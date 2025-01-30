package com.mobile.myMoney.data.networt.Book

import android.content.Context
import com.mobile.myMoney.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NVService(val context: Context) {
    private val TAG = "NVService"
    private val service: INaverBook

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(context.resources.getString(R.string.url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(INaverBook::class.java)
    }


    suspend fun getBooks(query: String, clientID: String, clientSecret: String): List<Book> {
        val BookRoot = service.getBooks("json", clientID, clientSecret, query)
        return BookRoot.book
    }
}