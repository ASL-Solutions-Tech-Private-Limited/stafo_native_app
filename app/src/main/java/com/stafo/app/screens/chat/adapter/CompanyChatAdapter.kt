package com.stafo.app.screens.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ChatItemLayoutBinding
import com.stafo.app.screens.chat.model.ChatResponse


class CompanyChatAdapter (
    private var list: List<ChatResponse>,
    var context: Context
) : RecyclerView.Adapter<CompanyChatAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ChatItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChatItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {

                if (this.checkUser == 0) {
                    binding.otherChatLayout.visibility=View.VISIBLE
                    binding.selfChatLayout.visibility=View.GONE
                    binding.txtOtherChat.text=this.message
                } else {
                    binding.otherChatLayout.visibility=View.GONE
                    binding.selfChatLayout.visibility=View.VISIBLE
                    binding.txtSelfChat.text=this.message
                }


            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}
