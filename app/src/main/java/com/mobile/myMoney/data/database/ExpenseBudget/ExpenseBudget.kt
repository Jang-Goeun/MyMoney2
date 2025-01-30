package com.mobile.myMoney.data.database.ExpenseBudget

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenseBudget_table")
data class ExpenseBudget(
    @PrimaryKey(autoGenerate = false)
    val _id: Long,

    @ColumnInfo (name="food")
    val food_budget: Int,

    @ColumnInfo (name="transport")
    val transport_budget: Int,

    @ColumnInfo (name="leisure")
    val leisure_budget: Int,

    @ColumnInfo (name="misc")
    val misc_budget: Int,

    @ColumnInfo (name="total")
    val total_budget: Int,
){
    // override toString()
    override fun toString(): String {
        return "$_id, $food_budget, $transport_budget, $leisure_budget, $misc_budget, $total_budget"
    }
}