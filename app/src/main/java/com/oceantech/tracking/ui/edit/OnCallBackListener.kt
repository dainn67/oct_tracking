package com.oceantech.tracking.ui.edit

interface OnCallBackListener {
    fun notifyFromViewHolder()

    fun notifyAddNewTask(oh: Double, ot: Double, ohContent: String, otContent: String, prjId: String)

    fun notifyDeleteTask()
}