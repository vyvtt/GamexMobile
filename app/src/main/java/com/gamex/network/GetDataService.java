package com.gamex.network;

import com.gamex.models.Exhibition;
import com.gamex.utils.Constant;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GetDataService {
    @GET(Constant.API_EXHIBITION)
    Call<List<Exhibition>> getAllExhibition();

    @FormUrlEncoded
    @POST(Constant.API_LOGIN)
    Call<ResponseBody> loginAccount(@Field("username") String username, @Field("password") String password);

}
