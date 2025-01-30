package com.mobile.myMoney.data.networt.Map

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TMapService {
    @GET("tmap/pois/search/around")
    suspend fun searchPOIs(
        @Header("accept") accept: String,
        @Header("appKey") appKey: String,
        @Query("version") version: String,
        @Query("centerLon") centerLon: String,
        @Query("centerLat") centerLat: String,
        @Query("categories") categories: String,
        @Query("page") page: Int,
        @Query("count") count: Int,
        @Query("radius") radius: Int,
        @Query("reqCoordType") reqCoordType: String,
        @Query("resCoordType") resCoordType: String,
        @Query("multiPoint") multiPoint: String
    ): POIResponse


}