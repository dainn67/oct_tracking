package com.oceantech.tracking.data.model.response

data class MemberResponse(
    val timestamp: String,
    val code: Int,
    val message: String,
    val data: MemberData,
    val total: Int
)

data class MemberData(
    val content: List<Member>,
    val pageable: Pageable,
    val totalPages: Int,
    val totalElements: Int,
    val last: Boolean,
    val size: Int,
    val number: Int,
    val sort: Sort,
    val numberOfElements: Int,
    val first: Boolean,
    val empty: Boolean
)

data class User(
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
    val lastLoginFailures: Any?,
    val lastLoginTime: Any?,
    val totalLoginFailures: Any?,
    val orgId: Any?,
    val roles: List<String>?,
    val authorities: Any?
)