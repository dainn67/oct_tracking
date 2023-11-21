package com.oceantech.tracking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentAdminHomeBinding

class AdminHomeFragment : TrackingBaseFragment<FragmentAdminHomeBinding>() {

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAdminHomeBinding {
        return FragmentAdminHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val placeholderList1 = listOf("Team 1", "Team 2", "Team 3")
        val typeAdapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, placeholderList1)
        typeAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        views.spinnerTeam.adapter = typeAdapter1

        val placeholderList2 = listOf("Member 1", "Member 2", "Member 3")
        val typeAdapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, placeholderList2)
        typeAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        views.spinnerMember.adapter = typeAdapter2
    }
}