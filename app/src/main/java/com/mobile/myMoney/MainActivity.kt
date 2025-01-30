package com.mobile.myMoney

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mobile.myMoney.databinding.ActivityMainBinding
import java.util.Calendar
import android.graphics.Color
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.mobile.myMoney.data.database.CategoryExpense
import com.mobile.myMoney.data.database.ExpenseBudget.ExpenseBudget
import com.mobile.myMoney.data.networt.Book.NVService
import com.mobile.myMoney.ui.Expense.ExpenseViewModel
import com.mobile.myMoney.ui.Expense.ExpenseViewModelFactory
import com.mobile.myMoney.ui.ExpenseBudget.ExpenseBudgetViewModel
import com.mobile.myMoney.ui.ExpenseBudget.ExpenseBudgetViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var budgetViewModel: ExpenseBudgetViewModel
    private lateinit var nvService: NVService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val repository = (application as Application).expenseRepo
        val budgetRepository = (application as Application).expenseBudgetRepo

        // ExpenseViewModel 초기화
        val factory = ExpenseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ExpenseViewModel::class.java)

        // ExpenseBudgetViewModel 초기화
        val budgetFactory = ExpenseBudgetViewModelFactory(budgetRepository)
        budgetViewModel = ViewModelProvider(this, budgetFactory).get(ExpenseBudgetViewModel::class.java)

        nvService = NVService(this)

        lifecycleScope.launch {
            // 지출 예산 데이터 확인
            budgetViewModel.allExpensesBudget.observe(this@MainActivity) { budgets ->
                if (budgets.isEmpty()) {
                    // 저장된 지출 예산이 없다면 초기 값 0으로 데이터 추가
                    val defaultBudget = ExpenseBudget(
                        _id = 0,
                        food_budget = 0,
                        transport_budget = 0,
                        leisure_budget = 0,
                        misc_budget = 0,
                        total_budget = 0
                    )
                    budgetViewModel.addExpenseBudget(defaultBudget)
                }
            }
        }

        // 현재 월을 가져오기
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        binding.txtCardTitle.text = "${month}월 총 지출액"

        var yearMonth = "${year}${String.format("%02d", month)}"

        lifecycleScope.launch {
            binding.txtCardMoney.text = "${viewModel.totalExpenses(yearMonth)}원"
        }

        // 파이 차트 로드
        loadCategoryExpenses(yearMonth)

        // 도서 데이터 불러오기 및 랜덤 추천
        fetchRandomBook()

        // 버튼 클릭 리스너
        binding.btnSetBudgetGoal.setOnClickListener {
            val intent = Intent(this, AnalysisCriteriaActivity::class.java)
            startActivity(intent)
        }

        binding.btnAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }

        binding.btnViewExpenseList.setOnClickListener {
            val intent = Intent(this, ViewExpenseListActivity::class.java)
            startActivity(intent)
        }

        binding.btnAnalysisResult.setOnClickListener {
            val intent = Intent(this, AnalysisActivity::class.java)
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1

            intent.putExtra("YEAR", year)
            intent.putExtra("MONTH", month)
            startActivity(intent)
        }

        binding.btnSearchNearbyMart.setOnClickListener {
            val intent = Intent(this, MapViewActivity::class.java)
            startActivity(intent)
        }
    }

    // 카테고리별 지출 데이터를 불러오기
    private fun loadCategoryExpenses(yearMonth: String) {
        lifecycleScope.launch {
            viewModel.loadCategoryExpenses(yearMonth)
            viewModel.categoryExpenses.collect { categoryExpenses ->
                updatePieChart(categoryExpenses)
            }
        }
    }

    // 파이 차트 업데이트
    private fun updatePieChart(categoryExpenses: List<CategoryExpense>) {
        val pieEntries = categoryExpenses.map { PieEntry(it.totalAmount, it.category) }

        val dataSet = PieDataSet(pieEntries, "").apply {
            sliceSpace = 2f
            colors = ColorTemplate.COLORFUL_COLORS.toList()
            valueTextSize = 14f
            valueTextColor = Color.BLACK
        }

        val pieData = PieData(dataSet)
        binding.pieChartHome.apply {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = false
            legend.isEnabled = true
            setUsePercentValues(true)
            animateY(1000)
            invalidate() // 차트 갱신
        }

        // 상위 3개 카테고리 추출 및 TextView에 표시
        val top3Categories = categoryExpenses
            .take(3) // 이미 정렬된 데이터에서 상위 3개 항목만 가져오기
            .joinToString(", ") { it.category } // 카테고리 이름을 콤마로 연결

        // TextView 업데이트
        binding.txtCardTop3List.text = top3Categories
    }

    // 추천 도서
    private fun fetchRandomBook() {
        val clientID = resources.getString(R.string.naver_client_id)
        val clientSecret = resources.getString(R.string.naver_client_secret)

        lifecycleScope.launch {
            try {
                // 도서 목록 가져오기
                val books = nvService.getBooks("저축", clientID, clientSecret)

                if (books.isNotEmpty()) {
                    // 랜덤으로 도서 1개 선택
                    val randomBook = books[Random.nextInt(books.size)]

                    // 화면에 책 정보 표시
                    binding.txtRandomBook.text = "저축 관련 추천 도서\n" + "${randomBook.title}/" +
                            "저자: ${randomBook.author}"
                } else {
                    binding.txtRandomBook.text = "추천 도서가 없습니다."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "도서를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}