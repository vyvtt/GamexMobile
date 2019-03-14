package com.gamex.services.network;

import com.gamex.models.Exhibition;
import com.gamex.models.Survey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface DataService {

    // Login
    @FormUrlEncoded
    @POST("/token")
    @Headers("Content-Type:application/x-www-form-urlencoded")
    Call<ResponseBody> loginAccount(
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
    Call<ResponseBody> checkInEvent(
            @Header("Authorization") String accessToken,
            @Field("id") String id
    );

    // Scan QR survey

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

    // Survey details (question)
    @GET("/api/survey")
    Call<Survey> getSurveyQuestions(
            @Header("Authorization") String accessToken,
            @Query("id") int surveyId
    );

    @POST("/api/survey")
    Call<ResponseBody> submitSurvey(
            @Header("Authorization") String accessToken,
            @Body RequestBody answer
            );

//    // login with FB
//    @Headers({"Content-Type:application/json"})
//    @POST("/api/Account/RegisterExternal")
//    Call<ResponseBody> loginWithFB(
//            @Header("Authorization") String auth,
//            @Body HashMap<String, String> user
//    );

//    // account info
//    @PUT("api/account/{username}")
//    Call<ResponseBody> getAccountInfo(@Path("username") String username);

//    // exhibition details
//    @GET("api/exhibition/{exhibitionId}")
//    Call<Exhibition> getExhibitionDetails(@Path("exhibitionId") String exhibitionId);

//    @GET("api/exhibition/{exhibitionId}/company")
//    Call<List<Company>> getListCompanyOfExhibition(@Path("exhibitionId") String exhibitionId);
}
