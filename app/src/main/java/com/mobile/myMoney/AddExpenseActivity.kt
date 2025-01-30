package com.mobile.myMoney

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mobile.myMoney.data.database.Expense.Expense
import com.mobile.myMoney.databinding.ActivityAddExpenseBinding
import com.mobile.myMoney.ui.Expense.ExpenseViewModel
import com.mobile.myMoney.ui.Expense.ExpenseViewModelFactory

class AddExpenseActivity : AppCompatActivity() {

    val TAG = "AddExpenseActivity"

    val binding by lazy {
        ActivityAddExpenseBinding.inflate(layoutInflater)
    }

    val expenseViewModel: ExpenseViewModel by viewModels {
        ExpenseViewModelFactory( (application as Application).expenseRepo )
    }

    private lateinit var spinnerBank: Spinner
    private lateinit var spinnerCategory: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // Spinner 초기화
        spinnerBank = binding.spinnerBank
        val banks = listOf("신한은행", "국민은행", "우리은행", "하나은행", "농협은행", "IBK기업은행", "토스뱅크", "신협은행", "카카오뱅크", "기타")
        spinnerCategory = binding.spinnerCategory
        val categories = listOf("식비", "교통비", "문화/여가비", "기타")

        // Spinner에 데이터 연결
        val bankAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, banks)
        bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBank.adapter = bankAdapter

        val cateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = cateAdapter

        binding.btnAdd.setOnClickListener {
            val bank = spinnerBank.selectedItem.toString()
            val account = binding.edTextAccount.text.toString()
            val date = binding.edTextDate.text.toString()
            val category = spinnerCategory.selectedItem.toString()
            val merchant = binding.edTextStore.text.toString()
            val amount = binding.edTextAmount.text.toString().toIntOrNull()

            // 입력 데이터 검증
            if (account.isBlank() || date.isBlank() || merchant.isBlank() || amount == null) {
                Toast.makeText(this, "모든 필드를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 새로운 Expense 객체 생성
            val expense = Expense(0, bank,account, date,category,merchant,amount)

            // ViewModel을 통해 데이터 추가
            expenseViewModel.addExpense(expense)

            // 저장 완료 메시지
            Toast.makeText(this, "지출 내역이 저장되었습니다.", Toast.LENGTH_SHORT).show()

            // MainActivity로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}