package com.stafo.app.screens.tripPlan.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemTripExpensesBinding
import com.stafo.app.screens.tripPlan.dataClass.DataExpenses
import com.stafo.app.screens.tripPlan.dataClass.dashboard.Trips
import com.stafo.app.utils.generateGradientDrawables
import com.stafo.app.utils.getExpenseIcon
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class ExpensesListAdapter(
    private var mContext: Context,
    private var tripData: List<DataExpenses>,
    private val onItemClickListener: (DataExpenses, String) -> Unit
) : RecyclerView.Adapter<ExpensesListAdapter.FollowUpViewHolder>() {
    private val gradientBackgrounds = generateGradientDrawables(4)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowUpViewHolder {
        val binding =
            ItemTripExpensesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowUpViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowUpViewHolder, position: Int) {
        val item = tripData[position]
        val binding = holder.binding

        binding.tvExpenseType.text = "${getExpenseIcon(item.expenseType ?: "")} ${item.expenseType}"
        binding.tvAmount.text = "₹ ${item.amount}"
        binding.tvDate.text = "${formatDateTime(item.updatedAt ?: "")}"
        binding.tvNote.text = item.note

    }

    override fun getItemCount(): Int = tripData.size

    class FollowUpViewHolder(val binding: ItemTripExpensesBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun formatDateTime(isoDateTime: String): String {
        val zonedDateTime = ZonedDateTime.parse(isoDateTime)
        val formatter = DateTimeFormatter.ofPattern("d MMMM yy hh:mm a", Locale.getDefault())
        return zonedDateTime.format(formatter)
    }
}