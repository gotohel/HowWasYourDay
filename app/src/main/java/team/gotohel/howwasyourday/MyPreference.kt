package team.gotohel.howwasyourday

import android.content.Context

object MyPreference  {

    private const val APPLICATION_PREFS_NAME = "preference_application"
    private fun getAppPreference() = MyApplication.context.getSharedPreferences(APPLICATION_PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Login Preference
     */
    private const val KEY_SAVED_USER_ID = "KEY_SAVED_USER_ID"
    var savedUserId: String?
        get() = getAppPreference().getString(KEY_SAVED_USER_ID, null)
        set(value) = getAppPreference().edit()
            .putString(KEY_SAVED_USER_ID, value)
            .apply()

    private const val KEY_SAVED_USER_NAME = "KEY_SAVED_USER_NAME"
    var savedUserName: String?
        get() = getAppPreference().getString(KEY_SAVED_USER_NAME, null)
        set(value) = getAppPreference().edit()
            .putString(KEY_SAVED_USER_NAME, value)
            .apply()

    private const val KEY_STAY_LOGIN = "KEY_STAY_LOGIN"
    var stayLogin: Boolean
        get() = getAppPreference().getBoolean(KEY_STAY_LOGIN, false)
        set(value) = getAppPreference().edit()
            .putBoolean(KEY_STAY_LOGIN, value)
            .apply()
}