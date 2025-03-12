package com.asl_emp_mng.app.utils

import com.asl_emp_mng.app.screens.auth.dataClass.CompanyData
import com.asl_emp_mng.app.screens.settings.dataClass.Employee
import com.asl_emp_mng.app.screens.settings.dataClass.EmployeeData
import com.orhanobut.hawk.Hawk

//Session

private val LOGIN_DETAILS = "user_details"
private val isEMPLogin = "is_emp_login"
private val isCOMPANYLogin = "is_company_login"
private val isOnBoardingShown = "is_onboarding_shown"
private val TOKEN = "token"
private val COMPANY_DETAILS = "company_details"
private val EMPLOYEE_DETAILS = "employee_details"
private val COM_ID = "com_id"
private val EMP_BRANCH_ID = "emp_branch_id"
private val isLockSet = "is_lock_set"
private val isLockUserSet = "is_lock_user_set"

fun isOnBoardingScreenShown(): Boolean {
    return Hawk.get(isOnBoardingShown, false)
}

fun setIsOnBoardingScreenShown(isOnBoardingShown_: Boolean) {
    Hawk.put(isOnBoardingShown, isOnBoardingShown_)
}


fun setIsLockUser(isLock: Boolean) {
    Hawk.put(isLockUserSet, isLock)
}


fun getIsLockUser(): Boolean? {
    return Hawk.get(isLockUserSet, false)
}
fun setIsLock(isLock: Boolean) {
    Hawk.put(isLockSet, isLock)
}


fun getIsLock(): Boolean? {
    return Hawk.get(isLockSet, false)
}
fun setIsEMPLogin(islogin: Boolean) {
    Hawk.put(isEMPLogin, islogin)
}

fun getIsLogin(): Boolean? {
    return Hawk.get(isEMPLogin, false)
}

fun setIsCOMPANYLogin(islogin: Boolean) {
    Hawk.put(isCOMPANYLogin, islogin)
}

fun getIsCOMPANYLogin(): Boolean? {
    return Hawk.get(isCOMPANYLogin, false)
}

fun setCompanyDetails(companyDetails: CompanyData) {
    Hawk.put(COMPANY_DETAILS, companyDetails)
}

fun getCompanyDetails(): CompanyData? {
    return Hawk.get(COMPANY_DETAILS, null)
}

fun setEmployeeDetails(employeeDetails: Employee) {
    Hawk.put(EMPLOYEE_DETAILS, employeeDetails)
}

fun getEmployeeDetails(): Employee? {
    return Hawk.get(EMPLOYEE_DETAILS, null)
}

fun setUserAccessToken(token: String) {
    Hawk.put(TOKEN, token)
}

fun getUserAccessToken(): String? {
    return Hawk.get(TOKEN, null)
}

fun setEmployeeComId(com_id: String) {
    Hawk.put(COM_ID, com_id)
}

fun getEmployeeComId(): String? {
    return Hawk.get(COM_ID, null)
}
fun setEmployeeBranchId(emp_branch_id: String) {
    Hawk.put(EMP_BRANCH_ID, emp_branch_id)
}
fun getEmployeeBranchId(): String? {
    return Hawk.get(EMP_BRANCH_ID, null)
}