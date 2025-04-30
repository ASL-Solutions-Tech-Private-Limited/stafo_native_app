package com.stafo.app.screens.chat.dataClass

data class ChatResponse(
    val success: Boolean,
    val message: String,
    val data: List<Chat>
)

data class Chat(
    val id: Int,
    val company_id: Int,
    val sender: Int,
    val receiver: Int,
    val message: String,
    val message_by: String,
    val is_seen_user: Boolean?,
    val is_seen_admin: Boolean?,
    val created_at: String,
    val updated_at: String
)

