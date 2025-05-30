package com.stafo.app.screens.tms.dataClass

data class AddCommentResponse(
    val status: Boolean,
    val message: String,
    val data: CommentData
)

data class CommentData(
    val task_id: String,
    val employee_id: String,
    val comments: String,
    val updated_at: String,
    val created_at: String,
    val id: Int
)
