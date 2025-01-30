package com.asl_emp_mng.app.utils

import com.orhanobut.hawk.Hawk

//Session

private val LOGIN_DETAILS = "user_details"
private val isLogin = "is_login"
private val isOnBoardingShown = "is_onboarding_shown"
private val TOKEN = "token"
public val USERNAME = "user_name"
public val USERPASSWORD = "user_password"
private val FirebaseAuthToken = "firebase_token"
private val STAFFLIST = "staff_list"
private val VOICE_ON_OFF = "voice_on_off"
private val LANGUAGE = "language"

fun isOnBoardingScreenShown(): Boolean {
    return Hawk.get(isOnBoardingShown, false)
}

fun setIsOnBoardingScreenShown(isOnBoardingShown_: Boolean) {
    Hawk.put(isOnBoardingShown, isOnBoardingShown_)
}

fun setIsLogin(islogin: Boolean) {
    Hawk.put(isLogin, islogin)
}

fun getIsLogin(): Boolean? {
    return Hawk.get(isLogin, false)
}

fun setLoginDetails(userDetails: String) {
    Hawk.put(LOGIN_DETAILS, userDetails)
}

fun getLoginDetails(): String? {
    return Hawk.get(LOGIN_DETAILS, null)
}

fun setUserAccessToken(token: String) {
    Hawk.put(TOKEN, token)
}

fun getUserAccessToken(): String? {
    return Hawk.get(TOKEN, null)
}


fun setVoiceOnOff(voiceOnOff: Boolean) {
    Hawk.put(VOICE_ON_OFF, voiceOnOff)
}

fun getVoiceOnOff(): Boolean? {
    return Hawk.get(VOICE_ON_OFF, true)
}

fun setLanguage(language: String) {
    Hawk.put(LANGUAGE, language)
}

fun getLanguage(): String {
    return Hawk.get(LANGUAGE, "hi")
}
