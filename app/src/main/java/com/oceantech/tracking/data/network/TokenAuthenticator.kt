package com.oceantech.tracking.data.network

import android.content.Context
import android.util.Log
import com.oceantech.tracking.data.model.Constants.Companion.TAG
import com.oceantech.tracking.data.network.RemoteDataSource.Companion.CHECK_TOKEN_AUTH
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    val accessToken: String,
    private val context: Context
) : Authenticator {
    private val maxRetryCount = 20
    private var retryCount = 0

    override fun authenticate(route: Route?, response: Response): Request? {
        if(retryCount >= maxRetryCount) return null

        var newToken = accessToken
        if(response.code == 401 || response.code == 403){
            SessionManager(context).fetchAuthToken()?.let {
                retryCount++
                newToken = it
            }
        }

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newToken")
            .build()
    }
}
