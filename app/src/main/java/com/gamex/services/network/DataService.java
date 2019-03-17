package com.gamex.services.network;

import com.gamex.models.Company;
import com.gamex.models.Exhibition;
import com.gamex.models.History;
import com.gamex.models.Profile;
import com.gamex.models.Reward;
import com.gamex.models.RewardHistory;
import com.gamex.models.Survey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface DataService {

    // Login
    @FormUrlEncoded
    @POST("/token")
    @Headers("Content-Type:application/x-www-form-urlencoded")
    Call<ResponseBody> login(
            @Field("grant_type") String grantType,
            @Field("username") String username,
            @Field("password") String password
    );

    // register
    @POST("api/account")
    Call<ResponseBody> register(@Body HashMap<String, String> user);

    // login with FB 1: Get external provider
    @GET("/api/Account/ExternalLogins?returnUrl=%2F&generateState=false")
    Call<ResponseBody> fbGetExternalProvider();

    // login with FB 2: Get user info by access token
    @GET("/api/account/userinfo")
    Call<ResponseBody> fbGetUserInfo(@Header("Authorization") String accessToken);

    // login with FB 3: Register External
    @POST("/api/Account/RegisterExternal")
    Call<ResponseBody> fbRegisterExternal(
            @Header("Authorization") String accessToken,
            @Header("Cookie") String cookie
    );

    // Scan QR Check in
    @FormUrlEncoded
    @POST("/api/user/exhibition")
    @Headers("Content-Type:application/x-www-form-urlencoded")
    Call<ResponseBody> scanCheckInEvent(
            @Header("Authorization") String accessToken,
            @Field("id") String id
    );

    // Scan QR survey
    @GET("/api/surveys")
    Call<List<Survey>> scanGetSurveys(
            @Header("Authorization") String accessToken,
            @QueryMap Map<String, String> params
    );

    // Main screen exhibition
    @GET("/api/exhibitions")
    Call<List<Exhibition>> getExhibitionsList(
            @Header("Authorization") String accessToken,
            @QueryMap Map<String, Object> options
    );

    // Exhibition details
    @GET("/api/exhibition")
    Call<Exhibition> getExhibitionDetails(
            @Header("Authorization") String accessToken,
            @Query("id") String id
    );

    // Company details
    @GET("/api/company")
    Call<Company> getCompanyDetails(
            @Header("Authorization") String accessToken,
            @Query("id") String id
    );

    // Survey details (question)
    @GET("/api/survey")
    Call<Survey> getSurveyQuestions(
            @Header("Authorization") String accessToken,
            @Query("id") int surveyId
    );

    // Submit survey
    @POST("/api/survey")
    @Headers("Content-Type:application/json")
    Call<ResponseBody> submitSurvey(
            @Header("Authorization") String accessToken,
            @Body RequestBody answer
    );

    // Activity history
    @GET("/api/activity")
    Call<List<History>> getActivityHistory(
            @Header("Authorization") String accessToken,
            @QueryMap Map<String, Object> options
    );

    // Change password
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @PUT("/api/account/password")
    Call<ResponseBody> changePassword(
            @Header("Authorization") String accessToken,
            @Field("oldPassword") String oldPass,
            @Field("newPassword") String newPass,
            @Field("confirmPassword") String rePass
    );

    // Get profile
    @GET("/api/account")
    Call<Profile> getProfile(
            @Header("Authorization") String accessToken
    );

    // Update Profile
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @PUT("/api/account")
    Call<ResponseBody> updateProfile(
            @Header("Authorization") String accessToken,
            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("username") String username
    );

    // Rewards list all
    @GET("/api/rewards")
    Call<List<Reward>> getListRewards(
            @Header("Authorization") String accessToken
    );

    // Reward Exchange
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/api/reward")
    Call<ResponseBody> exchangeReward(
            @Header("Authorization") String accessToken,
            @Field("id") int id
    );

    // Reward history
    @GET("/api/reward/history")
    Call<List<RewardHistory>> getRewardHistory(
            @Header("Authorization") String accessToken
    );

    // Reward point
    @GET("/api/reward/point")
    Call<ResponseBody> getRewardPoint(
            @Header("Authorization") String accessToken
    );

    // Bookmark exhibition
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/api/bookmark/exhibition")
    Call<ResponseBody> bookmarkExhibition(
            @Header("Authorization") String accessToken,
            @Field("id") String id
    );

    // Bookmark company
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/api/bookmark/company")
    Call<ResponseBody> bookmarkCompany(
            @Header("Authorization") String accessToken,
            @Field("id") String id
    );

    // Remove bookmark exhibition
    @Headers("Content-Type:application/json")
//    @DELETE("/api/bookmark/exhibition")
    @HTTP(method = "DELETE", path = "/api/bookmark/exhibition", hasBody = true)
    Call<ResponseBody> removeBookmarkExhibition(
            @Header("Authorization") String accessToken,
            @Body RequestBody body
    );

    // Remove bookmark exhibition
    @Headers("Content-Type:application/json")
    @DELETE("/api/bookmark/company")
    Call<ResponseBody> removeBookmarkCompany(
            @Header("Authorization") String accessToken,
            @Body RequestBody body
    );


}
