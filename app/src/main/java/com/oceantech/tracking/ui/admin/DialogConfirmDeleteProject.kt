package com.oceantech.tracking.ui.admin

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.response.Project

class DialogConfirmDeleteProject (
    private val context: Context,
    private val listener: OnCallBackListenerAdmin,
    private val project: Project
): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(getString(R.string.confirm_action))
        alertDialog.setMessage(getString(R.string.confirm_question))

        alertDialog.setPositiveButton(getString(R.string.confirm)) { _, _ ->
            listener.notifyDelete(project.id!!)
        }

        alertDialog.setNegativeButton(getString(R.string.Cancel)) { dialog, _ ->
            dialog.cancel()
        }

        return alertDialog.create()
    }
}