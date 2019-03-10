package com.gamex.network;

import com.gamex.models.Company;
import com.gamex.models.Exhibition;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface DataService {
    @GET("api/Exhibition")
    Call<List<Exhibition>> getAllExhibition();

    // login
//    @FormUrlEncoded
//    @POST("api/account/login")
//    Call<ResponseBody> loginAccount(
//            @Field("username") String username,
//            @Field("password") String password);

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

//     login with FB 1: Get external provider
    @GET("/api/Account/ExternalLogins?returnUrl=%2F&generateState=false")
    Call<ResponseBody> getExternalProvider();

    // login with FB 2: open FB view
    @GET
    Call<ResponseBody> getFacebookWebView(@Url String facebook);
//    Call<ResponseBody> getFacebookWebView(@Path(value="facebook", encoded=true) String facebook);

    @GET
    Call<ResponseBody> getFacebookGraph(@Url String url);

    // login with FB
    @Headers({"Content-Type:application/json"})
    @POST("/api/Account/RegisterExternal")
    Call<ResponseBody> loginWithFB(
            @Header("Authorization") String auth,
            @Body HashMap<String, String> user
//            @Field("Username") String username,
//            @Field("FirstName") String firstname,
//            @Field("LastName") String lastname
            );

    // account info
    @PUT("api/account/{username}")
    Call<ResponseBody> getAccountInfo(@Path("username")String username);

    // exhibition details
    @GET("api/exhibition/{exhibitionId}")
    Call<Exhibition> getExhibitionDetails(@Path("exhibitionId") String exhibitionId);

    @GET("api/exhibition/{exhibitionId}/company")
    Call<List<Company>> getListCompanyOfExhibition(@Path("exhibitionId") String exhibitionId);
}