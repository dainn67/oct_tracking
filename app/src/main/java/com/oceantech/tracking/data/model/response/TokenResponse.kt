package com.oceantech.tracking.data.model.response

import com.google.gson.annotations.SerializedName
import com.oceantech.tracking.data.model.User

data class TokenResponse(

    @SerializedName("access_token")
    var accessToken: String?,

    @SerializedName("refresh_token")
    var refreshToken: String?,

    @SerializedName("expires_in")
    var expiresIn: Int?,

    @SerializedName("user")
    var user: CheckUser
)