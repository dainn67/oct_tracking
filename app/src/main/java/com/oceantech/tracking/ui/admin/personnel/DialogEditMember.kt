package com.oceantech.tracking.ui.admin.personnel

import android.annotation.SuppressLint
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
import androidx.fragment.app.DialogFragment
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.response.Member
import com.oceantech.tracking.data.model.response.Team
import com.oceantech.tracking.databinding.DialogEditMemberBinding
import com.oceantech.tracking.databinding.DialogEditTeamBinding
import com.oceantech.tracking.ui.admin.OnCallBackListenerAdmin

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

    private val positions = listOf("DEV BE", "DEV FE", "TESTER", "DEV FULLSTACK")
    private val genders = listOf("Male", "Female", "LGBT", "Other")
    private val types = listOf("Leader", "Deputy Leader", "Member")
    private val statuses = listOf("Staff", "Intern")
    private val levels = listOf("L0", "L1", "L2", "L3", "L4")
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
                if(binding.etEmail.text.isNullOrEmpty()) member.email!! else binding.etEmail.text.toString(),
                genders[selectedGender].toUpperCase(),
                levels[selectedLevel],
                if(binding.etName.text.isNullOrEmpty()) member.name!! else binding.etName.text.toString(),
                positions[selectedPosition].replace(" ", "_"),
                if(statuses[selectedStatus].equals("INTERN", ignoreCase = true)) "INTERNSHIP" else "STAFF",
                member.team,
                types[selectedType].toUpperCase()
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

        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkEnabled()
            }
        })
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkEnabled()
            }
        })
    }

    private fun setupSpinners(){
        var adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, positions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPosition.adapter = adapter
        for(i: Int in positions.indices) if(member.position.equals(positions[i], ignoreCase = true)) {
            initPosition = i
            binding.spinnerPosition.setSelection(i)
        }
        binding.spinnerPosition.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedPosition = position
                checkEnabled()
            }
        }

        adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, genders)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter
        for(i: Int in genders.indices) if(member.gender.equals(genders[i], ignoreCase = true)) {
            initGender = i
            binding.spinnerGender.setSelection(i)
        }
        binding.spinnerGender.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedGender = position
                checkEnabled()
            }
        }

        adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = adapter
        for(i: Int in types.indices) if(member.type.equals(types[i], ignoreCase = true)) {
            initType = i
            binding.spinnerType.setSelection(i)
        }
        binding.spinnerType.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedType = position
                checkEnabled()
            }
        }

        adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = adapter
        for(i: Int in statuses.indices) if(member.status.equals(statuses[i], ignoreCase = true)) {
            initStatus = i
            binding.spinnerStatus.setSelection(i)
        }
        binding.spinnerStatus.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedStatus = position
                checkEnabled()
            }
        }

        adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, levels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSkillLevel.adapter = adapter
        for(i: Int in levels.indices) if(member.level.equals(levels[i], ignoreCase = true)) {
            initLevel = i
            binding.spinnerSkillLevel.setSelection(i)
        }
        binding.spinnerSkillLevel.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedLevel = position
                checkEnabled()
            }
        }
    }

    private fun checkEnabled() {
        binding.confirmAdd.isEnabled =
                    !binding.etName.text.isNullOrEmpty()||
                    !binding.etEmail.text.isNullOrEmpty() ||
                    initPosition != selectedPosition ||
                    initGender != selectedGender ||
                    initLevel != selectedLevel ||
                    initType != selectedType ||
                    initStatus != selectedStatus
    }
}