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
import com.oceantech.tracking.data.model.Constants.Companion.GENDERS
import com.oceantech.tracking.data.model.Constants.Companion.LEVELS
import com.oceantech.tracking.data.model.Constants.Companion.POSITIONS
import com.oceantech.tracking.data.model.Constants.Companion.STATUSES
import com.oceantech.tracking.data.model.Constants.Companion.TYPES
import com.oceantech.tracking.data.model.response.Member
import com.oceantech.tracking.databinding.DialogEditMemberBinding
import com.oceantech.tracking.ui.admin.OnCallBackListenerAdmin
import com.oceantech.tracking.ui.client.editTask.EditFragment.Companion.setupEditTextBehavior

@SuppressLint("SetTextI18n")
class DialogEditMember(
    private val context: Context,
    private val listener: OnCallBackListenerAdmin,
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
                GENDERS[selectedGender].toUpperCase(),
                LEVELS[selectedLevel],
                if (binding.etName.text.isNullOrEmpty()) member.name!! else binding.etName.text.toString(),
                POSITIONS[selectedPosition].replace(" ", "_"),
                if (STATUSES[selectedStatus].equals(
                        "INTERN",
                        ignoreCase = true
                    )
                ) "INTERNSHIP" else "STAFF",
                member.team,
                TYPES[selectedType].toUpperCase()
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
        var adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, POSITIONS)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPosition.adapter = adapter
        for (i: Int in POSITIONS.indices) if (member.position.equals(
                POSITIONS[i],
                ignoreCase = true
            )
        ) {
            initPosition = i
            binding.spinnerPosition.setSelection(i)
        }

        adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, GENDERS)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter
        for (i: Int in GENDERS.indices) if (member.gender.equals(GENDERS[i], ignoreCase = true)) {
            initGender = i
            binding.spinnerGender.setSelection(i)
        }

        adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, TYPES)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = adapter
        for (i: Int in TYPES.indices) if (member.type.equals(TYPES[i], ignoreCase = true)) {
            initType = i
            binding.spinnerType.setSelection(i)
        }

        adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, STATUSES)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = adapter
        for (i: Int in STATUSES.indices) if (member.status.equals(STATUSES[i], ignoreCase = true)) {
            initStatus = i
            binding.spinnerStatus.setSelection(i)
        }

        adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, LEVELS)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSkillLevel.adapter = adapter
        for (i: Int in LEVELS.indices) if (member.level.equals(LEVELS[i], ignoreCase = true)) {
            initLevel = i
            binding.spinnerSkillLevel.setSelection(i)
        }
        setupSpinnerBehavior(binding.spinnerPosition, ::checkEnabled, "POSITION")
        setupSpinnerBehavior(binding.spinnerGender, ::checkEnabled, "GENDER")
        setupSpinnerBehavior(binding.spinnerType, ::checkEnabled, "TYPE")
        setupSpinnerBehavior(binding.spinnerStatus, ::checkEnabled, "STATUS")
        setupSpinnerBehavior(binding.spinnerSkillLevel, ::checkEnabled, "LEVEL")
    }

    private fun setupSpinnerBehavior(
        spinner: Spinner,
        operation: () -> Unit,
        dataToChange: String
    ) {
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (dataToChange) {
                    "LEVEL" -> selectedLevel = position
                    "STATUS" -> selectedStatus = position
                    "TYPE" -> selectedType = position
                    "GENDER" -> selectedGender = position
                    "POSITION" -> selectedPosition = position
                    else -> {}
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