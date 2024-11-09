package com.precapstone.fiveguys_backend.api.ai;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ImgGenApiService {

    @Multipart
    @POST("v1/remove-text")
    Call<ResponseBody> removeTextFromImage(
            @Header("X-IMGGEN-KEY") String apiKey,
            @Part MultipartBody.Part image
    );
}
