package com.precapstone.fiveguys_backend.common.retrofit;

import com.precapstone.fiveguys_backend.api.ai.PhotoroomApiService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoroomRetrofitClient {
    private static final String BASE_URL = "https://image-api.photoroom.com/";

    public static PhotoroomApiService create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(PhotoroomApiService.class);
    }

}
