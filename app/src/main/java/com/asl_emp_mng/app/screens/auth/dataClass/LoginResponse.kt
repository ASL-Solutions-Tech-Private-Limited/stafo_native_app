package com.asl_emp_mng.app.screens.auth.dataClass

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: Data
)

data class Data(
    val token: String
)
