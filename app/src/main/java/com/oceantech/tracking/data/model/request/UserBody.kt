package com.oceantech.tracking.data.model.request
data class UserBody(
    val username: String,
    val displayName: String = "",
    val email: String,
    val gender: Int,
    val roles: List<String>,
    val password: String,
    val active: Boolean = true,
    val justCreated: Boolean = true,
    val accountNonLocked: Boolean = true,
    val accountNonExpired: Boolean = true,
    val credentialsNonExpired: Boolean = true,
    val confirmPassword: String
)
