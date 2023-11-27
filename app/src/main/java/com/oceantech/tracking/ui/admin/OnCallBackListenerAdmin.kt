package com.oceantech.tracking.ui.admin

interface OnCallBackListenerAdmin {
    fun notifyEdit(id: String, code: String, name: String, status: String, desc: String)
    fun notifyAdd(code: String, name: String, status: String, desc: String)
    fun notifyDelete(id: String)
}