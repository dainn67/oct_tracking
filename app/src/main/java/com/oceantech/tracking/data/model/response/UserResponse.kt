package com.oceantech.tracking.data.model.response

data class UserResponse (
    val timestamp: String,
    val code: Int,
    val message: String,
    val data: UserDataContent,
    val total: Int
)

data class UserDataContent(
    val content: List<User>,
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