package com.stafo.app.screens.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ChatItemLayoutBinding
import com.stafo.app.screens.chat.dataClass.Chat
import com.stafo.app.screens.chat.dataClass.ChatResponse
import com.stafo.app.utils.formatUtcTo12HourLocalTimeLegacy


class CompanyChatAdapter (
    private var list: List<Chat>,
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

                if (this.message_by == "company") {
                    binding.otherChatLayout.visibility=View.GONE
                    binding.selfChatLayout.visibility=View.VISIBLE
                    binding.txtSelfChat.text=this.message

                    binding.txtSelfChatTime.text=formatUtcTo12HourLocalTimeLegacy(this.created_at)
                } else {
                    binding.otherChatLayout.visibility=View.VISIBLE
                    binding.selfChatLayout.visibility=View.GONE
                    binding.txtOtherChat.text=this.message
                    binding.txtOtherChatTime.text=formatUtcTo12HourLocalTimeLegacy(this.created_at)
                }


            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}
