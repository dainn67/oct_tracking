package com.oceantech.tracking.ui.admin.tracking

import android.app.AlertDialog.Builder
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.oceantech.tracking.data.model.response.Task
import com.oceantech.tracking.databinding.DialogTaskDetailBinding

class DialogTaskDetail(
    private val context: Context,
    private val task: Task
) : DialogFragment(){
    private lateinit var binding: DialogTaskDetailBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogTaskDetailBinding.inflate(layoutInflater)

        binding.code.text = task.project.code
        binding.type.text = task.project.name
        binding.etOH.hint = task.officeHour.toString()
        binding.etOT.hint = task.overtimeHour.toString()
        binding.etOHContent.hint = task.taskOffice
        binding.etOTContent.hint = task.taskOverTime

        val builder = Builder(context)
        builder.setView(binding.root)
        return builder.create()
    }
}