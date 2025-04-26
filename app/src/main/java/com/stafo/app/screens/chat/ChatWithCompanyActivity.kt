package com.stafo.app.screens.chat

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityChatWithCompanyBinding
import com.stafo.app.screens.chat.adapter.CompanyChatAdapter
import com.stafo.app.screens.chat.model.ChatResponse

class ChatWithCompanyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatWithCompanyBinding
    private lateinit var chatAdapter: CompanyChatAdapter
    private val chatList = mutableListOf<ChatResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatWithCompanyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        chatList.add(ChatResponse(0, "Hello, how can I help you?"))
        chatList.add(ChatResponse(1, "I have an issue with my order."))
        chatList.add(ChatResponse(0, "Sure, let me look into it."))
        chatList.add(ChatResponse(0, "Sure."))
        chatList.add(ChatResponse(1, "look into it."))
        chatList.add(ChatResponse(1, "Hello."))

        chatAdapter = CompanyChatAdapter(chatList, this)
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewChat.adapter = chatAdapter
    }
}