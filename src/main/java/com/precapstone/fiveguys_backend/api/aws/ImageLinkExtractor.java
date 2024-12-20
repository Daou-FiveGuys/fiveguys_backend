package com.precapstone.fiveguys_backend.api.aws;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

public class ImageLinkExtractor {
    public static String extractImageUrl(JsonObject jsonObject) {
        JsonArray imagesArray = jsonObject.getAsJsonArray("images");
        if (imagesArray != null && !imagesArray.isEmpty()) {
            JsonObject firstImageObject = imagesArray.get(0).getAsJsonObject();
            JsonElement urlElement = firstImageObject.get("url");
            if (urlElement != null) {
                return urlElement.getAsString(); // URL 반환
            }
        }
        return null;
    }
    public static String extractInpaintedImageUrl(JsonObject result) {
        JsonObject jsonObject = result.getAsJsonObject("image");
        if (jsonObject != null && !jsonObject.isEmpty()) {
            JsonElement urlElement = jsonObject.get("url");
            if (urlElement != null) {
                return urlElement.getAsString(); // URL 반환
            }
        }
        return null;
    }

    public static String extractUpscaledImageUrl(JsonObject result) {
        JsonObject jsonObject = result.getAsJsonObject("image");
        if (jsonObject != null && !jsonObject.isEmpty()) {
            JsonElement urlElement = jsonObject.get("url");
            if (urlElement != null) {
                return urlElement.getAsString(); // URL 반환
            }
        }
        return null;
    }

    public static String extractContentType(JsonObject jsonObject) {
        JsonArray imagesArray = jsonObject.getAsJsonArray("content_type");
        if (imagesArray != null && !imagesArray.isEmpty()) {
            JsonObject firstImageObject = imagesArray.get(0).getAsJsonObject();
            JsonElement urlElement = firstImageObject.get("url");
            if (urlElement != null) {
                return urlElement.getAsString(); // URL 반환
            }
        }
        return null;
    }
}
