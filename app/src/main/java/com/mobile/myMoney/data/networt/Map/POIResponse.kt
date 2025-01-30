package com.mobile.myMoney.data.networt.Map

data class POIResponse(
    val searchPoiInfo: SearchPoiInfo
)

data class SearchPoiInfo(
    val pois: POIs
)

data class POIs(
    val poi: List<POI>
)

data class POI(
    val name: String,
    val noorLat: Double,
    val noorLon: Double
)