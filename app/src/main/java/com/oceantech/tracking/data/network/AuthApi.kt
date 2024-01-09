package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.response.TokenResponse
import com.oceantech.tracking.data.model.UserCredentials
import com.oceantech.tracking.data.model.response.CheckTokenResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("oauth/token")
    fun loginWithRefreshToken(@Body credentials: UserCredentials):Call<TokenResponse>

    @FormUrlEncoded
    @POST("oauth/token")
    fun oauth(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String,
    ): Observable<TokenResponse>

    @POST("oauth/check_token")
    fun checkToken(
        @Query("token") token: String,
        @Header("Authorization") auth: String,
        @Header("Content-Type") accept: String
    ): Observable<CheckTokenResponse>

    companion object {
        const val CLIENT_ID = "core_client" //"core_client"

        const val CLIENT_SECRET = "secret" //"secret"

        const val GRANT_TYPE_PASSWORD = "password"

        const val GRANT_TYPE_REFRESH = "refresh_token"

        const val DEFAULT_SCOPES = "read write delete"
    }
}