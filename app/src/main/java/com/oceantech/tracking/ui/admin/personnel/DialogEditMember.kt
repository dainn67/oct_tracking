package com.oceantech.tracking.ui.admin.personnel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.Constants.Companion.GENDER
import com.oceantech.tracking.data.model.Constants.Companion.GENDER_LIST
import com.oceantech.tracking.data.model.Constants.Companion.LEVEL
import com.oceantech.tracking.data.model.Constants.Companion.LEVEL_LIST
import com.oceantech.tracking.data.model.Constants.Companion.POSITION
import com.oceantech.tracking.data.model.Constants.Companion.POSITION_LIST
import com.oceantech.tracking.data.model.Constants.Companion.STATUS
import com.oceantech.tracking.data.model.Constants.Companion.STATUS_LIST
import com.oceantech.tracking.data.model.Constants.Companion.TYPE
import com.oceantech.tracking.data.model.Constants.Companion.TYPE_LIST
import com.oceantech.tracking.data.model.response.Member
import com.oceantech.tracking.databinding.DialogEditMemberBinding
import com.oceantech.tracking.ui.client.tasksInteractionScreen.TaskInteractionFragment.Companion.setupEditTextBehavior

@SuppressLint("SetTextI18n")
class DialogEditMember(
    private val context: Context,
    private val listener: AdminMemberFragment,
    private val member: Member
) : DialogFragment() {
    private lateinit var binding: DialogEditMemberBinding

    private var initPosition = 0
    private var initGender = 0
    private var initType = 0
    private var initStatus = 0
    private var initLevel = 0

    private var selectedPosition = 0
    private var selectedGender = 0
    private var selectedType = 0
    private var selectedStatus = 0
    private var selectedLevel = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogEditMemberBinding.inflate(layoutInflater)

        binding.tvJoinDate.text = "${getString(R.string.join)}: ${member.dateJoin}"
        binding.tvTeam.text = "${getString(R.string.team)}: ${member.team?.name}"
        binding.tvCode.text = "${getString(R.string.code)}: ${member.code}"
        setupEditTexts()
        setupSpinners()

        binding.cancel.setOnClickListener { dismiss() }
        binding.confirmAdd.setOnClickListener {
            listener.notifyEditMember(
                member.id!!,
                member.code!!,
                member.dateJoin!!,
                if (binding.etEmail.text.isNullOrEmpty()) member.email!! else binding.etEmail.text.toString(),
                GENDER_LIST[selectedGender].toUpperCase(),
                LEVEL_LIST[selectedLevel],
                if (binding.etName.text.isNullOrEmpty()) member.name!! else binding.etName.text.toString(),
                POSITION_LIST[selectedPosition].replace(" ", "_"),
                if (STATUS_LIST[selectedStatus].equals("INTERN", ignoreCase = true)) "INTERNSHIP" else "STAFF",
                member.team,
                TYPE_LIST[selectedType].toUpperCase()
            )
            dismiss()
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        return builder.create()
    }

    private fun setupEditTexts() {
        binding.etName.hint = member.name
        binding.etEmail.hint = member.email

        setupEditTextBehavior(binding.etName, ::checkEnabled)
        setupEditTextBehavior(binding.etEmail, ::checkEnabled)
    }

    private fun setupSpinners() {

        setupSpinnerBehavior(binding.spinnerPosition, ::checkEnabled, POSITION)
        setupSpinnerBehavior(binding.spinnerGender, ::checkEnabled, GENDER)
        setupSpinnerBehavior(binding.spinnerType, ::checkEnabled, TYPE)
        setupSpinnerBehavior(binding.spinnerStatus, ::checkEnabled, STATUS)
        setupSpinnerBehavior(binding.spinnerSkillLevel, ::checkEnabled, LEVEL)
    }

    private fun setupSpinnerBehavior(
        spinner: Spinner,
        operation: () -> Unit,
        type: String
    ) {
        val options =  when (type){
            LEVEL -> LEVEL_LIST
            STATUS -> STATUS_LIST
            TYPE -> TYPE_LIST
            GENDER -> GENDER_LIST
            POSITION -> POSITION_LIST
            else -> emptyList()
        }
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val ref = when (type){
            LEVEL -> member.level
            STATUS -> member.status
            TYPE -> member.type
            GENDER -> member.gender
            POSITION -> member.position
            else -> ""
        }

        for (i: Int in options.indices) if (ref.equals(options[i], ignoreCase = true)) {
            initLevel = i
            spinner.setSelection(i)
        }

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (type) {
                    LEVEL -> selectedLevel = position
                    STATUS -> selectedStatus = position
                    TYPE -> selectedType = position
                    GENDER -> selectedGender = position
                    POSITION -> selectedPosition = position
                }
                operation()
            }
        }
    }

    private fun checkEnabled() {
        binding.confirmAdd.isEnabled =
            !binding.etName.text.isNullOrEmpty() ||
                    !binding.etEmail.text.isNullOrEmpty() ||
                    initPosition != selectedPosition ||
                    initGender != selectedGender ||
                    initLevel != selectedLevel ||
                    initType != selectedType ||
                    initStatus != selectedStatus
    }
}