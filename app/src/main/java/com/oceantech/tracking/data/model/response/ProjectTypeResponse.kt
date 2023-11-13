package com.oceantech.tracking.data.model.response

data class ProjectTypeResponse(
    val timestamp: String,
    val code: Int,
    val message: String,
    val data: ProjectData
)

data class ProjectData(
    val content: List<Project>,
    val pageable: ProjectPageable,
    val totalPages: Int,
    val totalElements: Int,
    val last: Boolean,
    val size: Int,
    val number: Int,
    val sort: ProjectSort,
    val numberOfElements: Int,
    val first: Boolean,
    val empty: Boolean,
    val total: Int
)

data class ProjectPageable(
    val sort: ProjectSort,
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val unpaged: Boolean
)

data class ProjectSort(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)