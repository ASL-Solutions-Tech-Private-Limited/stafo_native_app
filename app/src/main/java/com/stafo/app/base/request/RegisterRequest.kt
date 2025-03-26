package com.stafo.app.base.request

import com.stafo.app.base.model.CompanyInfo
import com.stafo.app.base.model.OwnerInfo

data class RegisterRequest(
    val company_info: CompanyInfo,
    val owner_info: OwnerInfo
)
