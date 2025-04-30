package com.stafo.app.screens.rank.dataClass

data class RankListResponse(
    val success: Boolean,
    val message: String,
    val ranklist: List<RankItem>,
    val month: String,
    val year: String,
    val employee_id: String
)

data class RankItem(
    val employee_id: Int,
    val month: Int,
    val total_marks: String,
    val employee: RankEmployee
)

data class RankEmployee(
    val id: Int,
    val emp_id: String,
    val name: String,
    val email: String,
    val phone: String
)

