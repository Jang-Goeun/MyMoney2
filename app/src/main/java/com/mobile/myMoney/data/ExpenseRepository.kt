package com.mobile.myMoney.data

import com.mobile.myMoney.data.database.CategoryExpense
import com.mobile.myMoney.data.database.Expense.Expense
import com.mobile.myMoney.data.database.Expense.ExpenseDao
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val allExpenses : Flow<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun getExpensesByCategory(category: String) : List<Expense> {
        val expenses = expenseDao.getExpensesByCategory(category)
        return expenses
    }

    suspend fun getTotalExpenseByCategory(yearMonth: String): List<CategoryExpense> {
        return expenseDao.getTotalExpenseByCategory(yearMonth)
    }

    suspend fun getTotalExpense(yearMonth: String): Int {
        return expenseDao.getTotalExpense(yearMonth)
    }

    suspend fun getExpensesByAccount(account: String) : List<Expense> {
        val expenses = expenseDao.getExpensesByAccount(account)
        return expenses
    }

    suspend fun searchExpenses( bank: String?, category: String?, startDate: String?, endDate: String?): List<Expense> {
        val expenses = expenseDao.searchExpenses(bank, category, startDate, endDate)
        return expenses
    }

    suspend fun getTotalExpense( bank: String?, category: String?, startDate: String?, endDate: String? ): Int {
        val expense = expenseDao.getTotalExpense(bank, category, startDate, endDate) ?: 0
        return expense
    }

    suspend fun getExpensesById(id: Long) : Expense {
        val expenses = expenseDao.getExpensesById(id)
        return expenses
    }

    suspend fun addExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    suspend fun modifyExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    suspend fun removeExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }
}