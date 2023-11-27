package com.oceantech.tracking.ui.admin

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.google.gson.Gson
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.Constants.Companion.TAG
import com.oceantech.tracking.data.model.response.Project
import com.oceantech.tracking.data.model.response.Task
import com.oceantech.tracking.data.network.RemoteDataSource
import com.oceantech.tracking.data.repository.UserRepository
import com.oceantech.tracking.ui.security.UserPreferences
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class AdminViewModel @AssistedInject constructor(
    @Assisted state: AdminViewState,
    val repository: UserRepository,
) : TrackingViewModel<AdminViewState, HomeViewAction, AdminViewEvent>(state) {
    @Inject
    lateinit var userPref: UserPreferences

    private lateinit var projectList: List<Project>
    private lateinit var projectTypeList: MutableList<String>

    private val mediaType = RemoteDataSource.DEFAULT_CONTENT_TYPE.toMediaTypeOrNull()
    private var accessToken: String? = null
    var language: Int = 1

    override fun handle(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.ResetLang -> handResetLang()
            else -> {}
        }
    }

    private fun handResetLang() {
        _viewEvents.post(AdminViewEvent.ResetLanguage)
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: AdminViewState): AdminViewModel
    }

    companion object : MvRxViewModelFactory<AdminViewModel, AdminViewState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: AdminViewState
        ): AdminViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

    fun initLoad() {
        viewModelScope.launch {
            val job = async { userPref.accessToken.firstOrNull() }
            accessToken = job.await()

            val startCalendar = Calendar.getInstance()
            startCalendar.set(Calendar.DAY_OF_MONTH, 1)
            val endCalendar = Calendar.getInstance()
            endCalendar.set(
                Calendar.DAY_OF_MONTH,
                endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            )

            reload(startCalendar, endCalendar) //load default tracking list
            loadProjectTypes()
            loadTeams()
            loadMembers()
        }
    }

    private fun loadTrackingList(
        startDate: String,
        endDate: String,
        teamId: String?,
        memberId: String?,
        pageIndex: Int,
        pageSize: Int
    ) {
        setState { copy(asyncListResponse = Loading()) }

        Log.i(TAG, "Param: $startDate $endDate $teamId $memberId $pageIndex $pageSize")
        repository.getTrackingList(
            startDate, endDate, teamId, memberId, pageIndex.toString(), pageSize.toString(),
            "Bearer $accessToken"
        ).execute {
            copy(asyncListResponse = it)
        }
    }

    fun loadProjectTypes(pageIndex: Int = 1, pageSize: Int = 10) {
        setState { copy(asyncProjectsResponse = Loading()) }

        repository.getProjects(pageIndex.toString(), pageSize.toString(), "Bearer $accessToken")
            .execute {
                projectTypeList = mutableListOf()

                it.invoke()?.data?.content?.forEach { it1 ->
                    projectTypeList.add(it1.code)
                }

                //get the list of projects if successful
                it.invoke()?.data?.content?.let { it1 ->
                    projectList = it1
                }

                copy(asyncProjectsResponse = it)
            }
    }

    fun addProject(code: String, name: String, status: String, desc: String) {
        setState { copy(asyncModify = Loading()) }

        val newProject = Project(
            name = name,
            code = code,
            status = status.toUpperCase(),
            description = desc,
            tasks = null
        )
        repository.addProject(
            RequestBody.create(mediaType, Gson().toJson(newProject)),
            "Bearer $accessToken"
        ).execute {
            copy(asyncModify = it)
        }
    }

    fun editProject(id: String, code: String, name: String, status: String, desc: String) {
        setState { copy(asyncModify = Loading()) }

        val newProject = Project(id, name, code, status.toUpperCase(), desc, null)
        Log.i(TAG, "$id $code $name ${status.toUpperCase()} $desc")
        repository.editProject(
            id,
            RequestBody.create(mediaType, Gson().toJson(newProject)),
            "Bearer $accessToken"
        ).execute {
            copy(asyncModify = it)
        }
    }

    fun deleteProject(id: String) {
        setState { copy(asyncModify = Loading()) }

        repository.deleteProject(id, "Bearer $accessToken").execute {
            copy(asyncModify = it)
        }
    }

    private fun loadTeams() {
        setState { copy(asyncTeamResponse = Loading()) }

        repository.getTeams("Bearer $accessToken").execute {
            copy(asyncTeamResponse = it)
        }
    }

    private fun loadMembers(teamId: String? = null) {
        setState { copy(asyncMemberResponse = Loading()) }

        repository.getMembers("Bearer $accessToken", teamId).execute {
            copy(asyncMemberResponse = it)
        }
    }

    fun reload(
        fromDate: Calendar,
        toDate: Calendar,
        teamId: String? = null,
        memberId: String? = null,
        pageIndex: Int = 1,
        pageSize: Int = 10
    ) {
        var year = fromDate.get(Calendar.YEAR)
        var month = fromDate.get(Calendar.MONTH)
        var day = fromDate.get(Calendar.DAY_OF_MONTH)
        val fromDateString =
            "$year-${if (month < 9) "0${month + 1}" else month + 1}-${if (day < 10) "0$day" else day}"

        year = toDate.get(Calendar.YEAR)
        month = toDate.get(Calendar.MONTH)
        day = toDate.get(Calendar.DAY_OF_MONTH)
        val toDateString =
            "$year-${if (month < 9) "0${month + 1}" else month + 1}-${if (day < 10) "0$day" else day}"

        loadTrackingList(fromDateString, toDateString, teamId, memberId, pageIndex, pageSize)
        if (teamId != null) loadMembers(teamId)
    }

    fun getTotalHour(tasks: List<Task>?): String {
        if (tasks.isNullOrEmpty()) return 0.0.toString()

        var res = 0.0;
        for (task in tasks) res += task.officeHour + task.overtimeHour
        return res.toString()
    }
}