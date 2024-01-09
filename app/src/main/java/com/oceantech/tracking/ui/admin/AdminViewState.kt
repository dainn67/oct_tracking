package com.oceantech.tracking.ui.admin

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.response.DateListResponse
import com.oceantech.tracking.data.model.response.MemberResponse
import com.oceantech.tracking.data.model.response.ModifyResponse
import com.oceantech.tracking.data.model.response.ProjectResponse
import com.oceantech.tracking.data.model.response.TeamResponse
import com.oceantech.tracking.data.model.response.UserResponse

data class AdminViewState(
    val asyncListResponse: Async<DateListResponse> = Uninitialized,
    val asyncProjectsResponse: Async<ProjectResponse> = Uninitialized,
    val asyncTeamResponse: Async<TeamResponse> = Uninitialized,
    val asyncMemberResponse: Async<MemberResponse> = Uninitialized,
    val asyncUserResponse: Async<UserResponse> = Uninitialized,
    val asyncModify: Async<ModifyResponse> = Uninitialized
) : MvRxState {
    fun isLoading() = asyncListResponse is Loading
            || asyncProjectsResponse is Loading
            || asyncTeamResponse is Loading
            || asyncMemberResponse is Loading
            || asyncUserResponse is Loading
            || asyncModify is Loading

    fun isFailed() = asyncListResponse is Fail
            || asyncProjectsResponse is Fail
            || asyncTeamResponse is Fail
            || asyncMemberResponse is Fail
            || asyncUserResponse is Fail
            || asyncModify is Fail
}