package com.oceantech.tracking.ui.admin.personnel

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
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
import com.oceantech.tracking.data.model.response.Member
import com.oceantech.tracking.data.model.response.Team
import com.oceantech.tracking.databinding.FragmentAdminPersonnelBinding
import com.oceantech.tracking.databinding.ItemMemberBinding
import com.oceantech.tracking.databinding.ItemTeamBinding
import com.oceantech.tracking.ui.admin.AdminViewModel
import com.oceantech.tracking.ui.admin.OnCallBackListenerAdmin

@SuppressLint("SetTextI18n")
class AdminPersonnelFragment : TrackingBaseFragment<FragmentAdminPersonnelBinding>(), OnCallBackListenerAdmin {

    private val viewModel: AdminViewModel by activityViewModel()

    //identify which part need reloading, avoid reloading everything
    private var teamsNeedReload = 1
    private var membersNeedReload = 1

    private var currentTeamId : String? = null
    private var pageIndexTeam = 1
    private var pageSizeTeam = 10
    private var pageIndexMember = 1
    private var pageSizeMember = 10
    private var maxPagesTeam = 0
    private var maxPagesMember = 0
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAdminPersonnelBinding {
        return FragmentAdminPersonnelBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.teamRecView.layoutManager = LinearLayoutManager(requireContext())
        views.memberRecView.layoutManager = LinearLayoutManager(requireContext())

        setupSpinnerSize()
        setupPages()
    }

    private fun setupSpinnerSize() {
        val optionSizes = listOf(10, 20, 30, 40, 50)
        val optionRows = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            optionSizes
        )
        optionRows.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        views.rowsTeam.adapter = optionRows
        views.rowsMember.adapter = optionRows
        views.rowsTeam.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                pageSizeTeam = when (position) {
                    0 -> 10
                    1 -> 20
                    2 -> 30
                    3 -> 40
                    else -> 50
                }

                pageIndexTeam = 1
                views.currentPageTeam.text = "${getString(com.oceantech.tracking.R.string.page)} 1"

