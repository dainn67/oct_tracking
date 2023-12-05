package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.response.ModifyResponse
import com.oceantech.tracking.data.model.response.CheckTokenResponse
import com.oceantech.tracking.data.model.response.DateListResponse
import com.oceantech.tracking.data.model.response.MemberResponse
import com.oceantech.tracking.data.model.response.ProjectResponse
import com.oceantech.tracking.data.model.response.TeamResponse
import com.oceantech.tracking.data.model.response.UserResponse
import com.oceantech.tracking.data.network.RemoteDataSource
import com.oceantech.tracking.data.network.UserApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    val api: UserApi
) {
    fun checkLogin(token: String?): Observable<CheckTokenResponse> {
        return api.checkToken(
            token ?: "null",
            RemoteDataSource.CHECK_TOKEN_AUTH,
            "application/x-www-form-urlencoded"
        ).subscribeOn(Schedulers.io())
    }

    fun getList(
        startDate: String?,
        endDate: String?,
        pageIndex: String?,
        pageSize: String?,
        token: String?
    ): Observable<DateListResponse> = api.getList(
        startDate, endDate, pageIndex, pageSize, token
    ).subscribeOn(Schedulers.io())

    fun getTrackingList(
        startDate: String?,
        endDate: String?,
        teamId: String?,
        memberId: String?,
        pageIndex: String?,
        pageSize: String?,
        token: String?
    ): Observable<DateListResponse> = api.getTrackingList(
        startDate, endDate, teamId, memberId, pageIndex, pageSize, token
    ).subscribeOn(Schedulers.io())

    fun getProjects(pageIndex: String, pageSize: String, token: String?): Observable<ProjectResponse> = api.getProjects(
        pageIndex, pageSize, auth = token
    ).subscribeOn(Schedulers.io())

    fun addProject(body: RequestBody, token: String?): Observable<ModifyResponse> = api.addProject(
        body, auth = token
    ).subscribeOn(Schedulers.io())

    fun editProject(id: String, body: RequestBody, token: String?): Observable<ModifyResponse> = api.updateProject(
        id, body, auth = token
    ).subscribeOn(Schedulers.io())

    fun deleteProject(prjId: String, token: String?): Observable<ModifyResponse> = api.deleteProject(
        prjId, auth = token
    ).subscribeOn(Schedulers.io())

    fun getTeams(pageIndex: String, pageSize: String, token: String?): Observable<TeamResponse> = api.getTeams(
        pageIndex, pageSize, auth = token
    ).subscribeOn(Schedulers.io())

    fun updateTeam(id: String, body: RequestBody, token: String?): Observable<ModifyResponse> = api.updateTeam(
        id, body, auth = token
    ).subscribeOn(Schedulers.io())

    fun getMembers(teamId: String?, pageIndex: String, pageSize: String, token: String?): Observable<MemberResponse> = api.getMembers(
        teamId, pageIndex, pageSize, auth = token
    ).subscribeOn(Schedulers.io())

    fun updateMember(id: String, body: RequestBody, token: String?): Observable<ModifyResponse> = api.updateMember(
        id, body, auth = token
    ).subscribeOn(Schedulers.io())

    fun getUsers(pageIndex: String, pageSize: String, token: String?): Observable<UserResponse> = api.getUsers(
        pageIndex, pageSize, auth = token
    ).subscribeOn(Schedulers.io())

    fun postTask(
        token: String?,
        body: RequestBody
    ): Observable<ModifyResponse> = api.postTask(token, body).subscribeOn(Schedulers.io())

    fun putTask(
        token: String?,
        dateId: String,
        body: RequestBody
    ): Observable<ModifyResponse> = api.putTask(
        token, dateId, body
    ).subscribeOn(Schedulers.io())
}