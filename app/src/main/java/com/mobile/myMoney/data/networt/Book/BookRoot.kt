package com.mobile.myMoney.data.networt.Book

import com.google.gson.annotations.SerializedName

data class BookRoot(
    @SerializedName("items")
    val book: List<Book>
)

// Book dto (item 저장)
data class Book(
    val title: String,
    val author: String,
    val publisher: String,
){
    override fun toString(): String {
        return "$title - $author - $publisher"
    }
}
