package com.oceantech.tracking.ui.admin.users

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.oceantech.tracking.data.model.Constants.Companion.ROLE_ACCOUNTANT
import com.oceantech.tracking.data.model.Constants.Companion.ROLE_ADMIN
import com.oceantech.tracking.data.model.Constants.Companion.ROLE_MANAGER
import com.oceantech.tracking.data.model.Constants.Companion.ROLE_STAFF
import com.oceantech.tracking.data.model.response.User
import com.oceantech.tracking.databinding.DialogEditUserBinding
import com.oceantech.tracking.utils.checkWhileListening

class DialogEditUser(
    private val context: Context,
//    private val listener: OnCallBackListenerAdmin,
    private val user: User
) : DialogFragment() {
    private lateinit var binding: DialogEditUserBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogEditUserBinding.inflate(layoutInflater)

        var selectedGenders = 0
        val genders = listOf("Male", "Female", "LGBT", "Other")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, genders)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter
        binding.spinnerGender.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedGenders = position
            }
        }

        binding.etUsername.hint = user.username
        binding.etEmail.hint = user.email
        for (i in genders.indices)
            if (user.gender.equals(genders[i], ignoreCase = true)) {
                selectedGenders = i
                binding.spinnerGender.setSelection(i)
            }

        binding.etUsername.checkWhileListening (::checkEnabled)
        binding.etEmail.checkWhileListening (::checkEnabled)

        binding.cbManager.isChecked = user.roles!!.contains(ROLE_MANAGER)
        binding.cbAccountant.isChecked = user.roles.contains(ROLE_ACCOUNTANT)
        binding.cbAdmin.isChecked = user.roles.contains(ROLE_ADMIN)
        binding.cbStaff.isChecked = user.roles.contains(ROLE_STAFF)


        binding.cancel.setOnClickListener { dismiss() }
        binding.confirmAdd.setOnClickListener {
//            if(project != null)
//                listener.notifyEdit(
//                    project.id!!,
//                    if(binding.etCode.text.isNullOrEmpty()) project.code else binding.etCode.text.toString(),
//                    if(binding.etName.text.isNullOrEmpty()) project.name else binding.etName.text.toString(),
//                    statuses[selectedStatus],
//                    if(binding.etDesc.text.isNullOrEmpty()) project.description ?: "" else binding.etDesc.text.toString()
//                )
//            else listener.notifyAdd(binding.etCode.text.toString(), binding.etName.text.toString(), statuses[selectedStatus], binding.etDesc.text.toString())
            dismiss()
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        return builder.create()
    }

    private fun checkEnabled(){
//        binding.confirmAdd.isEnabled = !(binding.etCode.text.isNullOrEmpty() || binding.etName.text.isNullOrEmpty())
    }
}