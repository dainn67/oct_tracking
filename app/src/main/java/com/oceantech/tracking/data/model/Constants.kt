package com.oceantech.tracking.data.model

class Constants {
    companion object{
        const val TAG = "az"
        val ROWS_LIST = listOf(10, 20, 30, 40, 50)
        val POSITION_LIST = listOf("DEV BE", "DEV FE", "TESTER", "DEV_FULLSTACK")
        val GENDER_LIST = listOf("Male", "Female", "LGBT", "Other")
        val TYPE_LIST = listOf("Leader", "Deputy Leader", "Member")
        val STATUS_LIST = listOf("Staff", "Intern")
        val LEVEL_LIST = listOf("L0", "L1", "L2", "L3", "L4")

        val PROJECT_STATUS_LIST = listOf("Working", "Pending", "Finish")

        const val ROLE_MANAGER = "ROLE_MANAGER"
        const val ROLE_ADMIN = "ROLE_ADMIN"
        const val ROLE_ACCOUNTANT = "ROLE_ACCOUNTANT"
        const val ROLE_STAFF = "ROLE_STAFF"
        val ROLE_LIST = listOf(ROLE_ADMIN, ROLE_ACCOUNTANT, ROLE_STAFF)

        const val POSITION = "POSITION"
        const val STATUS = "STATUS"
        const val LEVEL = "LEVEL"
        const val TYPE = "TYPE"
        const val GENDER = "GENDER"

        const val MALE = "MALE"
        const val FEMALE = "FEMALE"
        const val LGBT = "LGBT"
        const val OTHER_GENDER = "OTHER"
    }
}