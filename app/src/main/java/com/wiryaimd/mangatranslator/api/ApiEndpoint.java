package com.wiryaimd.mangatranslator.api;

import com.wiryaimd.mangatranslator.api.model.DetectModel;
import com.wiryaimd.mangatranslator.util.Const;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiEndpoint {

    @Headers({
            "content-type: application/json",
            "x-rapidapi-key: " + Const.RAPID_API_KEY,
            "x-rapidapi-host: " + Const.RAPID_API_HOST
    })
    @POST("/ocr")
    Call<DetectModel> postDetectModel(@Query("detectOrientation") boolean orientation, @Query("language") String lang, @Body String image);


}
