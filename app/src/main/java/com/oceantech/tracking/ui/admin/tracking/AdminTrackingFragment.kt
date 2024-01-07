package com.oceantech.tracking.ui.admin.tracking

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Constants.Companion.ROWS_LIST
import com.oceantech.tracking.data.model.response.DateObject
import com.oceantech.tracking.data.model.response.Member
import com.oceantech.tracking.data.model.response.Task
import com.oceantech.tracking.data.model.response.Team
import com.oceantech.tracking.databinding.FragmentAdminTrackingBinding
import com.oceantech.tracking.databinding.ItemTaskBinding
import com.oceantech.tracking.databinding.ItemTrackingBinding
import com.oceantech.tracking.ui.admin.AdminViewModel
import com.oceantech.tracking.ui.client.homeScreen.HomeViewModel
import com.oceantech.tracking.utils.checkPages
import com.oceantech.tracking.utils.setupSpinner
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@SuppressLint("SetTextI18n")
class AdminTrackingFragment : TrackingBaseFragment<FragmentAdminTrackingBinding>() {

    private val viewModel: AdminViewModel by activityViewModel()

    private var pageIndex: Int = 1
    private var pageSize: Int = 10
    private var maxPages: Int = 0
    private val fromDate: Calendar = Calendar.getInstance()
    private val toDate: Calendar = Calendar.getInstance()
    private var teamId: String? = null
    private var memberId: String? = null

    //to handle which part need reloading, avoid reloading all of them at once
    private var listNeedReload = true
    private var teamNeedReload = true
    private var memberNeedReload = true
    private var typesNeedReload = true

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAdminTrackingBinding {
        return FragmentAdminTrackingBinding.inflate(inflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        listNeedReload = true
        teamNeedReload = true
        memberNeedReload = true
        typesNeedReload = true

        setup()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup(){
        //setup pages and teams/member are called in invalidate()
        setupDateFilter()
        setupSpinnerSize()
        setupPages()

        views.swipeRefreshLayout.setOnRefreshListener {
            listNeedReload = true
            viewModel.reloadTracking(fromDate, toDate, teamId, memberId, pageIndex, pageSize)
            views.swipeRefreshLayout.isRefreshing = false
        }

        views.trackingRecView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.initLoad()
    }

    @SuppressLint("SetTextI18n")
    private fun setupDateFilter() {
        val calendar = Calendar.getInstance()
        fromDate.set(Calendar.DAY_OF_MONTH, 1)
        toDate.set(Calendar.DAY_OF_MONTH, toDate.getActualMaximum(Calendar.DAY_OF_MONTH))
        views.fromDate.text =
            "${getString(R.string.from_date)}: ${fromDate.get(Calendar.DAY_OF_MONTH)}/${
                fromDate.get(
                    Calendar.MONTH
                ) + 1
            }/${
                fromDate.get(Calendar.YEAR)
            }"
        views.toDate.text =
            "${getString(R.string.to_date)}: ${toDate.get(Calendar.DAY_OF_MONTH)}/${
                toDate.get(
                    Calendar.MONTH
                ) + 1
            }/${
                toDate.get(Calendar.YEAR)
            }"

        views.editFromDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    fromDate.set(Calendar.YEAR, year)
                    fromDate.set(Calendar.MONTH, month)
                    fromDate.set(Calendar.DAY_OF_MONTH, day)
                    views.fromDate.text =
                        "${getString(R.string.from_date)}: ${fromDate.get(Calendar.DAY_OF_MONTH)}/${
                            fromDate.get(
                                Calendar.MONTH
                            ) + 1
                        }/${
                            fromDate.get(Calendar.YEAR)
                        }"

                    pageIndex = 1
                    views.currentPage.text = "${getString(R.string.page)} 1"

