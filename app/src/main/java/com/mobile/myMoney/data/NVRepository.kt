package com.mobile.myMoney.data

import com.mobile.myMoney.data.networt.Book.Book
import com.mobile.myMoney.data.networt.Book.NVService

class NVRepository(private val nvService: NVService) {

    suspend fun getBooks(query: String, id: String, secret: String) : List<Book>? {
        return nvService.getBooks(query, id, secret)
    }
}