package com.stafo.app.screens.recharge.dataclass

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stafo.app.base.adapter.ActionsListAdapter
import com.stafo.app.databinding.ItemContactBinding
import com.stafo.app.screens.recharge.PlanActivity
import com.stafo.app.utils.generateTextBitmap

class ContactsAdapter(
    private var contactList: List<Contact>,
    var context: Activity,
    var listener: ContactsClickListener
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(contactList[position]) {
                binding.tvName.text = this.name
                binding.tvPhone.text = this.phone


                val placeholderBitmap = generateTextBitmap(this.name ?: "?")
                Glide.with(context)
                    .load(placeholderBitmap)
                    .into(binding.sivTitle)


                itemView.setOnClickListener { listener.onActionClick(phone) }


            }
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    interface ContactsClickListener {
        fun onActionClick(action: String)

    }

    fun updateList(newList: List<Contact>) {
        contactList = newList
        notifyDataSetChanged()
    }

}
