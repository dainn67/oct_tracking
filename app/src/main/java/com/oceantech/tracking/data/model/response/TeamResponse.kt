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
    val createDate: String? = null,
    val createdBy: String? = null,
    val modifyDate: String? = null,
    val modifiedBy: String? = null,
    val id: String? = null,
    val name: String,
    val code: String,
    val description: String? = null,
    var members: List<Member>? = listOf()
)