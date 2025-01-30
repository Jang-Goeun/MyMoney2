package com.mobile.myMoney

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mobile.myMoney.data.database.Expense.Expense
import com.mobile.myMoney.databinding.ActivityUpdateExpenseBinding
import com.mobile.myMoney.ui.Expense.ExpenseViewModel
import com.mobile.myMoney.ui.Expense.ExpenseViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UpdateExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateExpenseBinding
    private val expenseViewModel: ExpenseViewModel by viewModels {
        ExpenseViewModelFactory((application as Application).expenseRepo)
    }
    private var expenseId: Long = -1L
    private lateinit var spinnerBank: Spinner
    private lateinit var spinnerCategory: Spinner

    val banks = listOf("신한은행", "국민은행", "우리은행", "하나은행", "농협은행", "IBK기업은행", "토스뱅크", "신협은행", "카카오뱅크", "기타")
    val categories = listOf("식비", "교통비", "문화/여가비", "기타")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로 전달된 데이터 수신 (ID)
        expenseId = intent.getLongExtra("_id", -1L)
        if (expenseId == -1L) {
            Toast.makeText(this, "잘못된 데이터입니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 데이터 로드
        loadExpenseData(expenseId)


        // Spinner 초기화
        spinnerBank = binding.spinnerBank
        spinnerCategory = binding.spinnerCategory

        // Spinner에 데이터 연결
        val bankAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, banks)
        bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBank.adapter = bankAdapter

        val cateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = cateAdapter

        // 저장 버튼 클릭 이벤트
        binding.btnUpdate.setOnClickListener {
            updateExpense()
        }

        // 취소 버튼 클릭 이벤트
        binding.btnCancel.setOnClickListener {
            Toast.makeText(this, "수정이 취소되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadExpenseData(id: Long) {
        // ViewModel을 통해 데이터를 가져오기
        CoroutineScope(Dispatchers.IO).launch {
            val expense = expenseViewModel.searchExpense(id).await()

            if (expense != null) {
                // UI 업데이트는 메인 스레드에서 실행
                CoroutineScope(Dispatchers.Main).launch {
                    // expense.expenseBank 값으로 Spinner의 선택값 설정
                    val bankIndex = banks.indexOf(expense.expenseBank)
                    if (bankIndex >= 0) {
                        binding.spinnerBank.setSelection(bankIndex)
                    }
                    binding.edTextAccount.setText(expense.expenseAccount)
                    binding.edTextDate.setText(expense.expenseDate)
                    // expense.expenseBank 값으로 Spinner의 선택값 설정
                    val categoryIndex = categories.indexOf(expense.expenseCategory)
                    if (categoryIndex >= 0) {
                        binding.spinnerCategory.setSelection(categoryIndex)
                    }
                    binding.edTextStore.setText(expense.expenseMerchant)
                    binding.edTextAmount.setText(expense.expenseAmount.toString())
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@UpdateExpenseActivity, "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun updateExpense() {
        // 입력 데이터 가져오기
        val bank = spinnerBank.selectedItem.toString()
        val account = binding.edTextAccount.text.toString()
        val date = binding.edTextDate.text.toString()
        val category = spinnerCategory.selectedItem.toString()
        val merchant = binding.edTextStore.text.toString()
        val amount = binding.edTextAmount.text.toString().toIntOrNull()

        // 입력 데이터 검증
        if (account.isBlank() || date.isBlank() || merchant.isBlank() || amount == null) {
            Toast.makeText(this, "모든 필드를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 수정된 Expense 객체 생성
        val updatedExpense = Expense(
            _id = expenseId, // 기존 ID 유지
            expenseBank = bank,
            expenseAccount = account,
            expenseDate = date,
            expenseCategory = category,
            expenseMerchant = merchant,
            expenseAmount = amount
        )

        // ViewModel을 통해 데이터 수정
        expenseViewModel.modifyExpense(updatedExpense)

        // 결과를 전달하고 액티비티 종료
        val resultIntent = Intent().apply {
            putExtra("expenseId", updatedExpense._id)
        }
        setResult(RESULT_OK, resultIntent)
        Toast.makeText(this, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
        finish()
    }
}