package com.oceantech.tracking.ui.client.editTask

interface OnCallBackListenerClient {
    fun notifyFromViewHolder()

    fun notifyAddNewTask(oh: Double, ot: Double, ohContent: String, otContent: String, prjId: String)

    fun notifyDeleteTask()
}