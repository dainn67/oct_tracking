package com.oceantech.tracking.ui.admin.projects

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.oceantech.tracking.data.model.Constants.Companion.TAG
import com.oceantech.tracking.data.model.response.Project
import com.oceantech.tracking.databinding.FragmentAdminProjectBinding
import com.oceantech.tracking.databinding.ItemProjectBinding
import com.oceantech.tracking.ui.admin.AdminViewEvent
import com.oceantech.tracking.ui.admin.AdminViewModel
import com.oceantech.tracking.utils.checkPages
import com.oceantech.tracking.utils.setupSpinner

@SuppressLint("SetTextI18n")
class AdminProjectFragment : TrackingBaseFragment<FragmentAdminProjectBinding>() {

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

        views.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadProjectTypes(pageIndex, pageSize)
            views.swipeRefreshLayout.isRefreshing = false
        }

        views.floatButton.setOnClickListener {
            val dialog = DialogEditOrAddNewProject(requireContext(), this)
            dialog.show(requireActivity().supportFragmentManager, "new_project")
        }

        viewModel.observeViewEvents {
            handleEvent(it)
        }
    }

    private fun handleEvent(it: AdminViewEvent) {
        when (it) {
            is AdminViewEvent.ResetLanguage -> {
                viewModel.loadProjectTypes(pageIndex, pageSize)

                views.tvRows.text = getString(R.string.rows)
                views.currentPage.text = getString(R.string.page) + " " + pageIndex

            }

            is AdminViewEvent.DataModified -> {
                Log.i(TAG, "Hi there")
                viewModel.loadProjectTypes(pageIndex, pageSize)
            }
        }
    }

    private fun setupSpinnerSize() {
        views.rows.setupSpinner( { position ->
            pageSize = ROWS_LIST[position]

            pageIndex = 1
            views.currentPage.text = "${getString(R.string.page)} 1"
            viewModel.loadProjectTypes(pageIndex, pageSize)
        }, ROWS_LIST)
    }

    private fun setupPages() {
        views.prevPage.setOnClickListener {
            if (pageIndex > 1) pageIndex--
            views.currentPage.text = "${getString(R.string.page)} $pageIndex"
            checkPages(maxPages, pageIndex, views.prevPage, views.nextPage)
            viewModel.loadProjectTypes(pageIndex, pageSize)
        }
        views.nextPage.setOnClickListener {
            if (pageIndex < maxPages) pageIndex++
            views.currentPage.text = "${getString(R.string.page)} $pageIndex"
            checkPages(maxPages, pageIndex, views.prevPage, views.nextPage)
            viewModel.loadProjectTypes(pageIndex, pageSize)
        }
    }

    override fun invalidate(): Unit = withState(viewModel) {
//        if (checkReload)
//            when (it.asyncModify) {
//                is Success -> {
//                    checkReload = false
//                    views.waitingView.visibility = View.GONE
//
//                }
//            }

        when (it.asyncProjectsResponse) {
            is Success -> {
                views.waitingView.visibility = View.GONE
                maxPages = it.asyncProjectsResponse.invoke().data.totalPages
                views.projectRecView.adapter =
                    ProjectAdapter(it.asyncProjectsResponse.invoke().data.content)
                checkPages(maxPages, pageIndex, views.prevPage, views.nextPage)
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
                        DialogEditOrAddNewProject(requireContext(), this@AdminProjectFragment, project)
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

    fun notifyEditProject(
        id: String,
        code: String,
        name: String,
        status: String,
        desc: String
    ) {
        checkReload = true
        viewModel.editProject(id, code, name, status, desc)
    }

    fun notifyAddProject(code: String, name: String, status: String, desc: String) {
        checkReload = true
        viewModel.addProject(code, name, status, desc)
    }

    fun notifyDeleteProject(id: String) {
        checkReload = true
        viewModel.deleteProject(id)
    }
}
