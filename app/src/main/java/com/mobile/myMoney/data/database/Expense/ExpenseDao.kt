package com.mobile.myMoney.data.database.Expense

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mobile.myMoney.data.database.CategoryExpense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expense_table ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    // id 지출 내역 검색
    @Query("SELECT * FROM expense_table WHERE _id = :id")
    suspend fun getExpensesById(id: Long) : Expense

    // 카테고리별 지출 리스트
    @Query("SELECT * FROM expense_table WHERE category = :category")
    suspend fun getExpensesByCategory(category: String) : List<Expense>

    // 카테고리별 지출 합계
    @Query("""
    SELECT category, SUM(amount) as totalAmount 
    FROM expense_table 
    WHERE SUBSTR(date, 1, 6) = :yearMonth
    GROUP BY category
    ORDER BY totalAmount DESC
    """)
    suspend fun getTotalExpenseByCategory(yearMonth: String): List<CategoryExpense>

    // 전체 지출 합계
    @Query("""
    SELECT SUM(amount) as totalAmount 
    FROM expense_table 
    WHERE SUBSTR(date, 1, 6) = :yearMonth
    """)
    suspend fun getTotalExpense(yearMonth: String): Int

    // 계좌별 지출 리스트
    @Query("SELECT * FROM expense_table WHERE account = :account")
    suspend fun getExpensesByAccount(account: String) : List<Expense>

    // 검색 조건에 따른 지출 리스트
    @Query("""
        SELECT * FROM expense_table 
        WHERE (:bank IS NULL OR bank = :bank)
          AND (:category IS NULL OR category = :category)
          AND (:startDate IS NULL OR date >= :startDate)
          AND (:endDate IS NULL OR date <= :endDate)
        ORDER BY date DESC
    """)
    suspend fun searchExpenses(bank: String?, category: String?, startDate: String?, endDate: String? ): List<Expense>

    // 검색 조건에 따른 지출액 합계
    @Query("""
        SELECT SUM(amount) FROM expense_table 
        WHERE (:bank IS NULL OR bank = :bank)
          AND (:category IS NULL OR category = :category)
          AND (:startDate IS NULL OR date >= :startDate)
          AND (:endDate IS NULL OR date <= :endDate)
    """)
    suspend fun getTotalExpense( bank: String?, category: String?, startDate: String?, endDate: String? ): Int
}