package com.mobile.myMoney

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.myMoney.data.database.Expense.Expense
import com.mobile.myMoney.databinding.ListItemBinding

class ExpenseAdapter(var expenses: List<Expense>) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var itemLongClickListener: OnItemLongClickListener? = null
    // 버튼 클릭 리스너
    private var itemButtonClickListener: OnItemButtonClickListener? = null

    override fun getItemCount(): Int = expenses.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val itemBinding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.bind(expense)

        // 롱클릭 이벤트
        holder.itemBinding.root.setOnLongClickListener {
            itemLongClickListener?.onItemLongClickListener(it, position)
            true
        }

        // 버튼 클릭 이벤트
        holder.itemBinding.btnDetail.setOnClickListener {
            itemButtonClickListener?.onItemButtonClick(position, expense)
        }
    }

    // ViewHolder
    class ExpenseViewHolder(val itemBinding: ListItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(expense: Expense) {
            itemBinding.textBank.text = expense.expenseBank
            itemBinding.textAccount.text = expense.expenseAccount
            itemBinding.textDate.text = expense.expenseDate
            itemBinding.textCategory.text = expense.expenseCategory
            itemBinding.textMerchant.text = expense.expenseMerchant
            itemBinding.textAmount.text = "${expense.expenseAmount}원"
        }
    }

    // 롱클릭 리스너 인터페이스
    interface OnItemLongClickListener {
        fun onItemLongClickListener(view: View, pos: Int)
    }

    // 버튼 클릭 리스너 인터페이스
    interface OnItemButtonClickListener {
        fun onItemButtonClick(pos: Int, expense: Expense)
    }

    // 롱클릭 리스너 설정 메서드
    fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {
        itemLongClickListener = listener
    }

    // 버튼 클릭 리스너 설정 메서드
    fun setOnItemButtonClickListener(listener: OnItemButtonClickListener?) {
        itemButtonClickListener = listener
    }

    // 데이터 업데이트 메서드
    fun updateExpenses(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }

}
