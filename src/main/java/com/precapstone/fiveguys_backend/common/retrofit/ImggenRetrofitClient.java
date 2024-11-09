package com.precapstone.fiveguys_backend.common.retrofit;

import com.precapstone.fiveguys_backend.api.image.ImgGenApiService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImggenRetrofitClient {
    private static final String BASE_URL = "https://app.imggen.ai/";

    public static ImgGenApiService create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ImgGenApiService.class);
    }
}
