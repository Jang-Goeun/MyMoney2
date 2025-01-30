package com.mobile.myMoney

import android.app.Application
import com.mobile.myMoney.data.ExpenseBudgetRepository
import com.mobile.myMoney.data.database.Expense.ExpenseDatabase
import com.mobile.myMoney.data.ExpenseRepository
import com.mobile.myMoney.data.NVRepository
import com.mobile.myMoney.data.database.ExpenseBudget.ExpenseBudgetDatabase
import com.mobile.myMoney.data.networt.Book.NVService

class Application : Application() {
    val expenseDatabase by lazy {
        ExpenseDatabase.getDatabase(this)
    }

    val expenseRepo by lazy {
        ExpenseRepository(expenseDatabase.expenseDao())
    }

    val expenseBudgetDatabase by lazy {
        ExpenseBudgetDatabase.getDatabase(this)
    }

    val expenseBudgetRepo by lazy {
        ExpenseBudgetRepository(expenseBudgetDatabase.expenseBudgetDao())
    }
}