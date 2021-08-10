package com.breadcrumbsapp.interfaces

import com.breadcrumbsapp.model.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface APIService {


    @POST("v1/api/social_register")
    suspend fun socialRegister(@Body requestBody: RequestBody): Response<ResponseBody>


    @POST("v1/api/social_login")
    suspend fun socialLogin(@Body requestBody: RequestBody): Response<ResponseBody>

    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/get_trails")
    suspend fun getTrailsList(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<DiscoverScreenModel>
    // suspend fun getTrailsList(@Body requestBody: RequestBody): Response<ResponseBody>


    @GET("/maps/api/directions/json")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String
    )


    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/get_events")
    suspend fun getEventsList(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<GetEventsModel>


    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/begin_selfie_challenge")
    suspend fun beginSelfieChallenge(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<ResponseBody>


    @POST("v1/api/discover")
    suspend fun discoverPOI(@Body requestBody: RequestBody): Response<ResponseBody>


    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/get_user")
    suspend fun getUserDetails(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<GetUserModel>


    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/get_feed")
    suspend fun getFeedDetails(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<GetFeedDataModel>

    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/get_rankings")
    suspend fun getRankingDetails(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<GetRankingModel>



}