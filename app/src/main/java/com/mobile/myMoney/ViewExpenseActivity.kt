package com.mobile.myMoney

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mobile.myMoney.databinding.ActivityViewExpenseBinding
import com.mobile.myMoney.ui.Expense.ExpenseViewModel
import com.mobile.myMoney.ui.Expense.ExpenseViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewExpenseActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityViewExpenseBinding.inflate(layoutInflater)
    }

    val expenseViewModel: ExpenseViewModel by viewModels {
        ExpenseViewModelFactory( (application as Application).expenseRepo )
    }

    private var expenseId: Long = -1L
    private val updateExpenseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // 업데이트된 ID로 데이터 로드
                val updatedExpenseId = result.data?.getLongExtra("expenseId", -1L)
                if (updatedExpenseId != null && updatedExpenseId != -1L) {
                    loadExpenseData(updatedExpenseId)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Intent로 전달된 ID 값
        val expenseId = intent.getLongExtra("_id", -1L)

        if (expenseId == -1L) {
            Toast.makeText(this, "잘못된 데이터입니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadExpenseData(expenseId)

        binding.btnUpdate.setOnClickListener {
            val intent = Intent(this, UpdateExpenseActivity::class.java).apply {
                putExtra("_id", expenseId)
            }
            updateExpenseLauncher.launch(intent)
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
            startActivity(Intent(this, ViewExpenseListActivity::class.java))
            finish()
        }
    }

    private fun loadExpenseData(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val expense = (application as Application).expenseRepo.getExpensesById(id)
            if (expense != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.edTextBank.text = expense.expenseBank
                    binding.edTextAccount.text = expense.expenseAccount
                    binding.edTextDate.text = expense.expenseDate
                    binding.edTextCategory.text = expense.expenseCategory
                    binding.edTextStore.text = expense.expenseMerchant
                    binding.edTextAmount.text = "${expense.expenseAmount}원"
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@ViewExpenseActivity, "데이터를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}