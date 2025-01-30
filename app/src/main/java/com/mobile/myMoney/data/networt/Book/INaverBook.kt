package com.mobile.myMoney.data.networt.Book

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface INaverBook {
    @GET("v1/search/book.{type}")
    suspend fun getBooks(
        @Path("type") type:String,
        @Header("X-Naver-Client-Id") clientID: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Query("query") query: String,
        @Query("display") display: Int = 30
    ) : BookRoot
}