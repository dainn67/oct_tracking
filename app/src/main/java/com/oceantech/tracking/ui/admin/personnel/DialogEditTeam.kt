package com.oceantech.tracking.ui.admin.personnel

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.oceantech.tracking.data.model.response.Team
import com.oceantech.tracking.databinding.DialogEditTeamBinding
import com.oceantech.tracking.utils.checkWhileListening

class DialogEditTeam(
    private val listener: AdminTeamFragment,
    private val team: Team
) : DialogFragment() {
    private lateinit var binding: DialogEditTeamBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogEditTeamBinding.inflate(layoutInflater)

        binding.etName.hint = team.name
        binding.etCode.hint = team.code
        binding.etDesc.hint = team.description

        binding.etCode.checkWhileListening (::checkEnabled)
        binding.etName.checkWhileListening (::checkEnabled)

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

        val alertDialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .create()
            .also {
                it.window?.let {it1 ->
                    it1.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }

        return alertDialog
    }

    private fun checkEnabled() {
        binding.confirmAdd.isEnabled =
                    !binding.etCode.text.isNullOrEmpty() ||
                    !binding.etName.text.isNullOrEmpty()||
                    !binding.etDesc.text.isNullOrEmpty()
    }
}