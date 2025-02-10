package com.asl_emp_mng.app.base.model

import java.io.Serializable

data class OwnerInfo(
    val first_name: String,
    val last_name: String,
    val email: String,
    val mobile: String

): Serializable
