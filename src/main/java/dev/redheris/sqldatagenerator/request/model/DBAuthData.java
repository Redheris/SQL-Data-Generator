package dev.redheris.sqldatagenerator.request.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public record DBAuthData(
        @SerializedName("url")
        String url,
        @SerializedName("username")
        String username,
        @SerializedName("password")
        String password
) {
    public void validate() {
        Objects.requireNonNull(url, "'url' field is required");
        Objects.requireNonNull(username, "'username' field is required");
        Objects.requireNonNull(password, "'password' field is required");
    }
}
