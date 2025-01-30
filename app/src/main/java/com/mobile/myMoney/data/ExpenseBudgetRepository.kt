package com.mobile.myMoney.data

import com.mobile.myMoney.data.database.ExpenseBudget.ExpenseBudget
import com.mobile.myMoney.data.database.ExpenseBudget.ExpenseBudgetDao
import kotlinx.coroutines.flow.Flow

class ExpenseBudgetRepository(private val expenseBudgetDao: ExpenseBudgetDao) {
    val allExpensesBudget : Flow<List<ExpenseBudget>> = expenseBudgetDao.getAllExpenseBudget()

    suspend fun addExpenseBudget(expenseBudget: ExpenseBudget) {
        expenseBudgetDao.insertExpenseBudget(expenseBudget)
    }

    suspend fun modifyExpenseBudget(expenseBudget: ExpenseBudget) {
        expenseBudgetDao.updateExpenseBudget(expenseBudget)
    }
}