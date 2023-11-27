package com.oceantech.tracking.data.model.response

data class ModifyResponse(
    val timestamp: String,
    val code: Int,
    val message: String,
//    val data: TaskData?,
    val data: Any?,
    val total: Int
)

data class TaskData(
    val createDate: String,
    val createdBy: String,
    val modifyDate: String,
    val modifiedBy: String,
    val id: String,
    val dayOff: Boolean,
    val dateWorking: String,
    val member: Member,
    val tasks: List<Task>
)