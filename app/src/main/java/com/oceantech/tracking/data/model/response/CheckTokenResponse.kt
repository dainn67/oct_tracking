package com.oceantech.tracking.data.model.response

data class CheckTokenResponse(
    val userName: String,
    val scope: List<String>,
    val active: Boolean,
    val exp: Long,
    val user: CheckUser,
    val authorities: List<String>,
    val jti: String,
    val clientId: String
)

data class CheckUser(
    val createDate: String,
    val createdBy: String,
    val modifyDate: String,
    val modifiedBy: String,
    val id: Int,
    val gender: String?,
    val username: String,
    val accountNonExpired: Boolean,
    val accountNonLocked: Boolean,
    val active: Boolean,
    val credentialsNonExpired: Boolean,
    val email: String,
    val phone: String?,
    val justCreated: Boolean,
    val lastLoginFailures: Int?,
    val lastLoginTime: String?,
    val totalLoginFailures: Int?,
    val orgId: String?,
    val roles: List<String>,
    val authorities: List<String>
)