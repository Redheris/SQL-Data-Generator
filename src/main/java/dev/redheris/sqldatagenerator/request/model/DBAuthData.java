package dev.redheris.sqldatagenerator.request.model;

import com.google.gson.annotations.SerializedName;

public record DBAuthData(
        @SerializedName("url")
        String url,
        @SerializedName("username")
        String username,
        @SerializedName("password")
        String password
) {
}
