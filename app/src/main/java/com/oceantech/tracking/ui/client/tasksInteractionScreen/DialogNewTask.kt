package com.oceantech.tracking.ui.client.tasksInteractionScreen

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.oceantech.tracking.R
import com.oceantech.tracking.databinding.DialogNewTaskBinding
import com.oceantech.tracking.utils.checkWhileListening

class DialogNewTask(
    remainTypes: List<String>,
    private val listener: OnCallBackListenerClient,
    private val context: Context
) : DialogFragment() {

    private lateinit var binding: DialogNewTaskBinding
    private val types = remainTypes.toMutableList()
    private var selectedType = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewTaskBinding.inflate(layoutInflater)

        //remove the 1st element since it is used for display current types in EditFragment
        types.removeAt(0)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerNewTaskType.adapter = adapter
        binding.spinnerNewTaskType.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedType = position
            }
        }

        listenToChanges()

        binding.cancelNewTask.setOnClickListener { dismiss() }
        binding.confirmNewTask.setOnClickListener {
            val oh =
                with(binding.etNewOH.text.toString()) { if (this.isEmpty()) 0.0 else this.toDouble() }
            val ot =
                with(binding.etNewOT.text.toString()) { if (this.isEmpty()) 0.0 else this.toDouble() }
            listener.notifyAddNewTask(
                oh,
                ot,
                binding.etNewOHContent.text.toString(),
                binding.etNewOTContent.text.toString(),
                types[selectedType]
            )
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

    private fun listenToChanges() {
        binding.etNewOH.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkAddEnable()

                if (!s.isNullOrEmpty())
                    if (s.toString().toInt() <= 0 || s.toString().toInt() > 8)
                        binding.etNewOH.error = getString(R.string.invalid_time)
                    else
                        binding.etNewOH.error = null
                else binding.etNewOHContent.error = null

                binding.etNewOHContent.isEnabled = !s.isNullOrEmpty()
                binding.tvNewOHTask.text =
                    if (!s.isNullOrEmpty()) getString(R.string.office_task_star) else getString(R.string.office_task)
            }
        })
        binding.etNewOT.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkAddEnable()

                if (!s.isNullOrEmpty())
                    if (s.toString().toInt() <= 0 || s.toString().toInt() > 8)
                        binding.etNewOT.error = getString(R.string.invalid_time)
                    else
                        binding.etNewOT.error = null
                else binding.etNewOTContent.error = null

                binding.etNewOTContent.isEnabled = !s.isNullOrEmpty()
                binding.tvNewOTTask.text =
                    if (!s.isNullOrEmpty()) getString(R.string.overtime_task_star) else getString(R.string.overtime_task)
            }
        })

        binding.etNewOHContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkAddEnable()

                if (s.isNullOrEmpty())
                        binding.etNewOHContent.error = getString(R.string.field_not_empty)
                    else
                        binding.etNewOHContent.error = null
            }
        })

        binding.etNewOTContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkAddEnable()

                if (s.isNullOrEmpty())
                    binding.etNewOTContent.error = getString(R.string.field_not_empty)
                else
                    binding.etNewOTContent.error = null
            }
        })

        binding.etNewOHContent.checkWhileListening(::checkAddEnable)
        binding.etNewOTContent.checkWhileListening(::checkAddEnable)
    }

    private fun checkAddEnable() {
        binding.confirmNewTask.isEnabled =
            (!binding.etNewOH.text.isNullOrEmpty() && !binding.etNewOHContent.text.isNullOrEmpty())
                    || (!binding.etNewOT.text.isNullOrEmpty() && !binding.etNewOTContent.text.isNullOrEmpty())
    }
}