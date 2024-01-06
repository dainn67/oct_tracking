package com.oceantech.tracking.ui.client.tasksInteractionScreen

interface OnCallBackListenerClient {
    fun notifyFromViewHolder()

    fun notifyAddNewTask(oh: Double, ot: Double, ohContent: String, otContent: String, prjId: String)

    fun notifyDeleteTask()
}