                teamsNeedReload = 1
                viewModel.loadTeams(pageIndexTeam, pageSizeTeam)
            }
        }
        views.rowsMember.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                pageSizeMember = when (position) {
                    0 -> 10
                    1 -> 20
                    2 -> 30
                    3 -> 40
                    else -> 50
                }

                pageIndexMember = 1     //this need reload 1 times
                views.currentPageMember.text = "${getString(com.oceantech.tracking.R.string.page)} 1"

                membersNeedReload = 1
                viewModel.loadMembers(currentTeamId, pageIndexMember, pageSizeMember)
            }
        }
    }

    private fun setupPages() {
        views.prevPageTeam.setOnClickListener {
            if (pageIndexTeam > 1) pageIndexTeam--
            views.currentPageTeam.text = "${getString(R.string.page)} $pageIndexTeam"
            checkPagesTeam()

            teamsNeedReload = 1     //this need reload 1 times
            viewModel.loadTeams(pageIndexTeam, pageSizeTeam)
        }
        views.nextPageTeam.setOnClickListener {
            if (pageIndexTeam < maxPagesTeam) pageIndexTeam++
            views.currentPageTeam.text = "${getString(R.string.page)} $pageIndexTeam"
            checkPagesTeam()

            teamsNeedReload = 1     //this need reload 1 times
            viewModel.loadTeams(pageIndexTeam, pageSizeTeam)
        }

        views.prevPageMember.setOnClickListener {
            if (pageIndexMember > 1) pageIndexMember--
            views.currentPageMember.text = "${getString(R.string.page)} $pageIndexMember"
            checkPagesMember()

            membersNeedReload = 1
            viewModel.loadMembers(currentTeamId, pageIndexMember, pageSizeMember)
        }
        views.nextPageMember.setOnClickListener {
            if (pageIndexMember < maxPagesMember) pageIndexMember++
            views.currentPageMember.text = "${getString(R.string.page)} $pageIndexMember"
            checkPagesMember()

            membersNeedReload = 1
            viewModel.loadMembers(currentTeamId, pageIndexMember, pageSizeMember)
        }
    }

    private fun checkPagesTeam(max: Int? = null) {
        max?.let { maxPagesTeam = it }
        if(maxPagesTeam == 1){
            views.prevPageTeam.visibility = View.GONE
            views.nextPageTeam.visibility = View.GONE
        }else{
            when (pageIndexTeam) {
                1 -> {
                    views.prevPageTeam.visibility = View.GONE
                    views.nextPageTeam.visibility = View.VISIBLE
                }

                maxPagesTeam -> {
                    views.nextPageTeam.visibility = View.GONE
                    views.prevPageTeam.visibility = View.VISIBLE
                }

                else -> {
                    views.nextPageTeam.visibility = View.VISIBLE
                    views.prevPageTeam.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun checkPagesMember(max :Int? = null) {
        max?.let { maxPagesMember = it }
        if(maxPagesMember == 1){
            views.prevPageMember.visibility = View.GONE
            views.nextPageMember.visibility = View.GONE
        }else{
            when (pageIndexMember) {
                1 -> {
                    views.prevPageMember.visibility = View.GONE
                    views.nextPageMember.visibility = View.VISIBLE
                }

                maxPagesMember -> {
                    views.nextPageMember.visibility = View.GONE
                    views.prevPageMember.visibility = View.VISIBLE
                }

                else -> {
                    views.nextPageMember.visibility = View.VISIBLE
                    views.prevPageMember.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun invalidate(): Unit = withState(viewModel) {
        views.waitingView.visibility = if(it.asyncModify is Loading)  View.VISIBLE else View.GONE

        if(teamsNeedReload > 0)
            when (it.asyncTeamResponse) {
                is Loading -> views.waitingView.visibility = View.VISIBLE
                is Fail -> views.waitingView.visibility = View.GONE
                is Success -> {
                    teamsNeedReload--
                    views.waitingView.visibility = View.GONE

                    views.teamRecView.adapter = TeamAdapter(it.asyncTeamResponse.invoke().data.content)
                    checkPagesTeam(it.asyncTeamResponse.invoke().data.totalPages)
                }
            }

        if(membersNeedReload > 0)
            when (it.asyncMemberResponse) {
                is Loading -> views.waitingView.visibility = View.VISIBLE
                is Fail -> views.waitingView.visibility = View.GONE
                is Success -> {
                    membersNeedReload--
                    views.waitingView.visibility = View.GONE

                    views.memberRecView.adapter = MemberAdapter(it.asyncMemberResponse.invoke().data.content)
                    checkPagesMember(it.asyncMemberResponse.invoke().data.totalPages)
                }
            }
    }

    inner class TeamAdapter(
        private val list: List<Team>
    ) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {
        private var selectedItem = -1
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
            @SuppressLint("NotifyDataSetChanged")
            fun bind(team: Team, position: Int) {
                binding.teamName.text = "${getString(R.string.team)} ${position + 1} :  ${team.name}"
                if(selectedItem == position) binding.teamName.setTextColor(resources.getColor(R.color.green))
                else binding.teamName.setTextColor(resources.getColor(R.color.text_color))

                binding.root.setOnClickListener {
                    selectedItem = position
                    notifyDataSetChanged()

                    currentTeamId = team.id
                    views.tvMember.text = "${getString(R.string.members)} (${team.name})"

                    membersNeedReload = 1
                    pageIndexMember = 1
                    viewModel.loadMembers(currentTeamId, pageIndexMember, pageSizeMember)
                }

                binding.edit.setOnClickListener {
                    val dialog = DialogEditTeam(this@AdminPersonnelFragment, team)
                    dialog.show(requireActivity().supportFragmentManager, "edit_team")
                }
            }
        }
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
            fun bind(member: Member) {
                binding.code.text = member.code
                binding.name.text = member.name
                binding.joinDate.text = "${getString(R.string.join)}: ${member.dateJoin}"

                binding.root.setOnClickListener {
                    val dialog = DialogEditMember(requireContext(), this@AdminPersonnelFragment, member)
                    dialog.show(requireActivity().supportFragmentManager, "edit_member")
                }
            }
        }
    }

    override fun notifyEditTeam(id: String, name: String, code: String, desc: String) {
        teamsNeedReload = 2     //this need reload 2 times
        viewModel.editTeam(id, name, code, desc)
    }

    override fun notifyEditMember(
        id: String,
        code: String,
        dateJoin: String,
        email: String,
        gender: String,
        level: String,
        name: String,
        position: String,
        status: String,
        team: Team,
        type: String
    ) {
        membersNeedReload = 2
        viewModel.editMember(currentTeamId, pageIndexMember, pageSizeMember, id, code, dateJoin, email, gender, level, name, position, status, team, type)
    }

    override fun notifyEditProject(id: String, code: String, name: String, status: String, desc: String) {}
    override fun notifyAddProject(code: String, name: String, status: String, desc: String) {}
    override fun notifyDeleteProject(id: String) {}
}
