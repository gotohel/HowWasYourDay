package team.gotohel.howwasyourday

import android.content.Context

object MyPreference  {

    private const val APPLICATION_PREFS_NAME = "preference_application"
    private fun getAppPreference() = MyApplication.context.getSharedPreferences(APPLICATION_PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Login Preference
     */
    private const val KEY_SAVED_USER_ID = "KEY_SAVED_USER_ID"
    var savedUserId: Int
        get() = getAppPreference().getInt(KEY_SAVED_USER_ID, -1)
        set(value) = getAppPreference().edit()
            .putInt(KEY_SAVED_USER_ID, value)
            .apply()

    private const val KEY_SAVED_USER_EMAIL = "KEY_SAVED_USER_EMAIL"
    var savedUserEmail: String?
        get() = getAppPreference().getString(KEY_SAVED_USER_EMAIL, null)
        set(value) = getAppPreference().edit()
            .putString(KEY_SAVED_USER_EMAIL, value)
            .apply()

    private const val KEY_SAVED_USER_NICKNAME = "KEY_SAVED_USER_NICKNAME"
    var savedUserNickname: String?
        get() = getAppPreference().getString(KEY_SAVED_USER_NICKNAME, null)
        set(value) = getAppPreference().edit()
            .putString(KEY_SAVED_USER_NICKNAME, value)
            .apply()

    private const val KEY_SAVED_USER_PASSWORD = "KEY_SAVED_USER_PASSWORD"
    var savedUserPassword: String?
        get() = getAppPreference().getString(KEY_SAVED_USER_PASSWORD, null)
        set(value) = getAppPreference().edit()
            .putString(KEY_SAVED_USER_PASSWORD, value)
            .apply()

    private const val KEY_STAY_LOGIN = "KEY_STAY_LOGIN"
    var stayLogin: Boolean
        get() = getAppPreference().getBoolean(KEY_STAY_LOGIN, false)
        set(value) = getAppPreference().edit()
            .putBoolean(KEY_STAY_LOGIN, value)
            .apply()
}