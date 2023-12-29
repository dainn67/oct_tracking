package com.oceantech.tracking.ui.admin

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.google.gson.Gson
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.Constants
import com.oceantech.tracking.data.model.Constants.Companion.TAG
import com.oceantech.tracking.data.model.response.Member
import com.oceantech.tracking.data.model.response.Project
import com.oceantech.tracking.data.model.response.Task
import com.oceantech.tracking.data.model.response.Team
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
import javax.inject.Inject

class AdminViewModel @AssistedInject constructor(
    @Assisted state: AdminViewState,
    val repository: UserRepository,
) : TrackingViewModel<AdminViewState, HomeViewAction, AdminViewEvent>(state) {
    @Inject
    lateinit var userPref: UserPreferences

    var teamList: List<Team>? = null

    private val gson = Gson()

    private lateinit var projectList: List<Project>
    private lateinit var projectTypeList: MutableList<String>

    private val mediaType = RemoteDataSource.DEFAULT_CONTENT_TYPE.toMediaTypeOrNull()
    private var accessToken: String? = null
    var language: Int = 1

    override fun handle(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.ResetLang -> handResetLang()
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

            reloadTracking(startCalendar, endCalendar) //load default tracking list
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
            RequestBody.create(mediaType, gson.toJson(newProject)),
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
            RequestBody.create(mediaType, gson.toJson(newProject)),
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

    fun loadTeams(pageIndex: Int = 1, pageSize: Int = 1000) {
        Log.i(TAG, "loadTeams")
        setState { copy(asyncTeamResponse = Loading()) }

        repository.getTeams(pageIndex.toString(), pageSize.toString(), "Bearer $accessToken")
            .execute {
                teamList = asyncTeamResponse.invoke()?.data?.content
                copy(asyncTeamResponse = it)
            }
    }

    fun editTeam(id: String, name: String, code: String, desc: String, pageIndex: Int, pageSize: Int) {
        setState { copy(asyncModify = Loading()) }

        val body = RequestBody.create(
            mediaType,
            gson.toJson(Team(name = name, code = code, description = desc))
        )
        repository.updateTeam(id, body, "Bearer $accessToken").execute {
            loadTeams(1, 10)
            copy(asyncModify = it)
        }
    }

    fun loadMembers(teamId: String? = null, pageIndex: Int = 1, pageSize: Int = 1000) {
        Log.i(TAG, "loadMember")
        setState {copy(asyncMemberResponse = Loading()) }

        repository.getMembers(
            teamId,
            pageIndex.toString(),
            pageSize.toString(),
            "Bearer $accessToken"
        ).execute {
            if(!teamId.isNullOrEmpty() && !teamList.isNullOrEmpty()) {
                for(team in teamList!!){
                    if(team.id == teamId) team.members = asyncMemberResponse.invoke()?.data?.content
                    break
                }
            }
//            members = asyncMemberResponse.invoke()?.data?.content
            copy(asyncMemberResponse = it)
        }
    }

    fun editMember(
        teamId: String?,
        pageIndex: Int,
        pageSize: Int,
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
    ){
        val newMember = Member(
            name = name,
            code = code,
            email = email,
            dateJoin = dateJoin,
            gender = gender,
            level = level,
            position = position,
            status = status,
            team = team,
            type = type
        )
        setState { copy(asyncModify = Loading()) }
        val body = RequestBody.create(mediaType, gson.toJson(newMember))

        repository.updateMember(id, body, "Bearer $accessToken").execute {
            loadMembers(teamId, pageIndex, pageSize)
            copy(asyncModify = it)
        }
    }

    fun loadUsers(pageIndex: Int = 1, pageSize: Int = 10) {
        setState { copy(asyncUserResponse = Loading()) }

        repository.getUsers(pageIndex.toString(), pageSize.toString(), "Bearer $accessToken")
            .execute {
                copy(asyncUserResponse = it)
            }
    }

    fun reloadTracking(
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