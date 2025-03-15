package com.stafo.app.base.model

data class DashboardWish(
    var id: Int,
    var emp_id: String,
    var date_of_birth: String,
    var name: String,
    var email: String,
    var phone: String,
    var image: String?,
    var type: String,
    var date_of_joining: String
)
