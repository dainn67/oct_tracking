package com.oceantech.tracking.ui.admin.personnel

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Constants
import com.oceantech.tracking.data.model.Constants.Companion.ROWS_LIST
import com.oceantech.tracking.data.model.Constants.Companion.TAG
import com.oceantech.tracking.data.model.response.Team
import com.oceantech.tracking.databinding.FragmentAdminTeamBinding
import com.oceantech.tracking.databinding.ItemTeamBinding
import com.oceantech.tracking.ui.admin.AdminViewModel
import com.oceantech.tracking.ui.admin.OnCallBackListenerAdmin

class AdminTeamFragment : TrackingBaseFragment<FragmentAdminTeamBinding>() {
    private val viewModel: AdminViewModel by activityViewModel()

    private var maxPages = -1
    private var pageIndex = 1
    private var pageSize = 10
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAdminTeamBinding {
        return FragmentAdminTeamBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadTeams(pageIndex, pageSize)

        views.teamRecView.layoutManager = LinearLayoutManager(requireContext())

        setupSpinnerSize()
        setupPages()
    }


    private fun setupSpinnerSize() {
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ROWS_LIST)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        views.spinnerRow.adapter = spinnerAdapter
        views.spinnerRow.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                pageSize = ROWS_LIST[position]
                pageIndex = 1

                views.currentPage.text = "${getString(R.string.page)} 1"
                viewModel.loadTeams(pageIndex, pageSize)
            }
        }
    }

    private fun setupPages() {
        views.prevPage.setOnClickListener {
            if (pageIndex > 1) pageIndex--
            views.currentPage.text = "${getString(R.string.page)} $pageIndex"
            checkPages()
            viewModel.loadTeams(pageIndex, pageSize)
        }
        views.nextPage.setOnClickListener {
            if (pageIndex < maxPages) pageIndex++
            views.currentPage.text = "${getString(R.string.page)} $pageIndex"
            checkPages()
            viewModel.loadTeams(pageIndex, pageSize)
        }
    }

    private fun checkPages() {
        if (maxPages == 1) {
            views.prevPage.visibility = View.GONE
            views.nextPage.visibility = View.GONE
        } else {
            when (pageIndex) {
                1 -> {
                    views.prevPage.visibility = View.GONE
                    views.nextPage.visibility = View.VISIBLE
                }

                maxPages -> {
                    views.nextPage.visibility = View.GONE
                    views.prevPage.visibility = View.VISIBLE
                }

                else -> {
                    views.nextPage.visibility = View.VISIBLE
                    views.prevPage.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun invalidate(): Unit = withState(viewModel) {
        Log.i(TAG, "INVALIDATING")
        when(it.asyncTeamResponse){
            is Loading -> views.waitingView.visibility = View.VISIBLE
            is Fail -> views.waitingView.visibility = View.GONE
            is Success -> {
                views.waitingView.visibility = View.GONE
                views.teamRecView.adapter = TeamAdapter(it.asyncTeamResponse.invoke().data.content)
                maxPages = it.asyncTeamResponse.invoke().data.totalPages
                checkPages()
            }
        }
    }

    inner class TeamAdapter(
        private val list: List<Team>
    ) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder =
            TeamViewHolder(
                ItemTeamBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    parent,
                    false
                )
            )

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
            holder.bind(list[position], position)
        }

        inner class TeamViewHolder(private val binding: ItemTeamBinding) :
            RecyclerView.ViewHolder(binding.root) {
            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
            fun bind(team: Team, position: Int) {
                binding.teamNo.text = "${getString(R.string.no_)}${position + 1}: "
                binding.teamName.text = team.name
                binding.content.text = "${getString(R.string.desc)}: ${if(team.description.isNullOrEmpty()) getString(R.string.none) else team.description}"
                binding.teamCode.text = "${getString(R.string.code)}: ${team.code}"

                binding.root.setOnClickListener {
                    val dialog = DialogEditTeam(this@AdminTeamFragment, team)
                    dialog.show(requireActivity().supportFragmentManager, "edit_team")
                }
            }
        }
    }
    fun notifyEditTeam(id: String, name: String, code: String, desc: String) {
        viewModel.editTeam(id, name, code, desc, pageIndex, pageSize)
        //after done, load teams with pageIndex and pageSize
    }
}
