package com.breadcrumbsapp.view.rewards

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface RewardsService {
    @Headers("Accept:application/json", "Content-Type:application/json;")
    @POST("v1/api/get_rewards_all")
    suspend fun getUserRewardsList(
        @Header("Authorization") h1: String,
        @Body requestBody: RequestBody
    ): Response<GetRewardsDataModel>

}