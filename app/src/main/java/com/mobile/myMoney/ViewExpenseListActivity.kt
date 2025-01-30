package com.mobile.myMoney

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.myMoney.data.database.Expense.Expense
import com.mobile.myMoney.databinding.ActivityViewExpenselistBinding
import com.mobile.myMoney.ui.Expense.ExpenseViewModel
import com.mobile.myMoney.ui.Expense.ExpenseViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewExpenseListActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityViewExpenselistBinding.inflate(layoutInflater)
    }

    private val expenseViewModel: ExpenseViewModel by viewModels {
        ExpenseViewModelFactory((application as Application).expenseRepo)
    }

    private lateinit var adapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // RecyclerView 설정
        adapter = ExpenseAdapter(ArrayList())
        binding.recyclerViewExpenses.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewExpenses.adapter = adapter

        val expenseRepo = (application as Application).expenseRepo

        // 은행 및 카테고리 Spinner에 데이터 설정
        val banks = listOf("전체", "신한은행", "국민은행", "우리은행", "하나은행", "농협은행", "IBK기업은행", "토스뱅크", "신협은행", "카카오뱅크", "기타")
        val categories = listOf("전체", "식비", "교통비", "문화/여가비", "기타")

        binding.spinnerBank.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, banks)
        binding.spinnerCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)

        // 초기 데이터 설정
        observeAndUpdateExpenses()

        // 검색 버튼 클릭 이벤트
        binding.btnSearch.setOnClickListener {
            val selectedBank = if (binding.spinnerBank.selectedItem.toString() == "전체") null else binding.spinnerBank.selectedItem.toString()
            val selectedCategory = if (binding.spinnerCategory.selectedItem.toString() == "전체") null else binding.spinnerCategory.selectedItem.toString()
            val startDate = if (binding.etStartDate.text.toString() == "") null else binding.etStartDate.text.toString()
            val endDate = if (binding.etEndDate.text.toString() == "") null else binding.etEndDate.text.toString()

            if (selectedBank != null || selectedCategory != null || startDate != null || endDate != null) {
                fetchAndUpdateFilteredExpenses(selectedBank, selectedCategory, startDate, endDate)
            } else {
                observeAndUpdateExpenses()
            }
        }

        // 버튼 클릭 리스너 설정
        adapter.setOnItemButtonClickListener(object : ExpenseAdapter.OnItemButtonClickListener {
            override fun onItemButtonClick(pos: Int, expense: Expense) {
                // 버튼 클릭 시 처리
                val intent = Intent(this@ViewExpenseListActivity, ViewExpenseActivity::class.java).apply {
                    putExtra("_id", expense._id)
                }
                startActivity(intent)
            }
        })

        // Toolbar 설정
        setupToolbar()
    }

    // Toolbar 설정 함수
    private fun setupToolbar() {
        val drawable = resources.getDrawable(R.drawable.ic_arrow_back, null)
        val scaledDrawable = Bitmap.createScaledBitmap(
            (drawable as BitmapDrawable).bitmap,
            30, // 너비 (px 단위)
            30, // 높이 (px 단위)
            true
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

    // 전체 지출 내역 관찰 및 RecyclerView 업데이트
    private fun observeAndUpdateExpenses() {
        expenseViewModel.allExpenses.observe(this) { expenses ->
            updateRecyclerView(expenses)
        }
    }

    // 검색된 결과 가져와 RecyclerView 업데이트
    private fun fetchAndUpdateFilteredExpenses(
        selectedBank: String?,
        selectedCategory: String?,
        startDate: String?,
        endDate: String?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val filteredExpenses = expenseViewModel.searchExpenses(
                selectedBank,
                selectedCategory,
                startDate,
                endDate
            ).await()

            // 메인 스레드에서 RecyclerView 업데이트
            runOnUiThread {
                if (filteredExpenses.isNotEmpty()) {
                    updateRecyclerView(filteredExpenses)
                } else {
                    Toast.makeText(this@ViewExpenseListActivity, "검색된 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    adapter.updateExpenses(emptyList())
                    binding.tvTotalAmount.text = "총 지출액: 0원"
                }
            }
        }
    }

    // RecyclerView와 총 지출액 업데이트를 위한 공통 함수
    private fun updateRecyclerView(expenses: List<Expense>) {
        adapter.updateExpenses(expenses)
        val totalAmount = expenses.sumOf { it.expenseAmount }
        binding.tvTotalAmount.text = "총 지출액: ${totalAmount}원"
        Log.d(TAG, "총 지출액 업데이트됨: $totalAmount")
    }
}