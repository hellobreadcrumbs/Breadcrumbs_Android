package com.breadcrumbsapp.interfaces

import com.breadcrumbsapp.model.*
import com.breadcrumbsapp.view.rewards.GetRewardsDataModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
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
    ): Response<GetTrailsModel>
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

    @POST("v1/api/discover")
    suspend fun discoverPOI(@Body requestBody: RequestBody): Response<ResponseBody>

    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/get_user")
    suspend fun getUserDetails(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<GetUserModel>

    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/get_rankings")
    suspend fun getRankingDetails(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<GetRankingModel>


    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/get_user_ranking")
    suspend fun getUserRankingDetails(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<GetUserRankingModel>

    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/get_feed") // its for feed screen only. In React code., they used for feed screen only
    suspend fun getFeedDetails(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<GetFeedDataModel>

    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/get_my_feed") // For creator post and My Profile- My Post.
    suspend fun getMyFeedDetails(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<GetMyFeedModel>

    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/get_recommended_friends")
    suspend fun getRecommendedFriends(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<RecommendedFriendsModel>

    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/get_friends")
    suspend fun getUserFriendList(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<GetFriendsListModel>


    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/get_user_achievements")
    suspend fun getUserAchievements(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<GetUserAchievementsModel>


    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/react_post")
    suspend fun getFeedPostLikeDetails(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<ResponseBody>


    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/add_friend")
    suspend fun addFriend(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<ResponseBody>

    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/un_friend")
    suspend fun unFriend(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<ResponseBody>


    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/process_friend_request")
    suspend fun acceptORCancelFriendRequest(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<JsonObject>

    //update_profile

    @Multipart
    @POST("v1/api/update_profile")
    suspend fun updateProfile(
        @Header("Authorization") h1: String,
        @Part("id") id: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<JsonObject>

    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/begin_challenge")
    suspend fun beginChallenge(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    )

    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/begin_set_challenge")
    suspend fun beginSetChallenge(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    )


    @Multipart
    @POST("v1/api/begin_selfie_challenge")
    suspend fun uploadSelfieImage(
        @Header("Authorization") h1: String,
        @Part("user_id") id: RequestBody,
        @Part("poi_id") poi_id: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<JsonObject>

    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/add_survey")
    suspend fun addSurvey(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<ResponseBody>

    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/get_rewards")
    suspend fun getRewardList(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<GetRewardsDataModel>

}