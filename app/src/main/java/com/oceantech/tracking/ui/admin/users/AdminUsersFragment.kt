package com.oceantech.tracking.ui.admin.users

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
import com.oceantech.tracking.data.model.response.User
import com.oceantech.tracking.databinding.FragmentAdminUsersBinding
import com.oceantech.tracking.databinding.ItemUserBinding
import com.oceantech.tracking.ui.admin.AdminViewModel
import com.oceantech.tracking.ui.admin.projects.DialogEditProject

class AdminUsersFragment : TrackingBaseFragment<FragmentAdminUsersBinding>() {
    private val viewModel: AdminViewModel by activityViewModel()

    private var pageIndex: Int = 1
    private var pageSize: Int = 10
    private var maxPages: Int = 0
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAdminUsersBinding = FragmentAdminUsersBinding.inflate(
        inflater, container, false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinnerSize()
        setupPages()

        views.usersRecView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.loadUsers()

        views.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadUsers(pageIndex, pageSize)
            views.swipeRefreshLayout.isRefreshing = false
        }

        views.floatButton.setOnClickListener {
            val dialog = DialogAddNewUser(requireContext(), this)
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
                views.currentPage.text = "${getString(com.oceantech.tracking.R.string.page)} 1"
                viewModel.loadUsers(pageIndex, pageSize)
            }
        }
    }

    private fun setupPages() {
        views.prevPage.setOnClickListener {
            if (pageIndex > 1) pageIndex--
            views.currentPage.text = "${getString(com.oceantech.tracking.R.string.page)} $pageIndex"
//            checkPages()
            viewModel.loadUsers(pageIndex, pageSize)
        }
        views.nextPage.setOnClickListener {
            if (pageIndex < maxPages) pageIndex++
            views.currentPage.text = "${getString(com.oceantech.tracking.R.string.page)} $pageIndex"
//            checkPages()
            viewModel.loadUsers(pageIndex, pageSize)
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
        when (it.asyncUserResponse) {
            is Loading -> views.waitingView.visibility = View.VISIBLE
            is Fail -> views.waitingView.visibility = View.GONE
            is Success -> {
                views.waitingView.visibility = View.GONE
                views.usersRecView.adapter = UserAdapter(it.asyncUserResponse.invoke().data.content)
                maxPages = it.asyncUserResponse.invoke().data.totalPages
                checkPages()
            }
        }

        when (it.asyncModify) {
            is Loading -> views.waitingView.visibility = View.VISIBLE
            is Fail -> views.waitingView.visibility = View.GONE
            is Success -> views.waitingView.visibility = View.GONE
        }
    }

    fun addNewUser(username: String, email: String, gender: String, roles: List<String>, password: String){
        viewModel.addNewUser(pageIndex, pageSize, username, email, gender, roles, password)
    }

    inner class UserAdapter(
        private val list: List<User>
    ) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
            UserViewHolder(
                ItemUserBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    parent,
                    false
                )
            )

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            holder.bind(list[position])
        }

        inner class UserViewHolder(private val binding: ItemUserBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(user: User) {
                binding.username.text = "${user.username}"
                binding.email.text = "${getString(R.string.email)}: ${user.email}"

                binding.edit.setOnClickListener {
                    val dialog = DialogEditUser(requireContext(), user)
                    dialog.show(requireActivity().supportFragmentManager, "edit_user")
                }
            }
        }
    }
}
