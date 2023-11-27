package com.oceantech.tracking.ui.admin

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.response.DateListResponse
import com.oceantech.tracking.data.model.response.MemberResponse
import com.oceantech.tracking.data.model.response.ModifyResponse
import com.oceantech.tracking.data.model.response.ProjectResponse
import com.oceantech.tracking.data.model.response.TeamResponse

data class AdminViewState(
    val asyncListResponse: Async<DateListResponse> = Uninitialized,
    val asyncProjectsResponse: Async<ProjectResponse> = Uninitialized,
    val asyncTeamResponse: Async<TeamResponse> = Uninitialized,
    val asyncMemberResponse: Async<MemberResponse> = Uninitialized,

    val asyncModify: Async<ModifyResponse> = Uninitialized

) : MvRxState {}