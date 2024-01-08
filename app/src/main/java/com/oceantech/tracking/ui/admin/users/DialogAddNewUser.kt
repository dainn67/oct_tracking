package com.oceantech.tracking.ui.admin.users

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.oceantech.tracking.utils.checkWhileListening
import com.oceantech.tracking.utils.setupSpinner
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

        setupEditText()
        setupCheckBoxes()
        setupSpinner(binding.spinnerGender, {position ->
            selectedGender = GENDER_LIST[position].uppercase(Locale.ROOT)
        }, GENDER_LIST)

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

    private fun setupEditText(){
        binding.etUsername.checkWhileListening(::checkEnable)
        binding.etEmail.checkWhileListening(::checkEnable)
        binding.etPassword.checkWhileListening(::checkEnable)
        binding.etConfirmPass.checkWhileListening(::checkEnable)
    }

    private fun setupCheckBoxes() {
        setupCheckBox(binding.checkboxAdmin, ROLE_ADMIN)
        setupCheckBox(binding.checkboxAccountant, ROLE_ACCOUNTANT)
        setupCheckBox(binding.checkboxStaff, ROLE_STAFF)
    }

    private fun setupCheckBox(cb: CheckBox, roleType: String){
        cb.setOnCheckedChangeListener { _, isChecked ->
            checkEnable()
            if(isChecked) roleList.add(roleType)
            else roleList.remove(roleType)
        }
    }

    private fun checkEnable() {
        binding.confirmAdd.isEnabled =
                    !binding.etUsername.text.isNullOrBlank() &&
                    !binding.etEmail.text.isNullOrBlank() &&
                    !binding.etPassword.text.isNullOrBlank() &&
                    !binding.etConfirmPass.text.isNullOrBlank() &&
                    binding.etConfirmPass.text.toString().trim() == binding.etPassword.text.toString().trim() &&
                    (binding.checkboxAdmin.isChecked || binding.checkboxAccountant.isChecked || binding.checkboxStaff.isChecked)
    }
}