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
import androidx.fragment.app.activityViewModels
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
import com.oceantech.tracking.data.model.Constants.Companion.FEMALE
import com.oceantech.tracking.data.model.Constants.Companion.LGBT
import com.oceantech.tracking.data.model.Constants.Companion.MALE
import com.oceantech.tracking.data.model.Constants.Companion.TAG
import com.oceantech.tracking.data.model.response.Member
import com.oceantech.tracking.data.model.response.Team
import com.oceantech.tracking.databinding.FragmentAdminMemberBinding
import com.oceantech.tracking.databinding.ItemMemberBinding
import com.oceantech.tracking.databinding.ItemTeamBinding
import com.oceantech.tracking.ui.admin.AdminViewModel

class AdminMemberFragment : TrackingBaseFragment<FragmentAdminMemberBinding>() {
    private val viewModel: AdminViewModel by activityViewModel()

    private var maxPages = -1
    private var pageIndex = 1
    private var pageSize = 10
    private var currentTeamId: String? = null

    private var requireLoadTeamInitially = true
    private lateinit var teams: List<Team>

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAdminMemberBinding {
        return FragmentAdminMemberBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadTeams()
        viewModel.loadMembers()

        views.recViewMember.layoutManager = LinearLayoutManager(requireContext())
        views.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadMembers(currentTeamId, pageIndex, pageSize)
            views.swipeRefreshLayout.isRefreshing = false
        }

        setupSpinnerSize()
        setupPages()
    }

    private fun setupSpinnerFilter() {
        val teamNames = teams.map { team -> team.name } as MutableList
        teamNames.add(0, getString(R.string.none))
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, teamNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        views.spinnerTeam.adapter = spinnerAdapter
        views.spinnerTeam.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentTeamId = if(position == 0) null else teams[position].id
                pageIndex = 1

                views.currentPage.text = "${getString(R.string.page)} 1"
                viewModel.loadMembers(currentTeamId, pageIndex, pageSize)
            }
        }
    }

    private fun setupSpinnerSize() {
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, Constants.ROWS_LIST)
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
                pageSize = Constants.ROWS_LIST[position]
                pageIndex = 1

                views.currentPage.text = "${getString(R.string.page)} 1"
                viewModel.loadMembers(currentTeamId, pageIndex, pageSize)
            }
        }
    }

    private fun setupPages() {
        views.prevPage.setOnClickListener {
            if (pageIndex > 1) pageIndex--
            views.currentPage.text = "${getString(R.string.page)} $pageIndex"
            checkPages()
            viewModel.loadMembers(currentTeamId, pageIndex, pageSize)
        }
        views.nextPage.setOnClickListener {
            if (pageIndex < maxPages) pageIndex++
            views.currentPage.text = "${getString(R.string.page)} $pageIndex"
            checkPages()
            viewModel.loadMembers(currentTeamId, pageIndex, pageSize)
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
        if(requireLoadTeamInitially){
            when(it.asyncTeamResponse){
                is Loading -> views.waitingView.visibility = View.VISIBLE
                is Fail -> views.waitingView.visibility = View.GONE
                is Success -> {
                    requireLoadTeamInitially = false
                    views.waitingView.visibility = View.GONE
                    teams = it.asyncTeamResponse.invoke().data.content
                    setupSpinnerFilter()
                }
            }
        }
        when(it.asyncMemberResponse){
            is Loading -> views.waitingView.visibility = View.VISIBLE
            is Fail -> views.waitingView.visibility = View.GONE
            is Success -> {
                views.waitingView.visibility = View.GONE
                maxPages = it.asyncMemberResponse.invoke().data.totalPages
                checkPages()
                views.recViewMember.adapter = MemberAdapter(it.asyncMemberResponse.invoke().data.content)
            }
        }
    }

    fun notifyEditMember(id: String, code: String, dateJoin: String, email: String, gender: String, level: String, name: String, position: String, status: String, team: Team, type: String){
        viewModel.editMember(currentTeamId, pageIndex, pageSize, id, code, dateJoin, email, gender, level, name, position, status, team, type)
    }

    inner class MemberAdapter(
        private val list: List<Member>
    ) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder =
            MemberViewHolder(
                ItemMemberBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    parent,
                    false
                )
            )

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
            holder.bind(list[position])
        }

        inner class MemberViewHolder(private val binding: ItemMemberBinding) :
            RecyclerView.ViewHolder(binding.root) {
            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
            fun bind(member: Member) {
                Log.i(TAG, member.gender.toString())
                binding.avatar.setImageResource(
                    when(member.gender){
                        MALE -> R.drawable.male
                        FEMALE -> R.drawable.female
                        LGBT -> R.drawable.lgbt
                        else -> R.drawable.other_gender
                    }
                )

                binding.name.text = member.name
                binding.code.text = member.code
                binding.position.text = member.position

                binding.root.setOnClickListener {
                    val dialog = DialogEditMember(requireContext(), this@AdminMemberFragment, member)
                    dialog.show(requireActivity().supportFragmentManager, "edit_team")
                }
            }
        }
    }
}