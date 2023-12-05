package com.oceantech.tracking.ui.admin

import com.oceantech.tracking.data.model.response.Team

interface OnCallBackListenerAdmin {
    fun notifyEditProject(id: String, code: String, name: String, status: String, desc: String)
    fun notifyAddProject(code: String, name: String, status: String, desc: String)
    fun notifyDeleteProject(id: String)
    fun notifyEditTeam(id: String, name: String, code: String, desc: String)
    fun notifyEditMember(id: String, code: String, dateJoin: String, email: String, gender: String, level: String, name: String, position: String, status: String, team: Team, type: String)
}