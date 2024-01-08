package com.oceantech.tracking.ui.admin.users

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.response.Project
import com.oceantech.tracking.data.model.response.User

class DialogConfirmDeleteUser (
    private val context: Context,
    private val listener: AdminUsersFragment,
    private val user: User
): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(getString(R.string.confirm_action))
        alertDialogBuilder.setMessage(getString(R.string.confirm_question))

        alertDialogBuilder.setPositiveButton(getString(R.string.confirm)) { _, _ ->
            listener.deleteUser(user.id)
        }

        alertDialogBuilder.setNegativeButton(getString(R.string.Cancel)) { dialog, _ ->
            dialog.cancel()
        }

        return alertDialogBuilder.create()
    }
}