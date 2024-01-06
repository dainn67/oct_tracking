package com.oceantech.tracking.ui.admin.users

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.fragment.app.DialogFragment
import com.oceantech.tracking.data.model.Constants.Companion.GENDER
import com.oceantech.tracking.data.model.Constants.Companion.GENDER_LIST
import com.oceantech.tracking.data.model.Constants.Companion.MALE
import com.oceantech.tracking.data.model.Constants.Companion.ROLE_ACCOUNTANT
import com.oceantech.tracking.data.model.Constants.Companion.ROLE_ADMIN
import com.oceantech.tracking.data.model.Constants.Companion.ROLE_STAFF
import com.oceantech.tracking.data.model.Constants.Companion.TAG
import com.oceantech.tracking.databinding.DialogNewUserBinding
import com.oceantech.tracking.ui.client.tasksInteractionScreen.TaskInteractionFragment
import java.util.Locale

class DialogAddNewUser(
    private val context: Context,
    private val listener: AdminUsersFragment
): DialogFragment() {
    private lateinit var binding: DialogNewUserBinding

    private var selectedGender = MALE
    private val roleList = mutableListOf<String>()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewUserBinding.inflate(layoutInflater)

        binding.etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkEnable()
            }
        })

        setupEditText()
        setupSpinner()
        setupCheckBoxes()

        binding.cancel.setOnClickListener { dismiss() }
        binding.confirmAdd.setOnClickListener {
            Log.i(TAG, "${binding.etUsername.text} - ${binding.etEmail.text} - $selectedGender - ${roleList.size} - ${binding.etPassword.text}")

            listener.addNewUser(
                binding.etUsername.text.toString(),
                binding.etEmail.text.toString(),
                selectedGender,
                roleList,
                binding.etPassword.text.toString())
            dismiss()
        }

        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)
        return builder.create()
    }

    private fun setupEditText(){
        TaskInteractionFragment.setupEditTextBehavior(binding.etUsername, ::checkEnable)
        TaskInteractionFragment.setupEditTextBehavior(binding.etEmail, ::checkEnable)
        TaskInteractionFragment.setupEditTextBehavior(binding.etPassword, ::checkEnable)
        TaskInteractionFragment.setupEditTextBehavior(binding.etConfirmPass, ::checkEnable)
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, GENDER_LIST)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter
        binding.spinnerGender.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedGender = GENDER_LIST[position].uppercase(Locale.ROOT)
            }

        }
    }

    private fun setupCheckBoxes() {
        setupCheckBox(binding.checkboxAdmin, ROLE_ADMIN)
        setupCheckBox(binding.checkboxAccountant, ROLE_ACCOUNTANT)
        setupCheckBox(binding.checkboxStaff, ROLE_STAFF)
    }

    private fun setupCheckBox(cb: CheckBox, roleType: String){
        cb.setOnClickListener {
            checkEnable()
            if(cb.isChecked) roleList.add(roleType)
            else roleList.remove(roleType)
        }
    }

    fun checkEnable() {
        val check = !binding.etUsername.text.isNullOrEmpty() && !binding.etEmail.text.isNullOrEmpty() &&
                (binding.checkboxAdmin.isChecked || binding.checkboxAccountant.isChecked || binding.checkboxStaff.isChecked) &&
                !binding.etPassword.text.isNullOrEmpty() && !binding.etConfirmPass.text.isNullOrEmpty() && binding.etConfirmPass.text == binding.etPassword.text
        Log.i(TAG, check.toString())
//        binding.confirmAdd.isEnabled = check
    }
}