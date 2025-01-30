package com.mobile.myMoney

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mobile.myMoney.data.database.CategoryExpense
import com.mobile.myMoney.databinding.ActivityAnalysisBinding
import com.mobile.myMoney.ui.Expense.ExpenseViewModel
import com.mobile.myMoney.ui.Expense.ExpenseViewModelFactory
import kotlinx.coroutines.launch
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.mobile.myMoney.data.database.ExpenseBudget.ExpenseBudget
import com.mobile.myMoney.ui.ExpenseBudget.ExpenseBudgetViewModel
import com.mobile.myMoney.ui.ExpenseBudget.ExpenseBudgetViewModelFactory

class AnalysisActivity  : AppCompatActivity() {
    val binding by lazy {
        ActivityAnalysisBinding.inflate(layoutInflater)
    }

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var budgetViewModel: ExpenseBudgetViewModel
    var month = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val repository = (application as Application).expenseRepo
        val budgetRepository = (application as Application).expenseBudgetRepo

        // ViewModel 초기화
        val factory = ExpenseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ExpenseViewModel::class.java)

        // ExpenseBudgetViewModel 초기화
        val budgetFactory = ExpenseBudgetViewModelFactory(budgetRepository)
        budgetViewModel = ViewModelProvider(this, budgetFactory).get(ExpenseBudgetViewModel::class.java)

        // 전달받은 인텐트 데이터 가져오기
        val year = intent.getIntExtra("YEAR", 0)
        month = intent.getIntExtra("MONTH", 0)
        val currentYearMonth = "$year${String.format("%02d", month)}"

        // 데이터 불러오기 및 분석
        loadExpenseBudgetAndCompare(currentYearMonth)

        setupToolbar()
    }

    private fun setupToolbar() {
        val drawable = resources.getDrawable(R.drawable.ic_arrow_back, null)
        val scaledDrawable = Bitmap.createScaledBitmap(
            (drawable as BitmapDrawable).bitmap, 30, 30, true
        )
        binding.toolbar.navigationIcon = BitmapDrawable(resources, scaledDrawable)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // 예산과 실제 지출 비교
    private fun loadExpenseBudgetAndCompare(yearMonth: String) {
        // 예산 데이터 관찰
        budgetViewModel.allExpensesBudget.observe(this) { budgetList ->
            val budget = budgetList.firstOrNull() ?: ExpenseBudget(0, 0, 0, 0, 0, 0)

            // 이번 달 지출 데이터 가져오기
            lifecycleScope.launch {
                viewModel.loadCategoryExpenses(yearMonth)
                viewModel.categoryExpenses.collect { expenses ->
                    // 기존 파이 차트 업데이트
                    updatePieChart(expenses, "${month}월 지출")

                    // 예산과 실제 지출 비교
                    compareBudgetWithExpenses(budget, expenses)
                }
            }
        }
    }

    // 예산과 지출 비교 로직
    private fun compareBudgetWithExpenses(budget: ExpenseBudget, expenses: List<CategoryExpense>) {
        val expenseMap = expenses.associate { it.category to it.totalAmount }
        val resultText = StringBuilder()

        // 각 항목 비교
        resultText.append("이번달 지출 분석 결과\n\n")
        compareCategory("식비", budget.food_budget, expenseMap["식비"], resultText)
        compareCategory("교통비", budget.transport_budget, expenseMap["교통비"], resultText)
        compareCategory("문화/여가비", budget.leisure_budget, expenseMap["문화/여가비"], resultText)
        compareCategory("기타", budget.misc_budget, expenseMap["기타"], resultText)

        // 결과 출력
        binding.textView10.text = resultText.toString()
    }

    private fun compareCategory(
        category: String, budget: Int, expense: Float?, resultText: StringBuilder
    ) {
        val actualExpense = (expense ?: 0f).toInt()
        if (actualExpense > budget) {
            resultText.append("$category: 목표 금액 ${budget}원보다 ${actualExpense - budget}원 더 사용하였습니다.\n\n")
        } else {
            resultText.append("$category: 목표 금액 ${budget}원보다 ${budget - actualExpense}원 덜 사용하였습니다.\n\n")
        }
    }

    // 파이 차트 업데이트
    private fun updatePieChart(categoryExpenses: List<CategoryExpense>, chartLabel: String) {
        val pieEntries = categoryExpenses.map { PieEntry(it.totalAmount, it.category) }
        val dataSet = PieDataSet(pieEntries,  chartLabel).apply {
            sliceSpace = 2f
            colors = ColorTemplate.COLORFUL_COLORS.toList()
            valueTextSize = 14f
            valueTextColor = Color.BLACK
        }

        val pieData = PieData(dataSet)
        binding.pieChartCurrent.apply {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = false
            legend.isEnabled = true
            setUsePercentValues(true)
            animateY(1000)
            invalidate()
        }
    }
}