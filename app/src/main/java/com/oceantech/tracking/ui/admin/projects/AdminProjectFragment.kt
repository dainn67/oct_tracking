package com.oceantech.tracking.ui.admin.projects

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.oceantech.tracking.data.model.response.Project
import com.oceantech.tracking.data.model.response.Team
import com.oceantech.tracking.databinding.FragmentAdminProjectBinding
import com.oceantech.tracking.databinding.ItemProjectBinding
import com.oceantech.tracking.ui.admin.AdminViewModel
import com.oceantech.tracking.ui.admin.OnCallBackListenerAdmin

@SuppressLint("SetTextI18n")
class AdminProjectFragment : TrackingBaseFragment<FragmentAdminProjectBinding>(),
    OnCallBackListenerAdmin {

    private val viewModel: AdminViewModel by activityViewModel()

    private var maxPages = 0
    private var pageSize = 10
    private var pageIndex = 1

    private var checkReload = false

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAdminProjectBinding {
        return FragmentAdminProjectBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.projectRecView.layoutManager = LinearLayoutManager(requireContext())
        setupSpinnerSize()
        setupPages()

        views.addNewProject.setOnClickListener {
            val dialog = DialogEditProject(requireContext(), this)
            dialog.show(requireActivity().supportFragmentManager, "new_project")
        }
    }

    private fun setupSpinnerSize() {
        val optionSizes = listOf(10, 20, 30, 40, 50)
        val optionRows = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            optionSizes
        )
        optionRows.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        views.rows.adapter = optionRows
        views.rows.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                pageSize = when (position) {
                    0 -> 10
                    1 -> 20
                    2 -> 30
                    3 -> 40
                    else -> 50
                }

                pageIndex = 1
                views.currentPage.text = "${getString(R.string.page)} 1"
                viewModel.loadProjectTypes(pageIndex, pageSize)
            }
        }
    }

    private fun setupPages() {
        views.prevPage.setOnClickListener {
            if (pageIndex > 1) pageIndex--
            views.currentPage.text = "${getString(R.string.page)} $pageIndex"
            checkPages()
            viewModel.loadProjectTypes(pageIndex, pageSize)
        }
        views.nextPage.setOnClickListener {
            if (pageIndex < maxPages) pageIndex++
            views.currentPage.text = "${getString(R.string.page)} $pageIndex"
            checkPages()
            viewModel.loadProjectTypes(pageIndex, pageSize)
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
        if (checkReload)
            when (it.asyncModify) {
                is Loading -> views.waitingView.visibility = View.VISIBLE
                is Fail -> views.waitingView.visibility = View.GONE
                is Success -> {
                    checkReload = false
                    views.waitingView.visibility = View.GONE
                    viewModel.loadProjectTypes(pageIndex, pageSize)
                }
            }

        when (it.asyncProjectsResponse) {
            is Loading -> views.waitingView.visibility = View.VISIBLE
            is Fail -> views.waitingView.visibility = View.GONE
            is Success -> {
                views.waitingView.visibility = View.GONE
                maxPages = it.asyncProjectsResponse.invoke().data.totalPages
                views.projectRecView.adapter =
                    ProjectAdapter(it.asyncProjectsResponse.invoke().data.content)
                checkPages()
            }
        }
    }

    inner class ProjectAdapter(
        private val list: List<Project>
    ) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder =
            ProjectViewHolder(
                ItemProjectBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    parent,
                    false
                )
            )


        override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
            holder.bind(list[position])
        }

        inner class ProjectViewHolder(private val binding: ItemProjectBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(project: Project) {
                binding.code.text = project.code
                binding.name.text = project.name
                binding.status.text = project.status

                when (project.status) {
                    "WORKING" -> binding.status.setTextColor(resources.getColor(R.color.purple_200))
                    "PENDING" -> binding.status.setTextColor(resources.getColor(R.color.red))
                    "FINISH" -> binding.status.setTextColor(resources.getColor(R.color.green))
                }

                binding.edit.setOnClickListener {
                    val dialog =
                        DialogEditProject(requireContext(), this@AdminProjectFragment, project)
                    dialog.show(requireActivity().supportFragmentManager, "edit_project")
                }

                binding.delete.setOnClickListener {
                    val dialog =
                        DialogConfirmDeleteProject(
                            requireContext(),
                            this@AdminProjectFragment,
                            project
                        )
                    dialog.show(requireActivity().supportFragmentManager, "delete_project")
                }
            }
        }
    }

    override fun notifyEditProject(
        id: String,
        code: String,
        name: String,
        status: String,
        desc: String
    ) {
        checkReload = true
        viewModel.editProject(id, code, name, status, desc)
    }

    override fun notifyAddProject(code: String, name: String, status: String, desc: String) {
        checkReload = true
        viewModel.addProject(code, name, status, desc)
    }

    override fun notifyDeleteProject(id: String) {
        checkReload = true
        viewModel.deleteProject(id)
    }

    override fun notifyEditTeam(id: String, name: String, code: String, desc: String) {}
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
    ) {}
}
