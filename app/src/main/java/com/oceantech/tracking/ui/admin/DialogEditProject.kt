package com.oceantech.tracking.ui.admin

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.response.Project
import com.oceantech.tracking.databinding.DialogEditProjectBinding

class DialogEditProject(
    private val context: Context,
    private val listener: OnCallBackListenerAdmin,
    private val project: Project? = null
) : DialogFragment() {
    private lateinit var binding: DialogEditProjectBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogEditProjectBinding.inflate(layoutInflater)

        var selectedStatus = 0
        val statuses = listOf("Working", "Pending", "Finish")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = adapter
        binding.spinnerStatus.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedStatus = position
            }
        }

        if (project != null) {
            binding.confirmAdd.isEnabled = true
            binding.title.text = getString(R.string.edit_prj)
            binding.etCode.hint = project.code
            binding.etName.hint = project.name
            binding.etDesc.hint = project.description
            for (i in statuses.indices)
                if (project.status.equals(statuses[i], ignoreCase = true)){
                    selectedStatus = i
                    binding.spinnerStatus.setSelection(i)
                }
        } else {
            binding.title.text = getString(R.string.new_prj)
            binding.etCode.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    checkEnabled()
                }
            })

            binding.etName.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    checkEnabled()
                }
            })
        }



        binding.cancel.setOnClickListener { dismiss() }
        binding.confirmAdd.setOnClickListener {
            if(project != null)
                listener.notifyEdit(
                    project.id!!,
                    if(binding.etCode.text.isNullOrEmpty()) project.code else binding.etCode.text.toString(),
                    if(binding.etName.text.isNullOrEmpty()) project.name else binding.etName.text.toString(),
                    statuses[selectedStatus],
                    if(binding.etDesc.text.isNullOrEmpty()) project.description ?: "" else binding.etDesc.text.toString()
                )
            else listener.notifyAdd(binding.etCode.text.toString(), binding.etName.text.toString(), statuses[selectedStatus], binding.etDesc.text.toString())
            dismiss()
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        return builder.create()
    }

    private fun checkEnabled(){
        binding.confirmAdd.isEnabled = !(binding.etCode.text.isNullOrEmpty() || binding.etName.text.isNullOrEmpty())
    }
}