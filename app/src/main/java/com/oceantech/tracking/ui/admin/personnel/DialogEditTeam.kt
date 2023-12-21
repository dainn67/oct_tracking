package com.oceantech.tracking.ui.admin.personnel

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.DialogFragment
import com.oceantech.tracking.data.model.response.Team
import com.oceantech.tracking.databinding.DialogEditTeamBinding
import com.oceantech.tracking.ui.admin.OnCallBackListenerAdmin
import com.oceantech.tracking.ui.edit.EditFragment
import com.oceantech.tracking.ui.edit.EditFragment.Companion.setupEditTextBehavior

class DialogEditTeam(
    private val listener: OnCallBackListenerAdmin,
    private val team: Team
) : DialogFragment() {
    private lateinit var binding: DialogEditTeamBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogEditTeamBinding.inflate(layoutInflater)

        binding.etName.hint = team.name
        binding.etCode.hint = team.code
        binding.etDesc.hint = team.description

        setupEditTextBehavior(binding.etCode, ::checkEnabled)
        setupEditTextBehavior(binding.etName, ::checkEnabled)

        binding.cancel.setOnClickListener { dismiss() }
        binding.confirmAdd.setOnClickListener {
            listener.notifyEditTeam(
                team.id!!,
                if(binding.etName.text.isNullOrEmpty()) team.name else binding.etName.text.toString(),
                if(binding.etCode.text.isNullOrEmpty()) team.code else binding.etCode.text.toString(),
                if(binding.etDesc.text.isNullOrEmpty()) team.description ?: "" else binding.etDesc.text.toString(),
            )
            dismiss()
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        return builder.create()
    }

    private fun checkEnabled() {
        binding.confirmAdd.isEnabled =
                    !binding.etCode.text.isNullOrEmpty() ||
                    !binding.etName.text.isNullOrEmpty()||
                    !binding.etDesc.text.isNullOrEmpty()
    }
}