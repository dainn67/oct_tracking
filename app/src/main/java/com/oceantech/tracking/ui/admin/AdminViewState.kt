package com.oceantech.tracking.ui.admin

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.response.DateListResponse
import com.oceantech.tracking.data.model.response.MemberResponse
import com.oceantech.tracking.data.model.response.ModifyTaskResponse
import com.oceantech.tracking.data.model.response.Project
import com.oceantech.tracking.data.model.response.ProjectTypeResponse
import com.oceantech.tracking.data.model.response.TeamResponse

data class AdminViewState(
    val asyncListResponse: Async<DateListResponse> = Uninitialized,
    val asyncProjects: Async<List<Project>> = Uninitialized,
    val asyncProjectTypes: Async<ProjectTypeResponse> = Uninitialized,
    val asyncTeamResponse: Async<TeamResponse> = Uninitialized,
    val asyncMemberResponse: Async<MemberResponse> = Uninitialized,

    val asyncModify: Async<ModifyTaskResponse> = Uninitialized

) : MvRxState {
    fun isPersonnelFilterLoading() = asyncTeamResponse is Loading || asyncMemberResponse is Loading
}