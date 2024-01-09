package com.oceantech.tracking.ui.admin.personnel

import android.annotation.SuppressLint
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
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.Constants.Companion.GENDER
import com.oceantech.tracking.data.model.Constants.Companion.GENDER_LIST
import com.oceantech.tracking.data.model.Constants.Companion.LEVEL
import com.oceantech.tracking.data.model.Constants.Companion.LEVEL_LIST
import com.oceantech.tracking.data.model.Constants.Companion.POSITION
import com.oceantech.tracking.data.model.Constants.Companion.POSITION_LIST
import com.oceantech.tracking.data.model.Constants.Companion.STATUS
import com.oceantech.tracking.data.model.Constants.Companion.STATUS_LIST
import com.oceantech.tracking.data.model.Constants.Companion.TEAM
import com.oceantech.tracking.data.model.Constants.Companion.TYPE
import com.oceantech.tracking.data.model.Constants.Companion.TYPE_LIST
import com.oceantech.tracking.data.model.response.Member
import com.oceantech.tracking.data.model.response.Team
import com.oceantech.tracking.databinding.DialogEditMemberBinding
import com.oceantech.tracking.ui.admin.AdminViewModel
import com.oceantech.tracking.utils.checkWhileListening
import com.oceantech.tracking.utils.toDisplayDateTime

@SuppressLint("SetTextI18n")
class DialogEditMember(
    private val context: Context,
    private val listener: AdminMemberFragment,
    private val member: Member,
    private val teamList: List<Team>?
) : DialogFragment() {

    private lateinit var binding: DialogEditMemberBinding

    private var initPosition = 0
    private var initGender = 0
    private var initType = 0
    private var initStatus = 0
    private var initLevel = 0
    private var initTeamName = member.team.name

    private var selectedPosition = 0
    private var selectedGender = 0
    private var selectedType = 0
    private var selectedStatus = 0
    private var selectedLevel = 0
    private lateinit var selectedTeam: Team

    private var isValidEmail = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogEditMemberBinding.inflate(layoutInflater)

        binding.joinDate.text = toDisplayDateTime(member.dateJoin)
        binding.code.text = member.code
        setupEditTexts()
        setupSpinners()

        binding.cancel.setOnClickListener { dismiss() }
        binding.confirmAdd.setOnClickListener {
            listener.notifyEditMember(
                member.id!!,
                member.code!!,
                member.dateJoin,
                if (binding.etEmail.text.isNullOrEmpty()) member.email!! else binding.etEmail.text.toString(),
                GENDER_LIST[selectedGender].toUpperCase(),
                LEVEL_LIST[selectedLevel],
                if (binding.etName.text.isNullOrEmpty()) member.name!! else binding.etName.text.toString(),
                POSITION_LIST[selectedPosition].replace(" ", "_"),
                if (STATUS_LIST[selectedStatus].equals("Internship", ignoreCase = true)) "INTERNSHIP" else "STAFF",
                selectedTeam,
                TYPE_LIST[selectedType].toUpperCase()
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

    private fun setupEditTexts() {
        binding.etName.hint = member.name
        binding.etEmail.hint = member.email

        binding.etName.checkWhileListening (::checkEnabled)
        binding.etEmail.addTextChangedListener ( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$"
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.isNullOrBlank() || !s.matches(emailRegex.toRegex())){
                    binding.etEmail.error = getString(R.string.invalid_email)
                    isValidEmail = false
                } else isValidEmail = true
                checkEnabled()
            }
        } )
    }

    private fun setupSpinners() {
        setupSpinnerBehavior(binding.spinnerPosition, ::checkEnabled, POSITION)
        setupSpinnerBehavior(binding.spinnerGender, ::checkEnabled, GENDER)
        setupSpinnerBehavior(binding.spinnerType, ::checkEnabled, TYPE)
        setupSpinnerBehavior(binding.spinnerStatus, ::checkEnabled, STATUS)
        setupSpinnerBehavior(binding.spinnerSkillLevel, ::checkEnabled, LEVEL)
        setupSpinnerBehavior(binding.spinnerTeam, ::checkEnabled, TEAM)

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
            TEAM -> teamList?.map { team -> team.name } ?: emptyList()
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
            TEAM -> member.team.name
            else -> ""
        }

        for (i: Int in options.indices) if (ref!!.replace("_", " ").equals(options[i].replace("_", " "), ignoreCase = true)) {
            when (type) {
                LEVEL -> initLevel = i
                STATUS -> initStatus = i
                TYPE -> initType = i
                GENDER -> initGender = i
                POSITION -> initPosition = i
            }
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
                    TEAM -> selectedTeam = teamList?.get(position) ?: member.team
                }
                operation()
            }
        }
    }

    private fun checkEnabled() {
        binding.confirmAdd.isEnabled =
            !binding.etName.text.isNullOrBlank() ||
                    (!binding.etEmail.text.isNullOrBlank() && isValidEmail) ||
                    initPosition != selectedPosition ||
                    initGender != selectedGender ||
                    initLevel != selectedLevel ||
                    initType != selectedType ||
                    initStatus != selectedStatus ||
                    initTeamName != selectedTeam.name
    }
}