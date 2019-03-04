package com.gamex.network;

import com.gamex.models.Exhibition;
import com.gamex.models.ExhibitionDetails;
import com.gamex.utils.Constant;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GetDataService {
    @GET("api/Exhibition")
    Call<List<Exhibition>> getAllExhibition();

    // login
    @FormUrlEncoded
    @POST("api/account/login")
    Call<ResponseBody> loginAccount(@Field("username") String username, @Field("password") String password);

    // account info
    @PUT("api/account/{username}")
    Call<ResponseBody> getAccountInfo(@Path("username")String username);

    // exhibition details
    @GET("api/exhibition/{exhibitionId}")
    Call<ExhibitionDetails> getEXhibitionDetails(@Path("exhibitionId") String exhibitionId);

}
