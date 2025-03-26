package com.stafo.app.base.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.model.DashboardWish
import com.stafo.app.databinding.ItemWishListLayoutBinding
import com.stafo.app.utils.getFormattedDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AdapterWishListFrom(
    private var list: ArrayList<DashboardWish>,
    var context: Context
) : RecyclerView.Adapter<AdapterWishListFrom.ViewHolder>() {
    inner class ViewHolder(val binding: ItemWishListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWishListLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {

                binding.tvName.text = this.name

                if (this.type == "Anniversary") {
                    val dateOfJoining =
                        getFormattedDate(this.date_of_joining, "yyyy-MM-dd", "dd MMM yy")
                    binding.tvDate.text = "$dateOfJoining"
                    binding.ivBirthday.setImageResource(R.drawable.ic_work_aniversary)
                } else {
                    val dateOfJoining =
                        getFormattedDate(this.date_of_birth, "yyyy-MM-dd", "dd MMM yy")
                    binding.tvDate.text = "$dateOfJoining"
                    binding.ivBirthday.setImageResource(R.drawable.ic_emp_birthday)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDate(date: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val formatDate = LocalDate.parse(date, inputFormatter).format(outputFormatter)
        return formatDate
    }

}