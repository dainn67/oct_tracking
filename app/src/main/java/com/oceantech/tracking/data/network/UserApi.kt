package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.response.ModifyResponse
import com.oceantech.tracking.data.model.response.CheckTokenResponse
import com.oceantech.tracking.data.model.response.DateListResponse
import com.oceantech.tracking.data.model.response.MemberResponse
import com.oceantech.tracking.data.model.response.ProjectResponse
import com.oceantech.tracking.data.model.response.TeamResponse
import com.oceantech.tracking.data.model.response.UserResponse
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
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
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int,
    ): Observable<DateListResponse>

    @GET("api/v1/reports/page")
    fun getTrackingList(
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
        @Query("teamId") teamId: String?,
        @Query("memberId") memberId: String?,
        @Query("pageIndex") pageIndex: String?,
        @Query("pageSize") pageSize: String?,
    ): Observable<DateListResponse>


    @GET("api/v1/projects/page")
    fun getProjects(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int,
        @Query("keyword") keyword: String = "",
    ): Observable<ProjectResponse>

    @POST("api/v1/projects")
    fun addProject(
        @Body body: RequestBody,
    ): Observable<ModifyResponse>

    @PUT("api/v1/projects/{prjId}")
    fun updateProject(
        @Path("prjId") prjId: String,
        @Body body: RequestBody,
    ): Observable<ModifyResponse>

    @DELETE("api/v1/projects/{prjId}")
    fun deleteProject(
        @Path("prjId") prjId: String,
    ): Observable<ModifyResponse>

    @GET("api/v1/teams/page")
    fun getTeams(
        @Query("pageIndex") pageIndex: String,
        @Query("pageSize") pageSize: String,
    ): Observable<TeamResponse>

    @PUT("api/v1/teams/{teamId}")
    fun updateTeam(
        @Path("teamId") id: String,
        @Body body: RequestBody,
    ): Observable<ModifyResponse>

    @PUT("api/v1/members/{memberId}")
    fun updateMember(
        @Path("memberId") id: String,
        @Body body: RequestBody,
    ): Observable<ModifyResponse>

    @GET("api/v1/members/page")
    fun getMembers(
        @Query("teamId") teamId: String?,
        @Query("pageIndex") pageIndex: String,
        @Query("pageSize") pageSize: String,
    ): Observable<MemberResponse>

    @GET("api/v1/users/page")
    fun getUsers(
        @Query("pageIndex") pageIndex: String,
        @Query("pageSize") pageSize: String,
    ): Observable<UserResponse>

    @DELETE("api/v1/users/{uId}")
    fun deleteUser(
        @Path("uId") uId: Int
    ): Observable<ModifyResponse>

    @POST("api/v1/reports/")
    fun postTask(
        @Body body: RequestBody
    ): Observable<ModifyResponse>

    @PUT("api/v1/reports/{dateId}")
    fun putTask(
        @Path("dateId") dateId: String,
        @Body body: RequestBody
    ): Observable<ModifyResponse>

    @POST("api/v1/users")
    fun addNewUser(
        @Body body: RequestBody
    ): Observable<ModifyResponse>
}