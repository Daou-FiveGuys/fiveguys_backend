package com.precapstone.fiveguys_backend.api.image;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface PhotoroomApiService {
    @GET("v2/edit")
    @Streaming
    Call<ResponseBody> removeTextFromImage(
        @Header("x-api-key") String apiKey,
        @Query("removeBackground") boolean removeBackground,
        @Query("referenceBox") String referenceBox,
        @Query("textRemoval.mode") String textRemovalMode,
        @Query("imageUrl") String imageUrl
    );
}
