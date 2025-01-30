package com.mobile.myMoney.ui.ExpenseBudget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mobile.myMoney.data.ExpenseBudgetRepository

class ExpenseBudgetViewModelFactory(private val repository: ExpenseBudgetRepository) : ViewModelProvider.Factory {
    //ViewModel 객체를 생성하는 함수를 재정의
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 생성하려는 클래스가 ExpenseBudgetViewModel 일 경우 객체 생성
        if (modelClass.isAssignableFrom(ExpenseBudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseBudgetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}