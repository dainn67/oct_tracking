package com.oceantech.tracking.ui.client.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.google.gson.Gson
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.response.DateObject
import com.oceantech.tracking.data.model.response.Task
import com.oceantech.tracking.data.network.UserApi
import com.oceantech.tracking.databinding.FragmentHomeBinding
import com.oceantech.tracking.databinding.ItemDayBinding
import com.oceantech.tracking.databinding.ItemTaskBinding
import com.oceantech.tracking.ui.client.home.ClientHomeFragmentDirections
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@SuppressLint("SetTextI18n")
class ClientHomeFragment @Inject constructor(val api: UserApi) :
    TrackingBaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()

    private val selectedCalendar = Calendar.getInstance()
    private var pageIndex = 0
    private var pageSize = 10
    private var daysInMonth = 0
    private var lang = "English"

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeViewEvents {
            handleEvent(it)
        }

        viewModel.setParams(selectedCalendar, pageIndex, pageSize)
        viewModel.initLoad()

        setupMonthYearTab()
        setupAmountTab()
        views.mainRecView.layoutManager = LinearLayoutManager(requireContext())

        setupPageTab()
    }

    private fun setupMonthYearTab() {
        views.currentMonth.text = "${
            HomeViewModel.toMonthString(
                selectedCalendar.get(Calendar.MONTH),
                requireContext()
            )
        } ${selectedCalendar.get(Calendar.YEAR)}"
        views.prevMonth.setOnClickListener {
            selectedCalendar.add(Calendar.MONTH, -1)
            if (pageSize > 20) pageSize = selectedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            setupPageTab()
            updateMonthYearTab()
        }
        views.nextMonth.setOnClickListener {
            selectedCalendar.add(Calendar.MONTH, 1)
            if (pageSize > 20) pageSize = selectedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            setupPageTab()
            updateMonthYearTab()
        }
    }

    private fun updateMonthYearTab() {
        views.currentMonth.text = "${
            HomeViewModel.toMonthString(
                selectedCalendar.get(Calendar.MONTH),
                requireContext()
            )
        } ${selectedCalendar.get(Calendar.YEAR)}"
        viewModel.setParams(selectedCalendar, pageIndex, pageSize)
        viewModel.loadList()
    }

    private fun setupAmountTab() {
        val amounts = listOf(10, 20, "All")
        val adapter =
            context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, amounts) }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        views.spinnerAmount.adapter = adapter
        views.spinnerAmount.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        views.prevPage.visibility = View.VISIBLE
                        views.nextPage.visibility = View.VISIBLE
                        pageSize = 10
                    }

                    1 -> {
                        views.prevPage.visibility = View.VISIBLE
                        views.nextPage.visibility = View.VISIBLE
                        pageSize = 20
                    }

                    else -> {
                        views.prevPage.visibility = View.GONE
                        views.nextPage.visibility = View.GONE
                        daysInMonth = selectedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        pageSize = daysInMonth
                    }
                }
                setupPageTab()
                viewModel.setParams(selectedCalendar, pageIndex, pageSize)
                viewModel.loadList()
            }
        }
    }

    private fun setupPageTab() {
        pageIndex = 0

        if (pageSize > 20) {
            views.prevPage.visibility = View.GONE
            views.nextPage.visibility = View.GONE
        } else {
            views.prevPage.visibility = View.GONE
            views.nextPage.visibility = View.VISIBLE
        }

        views.currentPage.text = getString(R.string.page) + " 1"
        views.prevPage.setOnClickListener {
            if (pageIndex > 0) {
                pageIndex--
                views.currentPage.text = getString(R.string.page) + " " + (pageIndex + 1)

                when (pageIndex) {
                    0 -> {
                        views.prevPage.visibility = View.GONE
                        views.nextPage.visibility = View.VISIBLE
                    }

                    getPages() -> views.nextPage.visibility = View.GONE
                    else -> {
                        views.nextPage.visibility = View.VISIBLE
                        views.prevPage.visibility = View.VISIBLE
                    }
                }

                viewModel.setParams(selectedCalendar, pageIndex, pageSize)
                viewModel.loadList()
            }
        }

        views.nextPage.setOnClickListener {
            if (pageIndex < getPages()) {
                pageIndex++
                views.currentPage.text = getString(R.string.page) + " " + (pageIndex + 1)

                when (pageIndex) {
                    0 -> views.prevPage.visibility = View.GONE
                    getPages() - 1 -> {
                        views.prevPage.visibility = View.VISIBLE
                        views.nextPage.visibility = View.GONE
                    }

                    else -> {
                        views.nextPage.visibility = View.VISIBLE
                        views.prevPage.visibility = View.VISIBLE
                    }
                }

                viewModel.setParams(selectedCalendar, pageIndex, pageSize)
                viewModel.loadList()
            }
        }
    }

    private fun getPages(): Int {
        return when (pageSize) {
            10 -> if (daysInMonth == 31) 4 else 3
            20 -> 2
            else -> 1
        }
    }

    private fun handleEvent(it: HomeViewEvent) {
        when (it) {
            is HomeViewEvent.ResetLanguage -> {
                lang = resources.configuration.locale.displayLanguage
                viewModel.loadList()
                updateMonthYearTab()

                views.tvDays.text = getString(R.string.days)
                setupPageTab()
            }

            else -> {}
        }
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (it.asyncListResponse) {
            is Loading -> views.waitingView.visibility = View.VISIBLE
            is Fail -> views.waitingView.visibility = View.GONE
            is Success -> {
                views.waitingView.visibility = View.GONE
                views.mainRecView.adapter =
                    ListAdapter(requireContext(), it.asyncListResponse.invoke().data?.content!!)
            }
        }

        when (it.asyncProjectTypes) {
            is Loading -> views.waitingView.visibility = View.VISIBLE
            is Success -> views.waitingView.visibility = View.GONE
            is Fail -> views.waitingView.visibility = View.GONE
        }
    }

    inner class ListAdapter(
        private val context: Context,
        private val list: List<DateObject>
    ) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
        private val actualTime = Calendar.getInstance()

        override fun getItemCount() = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder =
            ListViewHolder(
                ItemDayBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
            holder.bind(list[position], context)
        }

        inner class ListViewHolder(private val binding: ItemDayBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(currentDate: DateObject, context: Context) {
                //current date variable
                val currentDateTime = Calendar.getInstance()
                currentDateTime.clear()
                currentDateTime.time = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).parse(currentDate.dateWorking)!!

                //day of week
                val dayOfWeekId = currentDateTime.get(Calendar.DAY_OF_WEEK)
                binding.tvItemDayOfWeek.text =
                    HomeViewModel.toDayOfWeek(dayOfWeekId, requireContext())

                //day of month
                binding.tvItemDayDate.text =
                    "${currentDateTime.get(Calendar.DAY_OF_MONTH)}/${currentDateTime.get(Calendar.MONTH) + 1}"
                if (dayOfWeekId == 1 || dayOfWeekId == 7) {
                    binding.tvDot.setTextColor(ContextCompat.getColor(context, R.color.red))
                    binding.tvItemDayOfWeek.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.red
                        )
                    )
                    binding.tvItemDayDate.setTextColor(ContextCompat.getColor(context, R.color.red))
                } else {
                    binding.tvDot.setTextColor(ContextCompat.getColor(context, R.color.light_blue))
                    binding.tvItemDayOfWeek.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.text_color
                        )
                    )
                    binding.tvItemDayDate.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.text_color
                        )
                    )
                }

                //day off indicator
                if (currentDate.id == null) {
                    binding.dayOffIndi.visibility = View.GONE
                    binding.llDayDetail.visibility = View.GONE
                } else {
                    if (currentDate.dayOff == true) {
                        binding.dayOffIndi.visibility = View.VISIBLE
                        binding.llDayDetail.visibility = View.GONE
                    } else if (currentDate.tasks.isNullOrEmpty()) {
                        binding.dayOffIndi.visibility = View.GONE
                        binding.llDayDetail.visibility = View.GONE
                    } else {
                        binding.dayOffIndi.visibility = View.GONE
                        binding.llDayDetail.visibility = View.VISIBLE

                        binding.tvAmount.text =
                            if (currentDate.tasks.size == 1) "1 ${getString(R.string.task)}"
                            else "${currentDate.tasks.size} ${getString(R.string.tasks)}"

                        //view task list detail
                        itemView.setOnClickListener {
                            if (binding.dateDetail.visibility == View.GONE) {
                                binding.dateDetail.visibility = View.VISIBLE
                                binding.dropdownArrow.setImageResource(R.drawable.down_arrow)

                                binding.dateDetail.layoutManager =
                                    LinearLayoutManager(requireContext())
                                binding.dateDetail.adapter = TaskAdapter(currentDate.tasks)
                            } else {
                                binding.dateDetail.visibility = View.GONE
                                binding.dropdownArrow.setImageResource(R.drawable.right_arrow)
                            }
                        }
                    }
                }

                //edit button
                if (currentDateTime > actualTime) binding.edit.visibility =
                    View.GONE else View.VISIBLE
                binding.edit.setOnClickListener {
                    val gson = Gson()
                    val action = ClientHomeFragmentDirections.actionNavHomeFragmentToEditFragment(
                        dateWorking = gson.toJson(currentDate),
                        lang = lang
                    )
                    findNavController().navigate(action)
                }
            }
        }
    }

    inner class TaskAdapter(private val list: List<Task>) :
        RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
            TaskViewHolder(
                ItemTaskBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            holder.bind(list[position])
        }

        inner class TaskViewHolder(private val binding: ItemTaskBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(task: Task) {
                binding.tvType.text = task.project.code
                binding.tvTaskOH.text = task.officeHour.toString()
                binding.tvTaskOT.text = task.overtimeHour.toString()
                binding.tvTaskOHContent.text = task.taskOffice
                binding.tvTaskOTContent.text = task.taskOverTime
            }
        }
    }
}