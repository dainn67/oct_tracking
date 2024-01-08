package com.oceantech.tracking.ui.client.tasksInteractionScreen

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.oceantech.tracking.R

class DialogConfirmDeleteTask (
    private val context: Context,
    private val listener: OnCallBackListenerClient
): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(getString(R.string.confirm_action))
        alertDialogBuilder.setMessage(getString(R.string.confirm_question))

        alertDialogBuilder.setPositiveButton(getString(R.string.confirm)) { _, _ ->
            listener.notifyDeleteTask()
        }

        alertDialogBuilder.setNegativeButton(getString(R.string.Cancel)) { dialog, _ ->
            dialog.cancel()
        }

        return alertDialogBuilder.create()
    }
}