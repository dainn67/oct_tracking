package com.oceantech.tracking.ui.admin.projects

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.response.Project

class DialogConfirmDeleteProject (
    private val context: Context,
    private val listener: AdminProjectFragment,
    private val project: Project
): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(getString(R.string.confirm_action))
        alertDialogBuilder.setMessage(getString(R.string.confirm_question))

        alertDialogBuilder.setPositiveButton(getString(R.string.confirm)) { _, _ ->
            listener.notifyDeleteProject(project.id!!)
        }

        alertDialogBuilder.setNegativeButton(getString(R.string.Cancel)) { dialog, _ ->
            dialog.cancel()
        }

        return alertDialogBuilder.create()
    }
}