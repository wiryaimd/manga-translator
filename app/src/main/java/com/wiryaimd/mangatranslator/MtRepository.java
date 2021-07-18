package com.wiryaimd.mangatranslator;

import com.wiryaimd.mangatranslator.api.ApiEndpoint;
import com.wiryaimd.mangatranslator.util.Const;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MtRepository {

    private ApiEndpoint apiEndpoint;

    public MtRepository(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.BASE_URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiEndpoint = retrofit.create(ApiEndpoint.class);
    }

    public ApiEndpoint getApiEndpoint() {
        return apiEndpoint;
    }
}
