package com.mobile.myMoney.ui.ExpenseBudget

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mobile.myMoney.data.ExpenseBudgetRepository
import com.mobile.myMoney.data.database.ExpenseBudget.ExpenseBudget
import kotlinx.coroutines.launch

class ExpenseBudgetViewModel(private val expenseBudgetRepo: ExpenseBudgetRepository) : ViewModel() {
    val allExpensesBudget : LiveData<List<ExpenseBudget>> = expenseBudgetRepo.allExpensesBudget.asLiveData()

    fun addExpenseBudget(expenseBudget: ExpenseBudget) = viewModelScope.launch {
        expenseBudgetRepo.addExpenseBudget(expenseBudget)
    }

    fun modifyExpenseBudget(expenseBudget: ExpenseBudget) = viewModelScope.launch {
        expenseBudgetRepo.modifyExpenseBudget(expenseBudget)
    }
}