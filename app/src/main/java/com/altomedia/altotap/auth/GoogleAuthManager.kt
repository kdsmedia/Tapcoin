package com.altomedia.altotap.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.altomedia.altotap.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

data class GoogleUser(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null
)

sealed class AuthResult {
    data class Success(val user: GoogleUser) : AuthResult()
    data object Cancelled : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class GoogleAuthManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("alto_auth_prefs", Context.MODE_PRIVATE)

    fun isSignedIn(): Boolean = prefs.getString("google_id", null) != null

    fun getCurrentUser(): GoogleUser? {
        val id = prefs.getString("google_id", null) ?: return null
        return GoogleUser(
            id = id,
            name = prefs.getString("user_name", "") ?: "",
            email = prefs.getString("user_email", "") ?: "",
            photoUrl = prefs.getString("user_photo", null)
        )
    }

    suspend fun signIn(activityContext: Context): AuthResult {
        return try {
            val webClientId = context.getString(R.string.default_web_client_id)
            val credentialManager = CredentialManager.create(activityContext)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = activityContext,
                request = request
            )

            val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
            val user = GoogleUser(
                id = credential.id,
                name = credential.displayName
                    ?: credential.givenName
                    ?: credential.id.substringBefore("@"),
                email = credential.id,
                photoUrl = credential.profilePictureUri?.toString()
            )
            saveUser(user)
            AuthResult.Success(user)

        } catch (e: GetCredentialCancellationException) {
            AuthResult.Cancelled
        } catch (e: NoCredentialException) {
            AuthResult.Error("Tidak ada akun Google ditemukan di perangkat ini.")
        } catch (e: GetCredentialException) {
            AuthResult.Error("Login gagal: ${e.message}")
        } catch (e: Exception) {
            AuthResult.Error("Terjadi kesalahan: ${e.message}")
        }
    }

    fun signOut() {
        prefs.edit().clear().apply()
    }

    private fun saveUser(user: GoogleUser) {
        prefs.edit()
            .putString("google_id", user.id)
            .putString("user_name", user.name)
            .putString("user_email", user.email)
            .putString("user_photo", user.photoUrl)
            .apply()
    }
}
