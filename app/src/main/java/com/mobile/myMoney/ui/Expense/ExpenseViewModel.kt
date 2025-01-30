package com.mobile.myMoney.ui.Expense

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mobile.myMoney.data.database.Expense.Expense
import com.mobile.myMoney.data.ExpenseRepository
import com.mobile.myMoney.data.database.CategoryExpense
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExpenseViewModel(private val expenseRepo: ExpenseRepository) : ViewModel() {
    val allExpenses : LiveData<List<Expense>> = expenseRepo.allExpenses.asLiveData()
    private val _totalExpense = MutableLiveData<Int>()
    val totalExpense: LiveData<Int> get() = _totalExpense
    private val _categoryExpenses = MutableStateFlow<List<CategoryExpense>>(emptyList())
    val categoryExpenses: StateFlow<List<CategoryExpense>> = _categoryExpenses

    fun loadCategoryExpenses(yearMonth: String) {
        viewModelScope.launch {
            _categoryExpenses.value = expenseRepo.getTotalExpenseByCategory(yearMonth)
        }
    }

    suspend fun totalExpenses(yearMonth: String): Int {
        return expenseRepo.getTotalExpense(yearMonth)
    }

    fun addExpense(expense: Expense) = viewModelScope.launch {
        expenseRepo.addExpense(expense)
    }

    fun searchExpenses( bank: String?, category: String?, startDate: String?, endDate: String?) : Deferred<List<Expense>> {
        val deferredExpenses = viewModelScope.async {
            expenseRepo.searchExpenses(bank, category, startDate, endDate)
        }
        return deferredExpenses
    }

    fun searchExpense(id: Long) : Deferred<Expense> {
        val expense = viewModelScope.async {
            expenseRepo.getExpensesById(id)
        }
        return expense
    }

    fun modifyExpense(expense: Expense) = viewModelScope.launch {
        expenseRepo.modifyExpense(expense)
    }

    fun removeExpense(expense: Expense) = viewModelScope.launch {
        expenseRepo.removeExpense(expense)
    }

    fun getExpensesByCategory(category: String) : Deferred<List<Expense>> {
        val deferredExpenses = viewModelScope.async {
            expenseRepo.getExpensesByCategory(category)
        }
        return deferredExpenses
    }

    fun getExpensesByAccount(account: String) : Deferred<List<Expense>> {
        val deferredExpenses = viewModelScope.async {
            expenseRepo.getExpensesByAccount(account)
        }
        return deferredExpenses
    }



    fun fetchTotalExpense( bank: String?, category: String?, startDate: String?, endDate: String? ) {
        viewModelScope.launch {
            val total = expenseRepo.getTotalExpense(bank, category, startDate, endDate)
            _totalExpense.value = total
        }
    }
}