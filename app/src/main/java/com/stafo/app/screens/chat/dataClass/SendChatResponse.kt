package com.stafo.app.screens.chat.dataClass

data class SendChatResponse(
    val success: Boolean,
    val message: String,
    val data: ChatData
)

data class ChatData(
    val company_id: String,
    val sender: String,
    val receiver: Int,
    val message: String,
    val message_by: String,
    val updated_at: String,
    val created_at: String,
    val id: Int
)