                    listNeedReload = true
                    viewModel.reloadTracking(fromDate, toDate, teamId, memberId, pageIndex, pageSize)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
            ).show()
        }
        views.editToDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    toDate.set(Calendar.YEAR, year)
                    toDate.set(Calendar.MONTH, month)
                    toDate.set(Calendar.DAY_OF_MONTH, day)
                    views.toDate.text =
                        "${getString(R.string.to_date)}: ${toDate.get(Calendar.DAY_OF_MONTH)}/${
                            toDate.get(
                                Calendar.MONTH
                            ) + 1
                        }/${
                            toDate.get(Calendar.YEAR)
                        }"

                    listNeedReload = true
                    pageIndex = 1
                    views.currentPage.text = "${getString(R.string.page)} 1"
                    viewModel.reloadTracking(fromDate, toDate, teamId, memberId, pageIndex, pageSize)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
            ).show()
        }
    }

    private fun setupTeamFilter(teams: List<Team>) {
        var userInteract = false
        val teamNames = teams.map { team -> team.name } as MutableList
        teamNames.add(0, getString(R.string.all_teams))

        setupSpinner(views.spinnerTeam, { position ->
            if (!userInteract) userInteract = true
            else {
                teamId = if (position == 0) null else teams[position - 1].id
                memberId = null

                memberNeedReload = true
                listNeedReload = true
                viewModel.reloadTracking(fromDate, toDate, teamId, memberId, pageIndex, pageSize)
            }
        }, teamNames)
    }

    private fun setupMemberFilter(members: List<Member>) {
        var userInteract = false
        val memberNames = members.map { member -> member.name } as MutableList
        memberNames.add(0, getString(R.string.all_members))

        setupSpinner(views.spinnerMember, { position ->
            if (!userInteract) userInteract = true
            else {
                listNeedReload = true
                memberId = if (position == 0) null else members[position - 1].id
                viewModel.reloadTracking(fromDate, toDate, teamId, memberId, pageIndex, pageSize)
            }
        }, memberNames)
    }

    private fun setupSpinnerSize() {
        setupSpinner(views.rows, { position ->
            pageSize = when (position) {
                0 -> 10
                1 -> 20
                2 -> 30
                3 -> 40
                else -> 50
            }

            pageIndex = 1
            views.currentPage.text = "${getString(R.string.page)} 1"

            listNeedReload = true
            viewModel.reloadTracking(fromDate, toDate, teamId, memberId, pageIndex, pageSize)
        }, ROWS_LIST)
    }

    private fun setupPages() {
        views.prevPage.setOnClickListener {
            if (pageIndex > 1) pageIndex--
            views.currentPage.text = "${getString(R.string.page)} $pageIndex"
            checkPages(maxPages, pageIndex, views.prevPage, views.nextPage)

            listNeedReload = true
            viewModel.reloadTracking(fromDate, toDate, teamId, memberId, pageIndex, pageSize)
        }
        views.nextPage.setOnClickListener {
            if (pageIndex < maxPages) pageIndex++
            views.currentPage.text = "${getString(R.string.page)} $pageIndex"
            checkPages(maxPages, pageIndex, views.prevPage, views.nextPage)

            listNeedReload = true
            viewModel.reloadTracking(fromDate, toDate, teamId, memberId, pageIndex, pageSize)
        }
    }

    override fun invalidate(): Unit = withState(viewModel) {
        if(listNeedReload){
            when (it.asyncListResponse) {
                is Loading -> views.waitingView.visibility = View.VISIBLE
                is Fail -> views.waitingView.visibility = View.GONE
                is Success -> {
                    val data = it.asyncListResponse.invoke().data

                    listNeedReload = false
                    views.waitingView.visibility = View.GONE
                    views.trackingRecView.adapter = TrackingAdapter(data?.content!!)
                    maxPages = data.totalPages!!
                    checkPages(maxPages, pageIndex, views.prevPage, views.nextPage)
                }
            }
        }

        if(teamNeedReload){
            when (it.asyncTeamResponse) {
                is Loading -> views.waitingView.visibility = View.VISIBLE
                is Fail -> views.waitingView.visibility = View.GONE
                is Success -> {
                    views.waitingView.visibility = View.GONE
                    teamNeedReload = false
                    setupTeamFilter(it.asyncTeamResponse.invoke().data.content)
                }
            }
        }

        if(memberNeedReload){
            when (it.asyncMemberResponse) {
                is Loading -> views.waitingView.visibility = View.VISIBLE
                is Fail -> views.waitingView.visibility = View.GONE
                is Success -> {
                    views.waitingView.visibility = View.GONE
                    memberNeedReload = false
                    setupMemberFilter(it.asyncMemberResponse.invoke().data.content)
                }
            }
        }

        if(typesNeedReload){
            when (it.asyncProjectsResponse) {
                is Loading -> views.waitingView.visibility = View.VISIBLE
                is Fail -> views.waitingView.visibility = View.GONE
                is Success -> {
                    views.waitingView.visibility = View.GONE
                    typesNeedReload = false
                }
            }
        }

    }

    inner class TrackingAdapter(
        private val list: List<DateObject>,
    ) : RecyclerView.Adapter<TrackingAdapter.TrackingViewHolder>() {
        override fun getItemCount() = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingViewHolder =
            TrackingViewHolder(
                ItemTrackingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onBindViewHolder(holder: TrackingViewHolder, position: Int) {
            holder.bind(list[position])
        }

        inner class TrackingViewHolder(
            private val binding: ItemTrackingBinding
        ) : RecyclerView.ViewHolder(binding.root) {
            @RequiresApi(Build.VERSION_CODES.M)
            fun bind(item: DateObject) {
                val calendar = Calendar.getInstance()
                calendar.time =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(item.dateWorking)!!
                binding.date.setTextColor(if(calendar.get(Calendar.DAY_OF_YEAR) % 2 == 0) requireContext().getColor(R.color.green) else requireContext().getColor(R.color.blue))
                binding.date.text = HomeViewModel.toDayOfWeek(
                    calendar.get(Calendar.DAY_OF_WEEK),
                    requireContext()
                ) + " - " + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1)

                if (item.member != null) {
                    binding.name.text = item.member.name

                    if (item.dayOff == true) {
                        binding.dropdown.visibility = View.GONE
                        binding.totalHours.text = getString(R.string.off)
                    } else {
                        binding.dropdown.visibility = View.VISIBLE
                        binding.dayOff.setImageResource(R.drawable.working)

                        binding.trackingDetailRecView.layoutManager =
                            LinearLayoutManager(requireContext())
                        binding.trackingDetailRecView.adapter = TrackingDetailAdapter(item.tasks!!)

                        with(binding.dropdown) {
                            this.setOnClickListener {
                                if (binding.trackingDetailRecView.visibility == View.GONE) {
                                    this.setImageResource(R.drawable.down_arrow)
                                    binding.trackingDetailRecView.visibility = View.VISIBLE
                                } else {
                                    this.setImageResource(R.drawable.right_arrow)
                                    binding.trackingDetailRecView.visibility = View.GONE
                                }
                            }
                        }

                        binding.totalHours.text = "${viewModel.getTotalHour(item.tasks)} ${getString(R.string.hours)}"
                    }
                } else {
                    binding.dayOff.setImageResource(R.drawable.no_event)
                    binding.totalHours.text = getString(R.string.no_event)
                }
            }
        }
    }

    inner class TrackingDetailAdapter(
        private val list: List<Task>
    ) : RecyclerView.Adapter<TrackingDetailAdapter.TrackingDetailViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): TrackingDetailViewHolder = TrackingDetailViewHolder(
            ItemTaskBinding.inflate(
                LayoutInflater.from(requireContext()),
                parent,
                false
            )
        )

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: TrackingDetailViewHolder, position: Int) {
            holder.bind(list[position])
        }

        inner class TrackingDetailViewHolder(
            private val binding: ItemTaskBinding
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(task: Task) {
                binding.tvType.text = task.project.code
                binding.tvTaskOH.text = task.officeHour.toString()
                binding.tvTaskOT.text = task.overtimeHour.toString()
                binding.tvTaskOHContent.text = task.taskOffice
                binding.tvTaskOTContent.text = task.taskOverTime

                binding.root.setOnClickListener {
                    val dialog = DialogTaskDetail(requireContext(), task)
                    dialog.show(requireActivity().supportFragmentManager, "task_detail")
                }
            }
        }
    }
}
