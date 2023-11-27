package com.oceantech.tracking.ui.edit

interface OnCallBackListenerClient {
    fun notifyFromViewHolder()

    fun notifyAddNewTask(oh: Double, ot: Double, ohContent: String, otContent: String, prjId: String)

    fun notifyDeleteTask()
}