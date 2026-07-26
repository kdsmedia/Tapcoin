package com.altomedia.altotap.auth

import android.accounts.AccountManager
import android.content.Context
import android.content.Intent

data class GoogleUser(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null
)

class GoogleAuthManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("alto_auth_prefs", Context.MODE_PRIVATE)

    fun isSignedIn(): Boolean = prefs.getString("user_email", null) != null

    fun getCurrentUser(): GoogleUser? {
        val email = prefs.getString("user_email", null) ?: return null
        return GoogleUser(
            id = email,
            name = prefs.getString("user_name", prettyName(email)) ?: prettyName(email),
            email = email,
            photoUrl = null
        )
    }

    /** Returns the system Google account-picker Intent (no SHA-1 / Cloud Console setup needed). */
    fun getAccountPickerIntent(): Intent =
        AccountManager.newChooseAccountIntent(
            /* selectedAccount = */ null,
            /* allowedAccounts = */ null,
            /* allowedAccountTypes = */ arrayOf("com.google"),
            /* alwaysPromptForAccount = */ false,
            /* descriptionOverrideText = */ null,
            /* addAccountAuthTokenType = */ null,
            /* addAccountRequiredFeatures = */ null,
            /* addAccountOptions = */ null
        )

    /** Persist the chosen Google email to SharedPreferences. */
    fun saveUserFromEmail(email: String) {
        prefs.edit()
            .putString("user_email", email)
            .putString("user_name", prettyName(email))
            .apply()
    }

    fun signOut() {
        prefs.edit().clear().apply()
    }

    /** Turn "john.doe@gmail.com" → "John Doe" */
    private fun prettyName(email: String): String =
        email.substringBefore("@")
            .replace(".", " ")
            .replace("_", " ")
            .split(" ")
            .joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercaseChar() }
            }
}
