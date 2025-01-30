package com.mobile.myMoney

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mobile.myMoney.data.database.ExpenseBudget.ExpenseBudget
import com.mobile.myMoney.databinding.ActivityAnalysisCriteriaBinding
import com.mobile.myMoney.ui.ExpenseBudget.ExpenseBudgetViewModel
import com.mobile.myMoney.ui.ExpenseBudget.ExpenseBudgetViewModelFactory
import kotlinx.coroutines.launch

class AnalysisCriteriaActivity  : AppCompatActivity() {
    val binding by lazy {
        ActivityAnalysisCriteriaBinding.inflate(layoutInflater)
    }
    private lateinit var budgetViewModel: ExpenseBudgetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val repository = (application as Application).expenseBudgetRepo
        val factory = ExpenseBudgetViewModelFactory(repository)
        budgetViewModel = ViewModelProvider(this, factory).get(ExpenseBudgetViewModel::class.java)

        // EditText에 hint 값 설정
        setHintsFromBudget()

        // 저장 버튼 리스너
        binding.btnSaveBudget.setOnClickListener {
            val food = binding.edTextEat.text.toString()
            val transportation = binding.edTextTransportation.text.toString()
            val recreation = binding.edTextRecreation.text.toString()
            val other = binding.edTextOther.text.toString()
            val total = binding.edTextTotal.text.toString()

            if (food.isBlank() || transportation.isBlank() || recreation.isBlank() || other.isBlank() || other.isBlank() || total == null) {
                Toast.makeText(this, "모든 필드를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveUpdatedBudget()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

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

    // 기존 데이터를 EditText의 hint 값으로 설정
    private fun setHintsFromBudget() {
        lifecycleScope.launch {
            budgetViewModel.allExpensesBudget.observe(this@AnalysisCriteriaActivity) { budgets ->
                if (budgets.isNotEmpty()) {
                    val budget = budgets[0] // 첫 번째 데이터 사용
                    binding.edTextEat.hint = "${budget.food_budget}"
                    binding.edTextTransportation.hint = "${budget.transport_budget}"
                    binding.edTextRecreation.hint = "${budget.leisure_budget}"
                    binding.edTextOther.hint = "${budget.misc_budget}"
                    binding.edTextTotal.hint = "${budget.total_budget}"
                }
            }
        }
    }

    // 수정된 예산 데이터를 저장
    private fun saveUpdatedBudget() {
        val updatedBudget = ExpenseBudget(
            _id = 0,
            food_budget = binding.edTextEat.text.toString().toIntOrNull() ?: binding.edTextEat.hint.toString().toInt(),
            transport_budget = binding.edTextTransportation.text.toString().toIntOrNull() ?: binding.edTextTransportation.hint.toString().toInt(),
            leisure_budget = binding.edTextRecreation.text.toString().toIntOrNull() ?: binding.edTextRecreation.hint.toString().toInt(),
            misc_budget = binding.edTextOther.text.toString().toIntOrNull() ?: binding.edTextOther.hint.toString().toInt(),
            total_budget = binding.edTextTotal.text.toString().toIntOrNull() ?: binding.edTextTotal.hint.toString().toInt()
        )

        // 업데이트 실행
        lifecycleScope.launch {
            budgetViewModel.modifyExpenseBudget(updatedBudget)
        }
    }
}