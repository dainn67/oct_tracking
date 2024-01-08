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
import com.oceantech.tracking.utils.checkPages
import com.oceantech.tracking.utils.setupSpinner

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
        setupSpinner(views.rows, {position ->
            pageSize = optionSizes[position]
        }, optionSizes)
    }

    private fun setupPages() {
        views.prevPage.setOnClickListener {
            if (pageIndex > 1) pageIndex--
            views.currentPage.text = "${getString(com.oceantech.tracking.R.string.page)} $pageIndex"
            checkPages(maxPages, pageIndex, views.prevPage, views.nextPage)
            viewModel.loadUsers(pageIndex, pageSize)
        }
        views.nextPage.setOnClickListener {
            if (pageIndex < maxPages) pageIndex++
            views.currentPage.text = "${getString(com.oceantech.tracking.R.string.page)} $pageIndex"
            checkPages(maxPages, pageIndex, views.prevPage, views.nextPage)
            viewModel.loadUsers(pageIndex, pageSize)
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
                checkPages(maxPages, pageIndex, views.prevPage, views.nextPage)
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

    fun deleteUser(uId: Int) {
        viewModel.deleteUser(uId, pageIndex, pageSize)
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

                binding.delete.setOnClickListener {
                    val dialog = DialogConfirmDeleteUser(requireContext(), this@AdminUsersFragment, user)
                    dialog.show(requireActivity().supportFragmentManager, "delete_user")
                }
            }
        }
    }
}
