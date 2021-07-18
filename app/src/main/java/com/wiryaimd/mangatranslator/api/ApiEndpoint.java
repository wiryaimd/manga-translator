package com.wiryaimd.mangatranslator.api;

import com.wiryaimd.mangatranslator.api.model.Detect2Model;
import com.wiryaimd.mangatranslator.api.model.DetectModel;
import com.wiryaimd.mangatranslator.api.model.Ex1Model;
import com.wiryaimd.mangatranslator.util.Const;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiEndpoint {

    /**
     * NOTE:
     * jika @GET("/posts") berisikan tanda / (slash) maka
     * base url yg berawal https://jsonplaceholder.typicode.com/v3/ direplace menjadi https://jsonplaceholder.typicode.com/posts
     *
     * sedangkan @GET("posts") tanpa slash
     * akan menjadi https://jsonplaceholder.typicode.com/v3/posts
     *
     */

    // @Query pada param digunakan pada https://jsonplaceholder.typicode.com/posts?userId=2&_sort=id dll
    // ?userId=query nyach ugh
    @GET("/posts")
    Call<List<DetectModel>> getDetectModel(
            @Query("userId") Integer[] id,
            @Query("_sort") String sort,
            @Query("_order") String order
    );

    @GET("/posts")
    Call<List<DetectModel>> getDetecModel(@QueryMap Map<String, String> map);

    // for ex: typicode.com/posts/3/comments
    @GET("/posts/{id}/{attr}")
    Call<List<Ex1Model>> getEx1(@Path("id") int id, @Path("attr") String attr);

    // posts
    @POST("posts")
    Call<DetectModel> postDetect(@Body DetectModel detectModel);

    // post menggunakan url encoded
    @FormUrlEncoded
    @POST
    Call<DetectModel> postDetectEncoded(@Field("userId") int id, @Field("title") String title, @Field("body") String text);

    // put nih boss aowkwoa
    @PUT("posts/{id}")
    Call<DetectModel> putDetect(@Path("id") int id, @Body DetectModel detectModel);

    /**
     * bedanya PUT dan PATCH adalah put mereplace semua data(jika ada field yg tidak di isi maka nilai = null)
     * sedangkan patch mereplace berdasarkan data yg telah diberikan(tidak semua field diganti) (analoginya //paste -a (di mc server fawe))
     */

    // patch
    @PATCH("posts/{id}")
    Call<DetectModel> patchDetect(@Path("id") int id, @Body DetectModel detectModel);

    // yaa delete cukk ngerti lahh
    @DELETE("posts/{id}")
    Call<DetectModel> deleteDetect(@Path("id") int id);

//    // POST Request
//    // microsoft vision
//    @Headers({"content-type: " + "application/json", "x-rapidapi-key: " + Const.RAPID_API_KEY, "x-rapidapi-host: " + Const.RAPID_API_HOST})
//    @POST("/ocr")
//    Call<Detect2Model> postDetect(
//            @Body String img,
//            @Query("detectOrientation") boolean detectOrientation,
//            @Query("language") String lang);


}
