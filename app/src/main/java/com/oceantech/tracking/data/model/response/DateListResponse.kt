package com.oceantech.tracking.data.model.response

import com.google.gson.annotations.SerializedName

data class DateListResponse (
    @SerializedName("timestamp") val timestamp: String?,
    @SerializedName("code") val code: Int?,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: Data?,
    @SerializedName("total") val total: Int?
)

data class Data(
    @SerializedName("content") val content: List<DateObject>?,
    @SerializedName("pageable") val pageable: Pageable?,
    @SerializedName("totalPages") val totalPages: Int?,
    @SerializedName("totalElements") val totalElements: Int?,
    @SerializedName("last") val last: Boolean?,
    @SerializedName("size") val size: Int?,
    @SerializedName("number") val number: Int?,
    @SerializedName("sort") val sort: Sort?,
    @SerializedName("numberOfElements") val numberOfElements: Int?,
    @SerializedName("first") val first: Boolean?,
    @SerializedName("empty") val empty: Boolean?
)

data class DateObject(
    @SerializedName("createDate") val createDate: String?,
    @SerializedName("createdBy") val createdBy: String?,
    @SerializedName("modifyDate") val modifyDate: String?,
    @SerializedName("modifiedBy") val modifiedBy: String?,
    @SerializedName("id") val id: String?,
    @SerializedName("dayOff") var dayOff: Boolean?,
    @SerializedName("dateWorking") val dateWorking: String,
    @SerializedName("member") val member: Member?,
    @SerializedName("tasks") val tasks: MutableList<Task>?
)

data class Task(
    @SerializedName("createDate") val createDate: String? = null,
    @SerializedName("createdBy") val createdBy: String? = null,
    @SerializedName("modifyDate") val modifyDate: String? = null,
    @SerializedName("modifiedBy") val modifiedBy: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("overtimeHour") val overtimeHour: Double,
    @SerializedName("officeHour") val officeHour: Double,
    @SerializedName("taskOffice") val taskOffice: String,
    @SerializedName("taskOverTime") val taskOverTime: String,
    @SerializedName("project") val project: Project
)

data class Project(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String,
    @SerializedName("code") val code: String,
    @SerializedName("status") val status: String,
    @SerializedName("description") val description: String?,
    @SerializedName("tasks") val tasks: List<Task>?
)


data class Member(
    @SerializedName("createDate") val createDate: String? = null,
    @SerializedName("createdBy") val createdBy: String? = null,
    @SerializedName("modifyDate") val modifyDate: String? = null,
    @SerializedName("modifiedBy") val modifiedBy: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String?,
    @SerializedName("code") val code: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("position") val position: String?,
    @SerializedName("level") val level: String?, // Change the type to the actual type if needed
    @SerializedName("status") val status: String?,
    @SerializedName("dateJoin") val dateJoin: String,
    @SerializedName("team") val team: Team, // Change the type to the actual type if needed
    @SerializedName("user") val user: User? = null
)

data class Pageable(
    @SerializedName("sort") val sort: Sort?,
    @SerializedName("offset") val offset: Int?,
    @SerializedName("pageNumber") val pageNumber: Int?,
    @SerializedName("pageSize") val pageSize: Int?,
    @SerializedName("paged") val paged: Boolean?,
    @SerializedName("unpaged") val unpaged: Boolean?
)

data class Sort(
    @SerializedName("empty") val empty: Boolean?,
    @SerializedName("sorted") val sorted: Boolean?,
    @SerializedName("unsorted") val unsorted: Boolean?
)
