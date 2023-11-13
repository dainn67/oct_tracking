package com.oceantech.tracking.data.repository

import com.google.gson.Gson
import com.oceantech.tracking.data.model.response.TokenResponse
import com.oceantech.tracking.data.network.AuthApi
import com.oceantech.tracking.data.network.AuthApi.Companion.GRANT_TYPE_PASSWORD
import com.oceantech.tracking.ui.security.UserPreferences
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class AuthRepository @Inject constructor(
    val api: AuthApi,
    private val preferences: UserPreferences
) {
    val gson = Gson()

    fun login(username: String, password: String): Observable<TokenResponse> = api.oauth(
//        UserCredentials(CLIENT_ID, CLIENT_SECRET, username, password, null, GRANT_TYPE_PASSWORD)
        username, password, AuthApi.CLIENT_ID, AuthApi.CLIENT_SECRET, GRANT_TYPE_PASSWORD
    ).subscribeOn(Schedulers.io())

    suspend fun saveAccessTokens(tokens: TokenResponse) {
        if (tokens.accessToken == null || tokens.refreshToken == null) return

        val gson = Gson()
        val calendar = Calendar.getInstance()
        tokens.expiresIn?.let { calendar.add(Calendar.SECOND, it) }
        preferences.saveAccessTokens(tokens.accessToken!!, tokens.refreshToken!!, gson.toJson(calendar))
    }
}