package com.mobile.myMoney.data.database.Expense

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_table")
data class Expense(
    @PrimaryKey (autoGenerate = true)
    val _id: Long,

    @ColumnInfo (name="bank")
    val expenseBank: String,

    @ColumnInfo (name="account")
    val expenseAccount: String,

    @ColumnInfo (name="date")
    val expenseDate: String,

    @ColumnInfo (name="category")
    val expenseCategory: String,

    @ColumnInfo (name="merchant")
    val expenseMerchant: String,

    @ColumnInfo (name="amount")
    val expenseAmount: Int
){
    // override toString()
    override fun toString(): String {
        return "$_id, $expenseBank: $expenseAccount, date: ${expenseDate}, category: ${expenseCategory}, merchant: ${expenseMerchant}, amount: ${expenseAmount}"
    }
}