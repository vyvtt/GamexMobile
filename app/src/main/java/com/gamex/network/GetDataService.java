package com.gamex.network;

import com.gamex.models.Exhibition;
import com.gamex.utils.Constant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {
    @GET(Constant.API_EXHIBITON)
    Call<List<Exhibition>> getAllExhibition();
}
