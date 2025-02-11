package com.asl_emp_mng.app.base.request

import com.asl_emp_mng.app.base.model.CompanyInfo
import com.asl_emp_mng.app.base.model.OwnerInfo

data class RegisterRequest(
    val company_info: CompanyInfo,
    val owner_info: OwnerInfo
)
