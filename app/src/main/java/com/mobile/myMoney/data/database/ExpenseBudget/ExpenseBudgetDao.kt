package com.mobile.myMoney.data.database.ExpenseBudget

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseBudgetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpenseBudget(expenseBudget: ExpenseBudget)

    @Update
    suspend fun updateExpenseBudget(expenseBudget: ExpenseBudget)

    @Query("SELECT * FROM expenseBudget_table")
    fun getAllExpenseBudget(): Flow<List<ExpenseBudget>>
}