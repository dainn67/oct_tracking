package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.response.ModifyTaskResponse
import com.oceantech.tracking.data.model.response.CheckTokenResponse
import com.oceantech.tracking.data.model.response.DateListResponse
import com.oceantech.tracking.data.model.response.MemberResponse
import com.oceantech.tracking.data.model.response.ProjectTypeResponse
import com.oceantech.tracking.data.model.response.TeamResponse
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface UserApi {
    @GET("api/v1/reports/staff/page")
    fun getList(
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
        @Query("pageIndex") pageIndex: String?,
        @Query("pageSize") pageSize: String?,
        @Header("Authorization") auth: String?
    ): Observable<DateListResponse>

    //?startDate=2023-11-01&endDate=2023-11-30&teamId=&memberId=&pageIndex=1&pageSize=10
    @GET("api/v1/reports/page")
    fun getTrackingList(
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
        @Query("teamId") teamId: String?,
        @Query("memberId") memberId: String?,
        @Query("pageIndex") pageIndex: String?,
        @Query("pageSize") pageSize: String?,
        @Header("Authorization") auth: String?
    ): Observable<DateListResponse>


    @POST("oauth/check_token")
    fun checkToken(
        @Query("token") token: String,
        @Header("Authorization") auth: String,
        @Header("Content-Type") accept: String
    ): Observable<CheckTokenResponse>

    @GET("api/v1/projects/page")
    fun getProjects(
        @Query("pageIndex") pageIndex: String = "1",
        @Query("pageSize") pageSize: String = "1000",
        @Query("keyword") keyword: String = "",
        @Header("Authorization") auth: String?
    ): Observable<ProjectTypeResponse>

    @GET("api/v1/teams/page")
    fun getTeams(
        @Query("pageIndex") pageIndex: String = "1",
        @Query("pageSize") pageSize: String = "1000",
        @Header("Authorization") auth: String?
    ): Observable<TeamResponse>

    @GET("api/v1/members/page")
    fun getMembers(
        @Query("pageIndex") pageIndex: String = "1",
        @Query("pageSize") pageSize: String = "1000",
        @Query("teamId") teamId: String?,
        @Header("Authorization") auth: String?
    ): Observable<MemberResponse>

    @POST("api/v1/reports/")
    fun postTask(
        @Header("Authorization") auth: String?,
        @Body body: RequestBody
    ): Observable<ModifyTaskResponse>

    @PUT("api/v1/reports/{dateId}")
    fun putTask(
        @Header("Authorization") auth: String?,
        @Path("dateId") dateId: String,
        @Body body: RequestBody
    ): Observable<ModifyTaskResponse>
}