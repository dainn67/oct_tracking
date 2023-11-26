package com.oceantech.tracking.data.model.response

import java.util.*

data class TeamResponse(
    val timestamp: String,
    val code: Int,
    val message: String,
    val data: TeamData,
    val total: Int
)

data class TeamData(
    val content: List<Team>,
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

data class Team(
    val createDate: String,
    val createdBy: String,
    val modifyDate: String,
    val modifiedBy: String,
    val id: String,
    val name: String,
    val code: String,
    val description: String?,
    val members: List<String>?
)