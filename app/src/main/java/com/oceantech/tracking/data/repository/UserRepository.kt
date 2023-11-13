package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.response.ModifyTaskResponse
import com.oceantech.tracking.data.model.response.CheckTokenResponse
import com.oceantech.tracking.data.model.response.DateListResponse
import com.oceantech.tracking.data.model.response.ProjectTypeResponse
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

    fun getProjects(token: String?): Observable<ProjectTypeResponse> = api.getProjects(
        auth = token
    ).subscribeOn(Schedulers.io())

    fun postTask(
        token: String?,
        body: RequestBody
    ): Observable<ModifyTaskResponse> = api.postTask(token, body).subscribeOn(Schedulers.io())

    fun putTask(
        token: String?,
        dateId: String,
        body: RequestBody
    ): Observable<ModifyTaskResponse> = api.putTask(
        token, dateId, body
    ).subscribeOn(Schedulers.io())
}