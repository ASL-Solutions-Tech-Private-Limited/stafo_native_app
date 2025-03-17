package com.stafo.app.screens.settings.dataClass

data class CreateLeavePolicyRequest(
    val holidays: List<LeavePolicyPostData>
)


data class LeavePolicyPostData(
    val name:String,
    val leaveType:String
)