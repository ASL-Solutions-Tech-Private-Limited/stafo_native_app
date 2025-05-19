package com.stafo.app.utils

import android.content.Context
import android.content.SharedPreferences
import com.orhanobut.hawk.Hawk
import com.stafo.app.screens.auth.dataClass.CompanyData
import com.stafo.app.screens.settings.dataClass.Employee

//Session

private val LOGIN_DETAILS = "user_details"
private const  val isEMPLogin = "is_emp_login"
private const val isCOMPANYLogin = "is_company_login"
private const  val isOnBoardingShown = "is_onboarding_shown"
private val TOKEN = "token"
private val COMPANY_DETAILS = "company_details"
private val EMPLOYEE_DETAILS = "employee_details"
private val COM_ID = "com_id"
private val EMP_BRANCH_ID = "emp_branch_id"
private val isLockSet = "is_lock_set"
private val isLockUserSet = "is_lock_user_set"
private val fbToken = "fb_token"


// SharedPreferences Helper
/*private fun getPrefs(context: Context): SharedPreferences {
    return context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
}

fun setIsOnBoardingScreenShown(context: Context, isShown: Boolean) {
    Hawk.put(isOnBoardingShown, isShown)
    val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    prefs.edit().putBoolean(isOnBoardingShown, isShown).apply()
}

fun isOnBoardingScreenShown(context: Context): Boolean {
    val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    return prefs.getBoolean(isOnBoardingShown, Hawk.get(isOnBoardingShown, false))
}


fun setIsEMPLogin(context: Context,islogin: Boolean) {
    Hawk.put(isEMPLogin, islogin)
    getPrefs(context).edit().putBoolean(isEMPLogin, islogin).apply()
}

fun getIsEMPLogin(context: Context): Boolean {
    return getPrefs(context).getBoolean(isEMPLogin, Hawk.get(isEMPLogin, false))
}


// 🔹 Company Login
fun setIsCOMPANYLogin(context: Context, isLogin: Boolean) {
    Hawk.put(isCOMPANYLogin, isLogin)
    getPrefs(context).edit().putBoolean(isCOMPANYLogin, isLogin).apply()
}

fun getIsCOMPANYLogin(context: Context): Boolean {
    return getPrefs(context).getBoolean(isCOMPANYLogin, Hawk.get(isCOMPANYLogin, false))
}*/
private fun getPrefs(context: Context): SharedPreferences {
    return context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
}

private fun getOnBoardingPrefs(context: Context): SharedPreferences {
    return context.getSharedPreferences("OnBoardingPrefs", Context.MODE_PRIVATE)
}



fun setIsOnBoardingScreenShown(context: Context, isShown: Boolean) {
    getOnBoardingPrefs(context).edit().putBoolean(isOnBoardingShown, isShown).apply()
}
fun isOnBoardingScreenShown(context: Context): Boolean {
    return getOnBoardingPrefs(context).getBoolean(isOnBoardingShown, false)
}
fun setIsEMPLogin(context: Context, isLogin: Boolean) {
    getPrefs(context).edit().putBoolean(isEMPLogin, isLogin).apply()
}
fun getIsEMPLogin(context: Context): Boolean {
    return getPrefs(context).getBoolean(isEMPLogin, false)
}
fun setIsCOMPANYLogin(context: Context, isLogin: Boolean) {
    getPrefs(context).edit().putBoolean(isCOMPANYLogin, isLogin).apply()
}

fun getIsCOMPANYLogin(context: Context): Boolean {
    return getPrefs(context).getBoolean(isCOMPANYLogin, false)
}






/*fun isOnBoardingScreenShown(): Boolean {
    return Hawk.get(isOnBoardingShown, false)
}

fun setIsOnBoardingScreenShown(isOnBoardingShown_: Boolean) {
    Hawk.put(isOnBoardingShown, isOnBoardingShown_)
}*/


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


fun setIsLoggedIn(context: Context, isLoggedIn: Boolean) {
    val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("is_logged_in", isLoggedIn).apply()
}

fun isLoggedIn(context: Context): Boolean {
    val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    return prefs.getBoolean("is_logged_in", false)
}


/*fun getIsLogin(): Boolean? {
    return Hawk.get(isEMPLogin, false)
}

fun setIsCOMPANYLogin(islogin: Boolean) {
    Hawk.put(isCOMPANYLogin, islogin)
}

fun getIsCOMPANYLogin(): Boolean? {
    return Hawk.get(isCOMPANYLogin, false)
}*/

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

fun setFBToken(token: String) {
    Hawk.put(fbToken, token)
}

fun getFBToken(): String? {
    return Hawk.get(fbToken, "")
}