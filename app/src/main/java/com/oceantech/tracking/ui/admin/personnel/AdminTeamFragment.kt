package com.oceantech.tracking.ui.admin.personnel

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Constants.Companion.ROWS_LIST
import com.oceantech.tracking.data.model.response.Team
import com.oceantech.tracking.databinding.FragmentAdminTeamBinding
import com.oceantech.tracking.databinding.ItemTeamBinding
import com.oceantech.tracking.ui.admin.AdminViewEvent
import com.oceantech.tracking.ui.admin.AdminViewModel
import com.oceantech.tracking.utils.checkPages
import com.oceantech.tracking.utils.setupSpinner

@SuppressLint("SetTextI18n")
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
        views.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadTeams(pageIndex, pageSize)
            views.swipeRefreshLayout.isRefreshing = false
        }

        setupPages()
        views.spinnerRow.setupSpinner({ position ->
            pageSize = ROWS_LIST[position]
            pageIndex = 1

            views.currentPage.text = "${getString(R.string.page)} 1"
            viewModel.loadTeams(pageIndex, pageSize)
        }, ROWS_LIST)

        viewModel.observeViewEvents {
            handleEvent(it)
        }
    }

    private fun handleEvent(it: AdminViewEvent) {
        when (it) {
            is AdminViewEvent.ResetLanguage -> {
                viewModel.loadTeams(pageIndex, pageSize)

                views.tvRows.text = getString(R.string.rows)
                views.currentPage.text = getString(R.string.page) + " " + pageIndex

            }

            is AdminViewEvent.DataModified -> {
                viewModel.loadTeams(pageIndex, pageSize)
            }
        }
    }

    private fun setupPages() {
        views.prevPage.setOnClickListener {
            if (pageIndex > 1) pageIndex--
            views.currentPage.text = "${getString(R.string.page)} $pageIndex"
            checkPages(maxPages, pageIndex, views.prevPage, views.nextPage)
            viewModel.loadTeams(pageIndex, pageSize)
        }
        views.nextPage.setOnClickListener {
            if (pageIndex < maxPages) pageIndex++
            views.currentPage.text = "${getString(R.string.page)} $pageIndex"
            checkPages(maxPages, pageIndex, views.prevPage, views.nextPage)
            viewModel.loadTeams(pageIndex, pageSize)
        }
    }

    override fun invalidate(): Unit = withState(viewModel) {
        if (it.asyncTeamResponse is Success) {
            views.waitingView.visibility = View.GONE
            views.teamRecView.adapter = TeamAdapter(it.asyncTeamResponse.invoke().data.content)
            maxPages = it.asyncTeamResponse.invoke().data.totalPages
            checkPages(maxPages, pageIndex, views.prevPage, views.nextPage)
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
                binding.content.text = "${getString(R.string.desc)}: ${
                    if (team.description.isNullOrEmpty()) getString(R.string.none) else team.description
                }"
                binding.teamCode.text = "${getString(R.string.code)}: ${team.code}"

                binding.root.setOnClickListener {
                    val dialog = DialogEditTeam(this@AdminTeamFragment, team)
                    dialog.show(requireActivity().supportFragmentManager, "edit_team")
                }
            }
        }
    }

    fun notifyEditTeam(id: String, name: String, code: String, desc: String) {
        viewModel.editTeam(id, name, code, desc)
        //after done, load teams with pageIndex and pageSize
    }
}
