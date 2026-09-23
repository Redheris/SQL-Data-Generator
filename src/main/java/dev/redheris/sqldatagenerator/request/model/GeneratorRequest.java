package dev.redheris.sqldatagenerator.request.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public record GeneratorRequest(
        @SerializedName("db_auth")
        DBAuthData dbAuth,
        @SerializedName("placeholders")
        Map<String, String[]> placeholdersMap,
        @SerializedName("models")
        Map<String, String> valueModelsMap,
        @SerializedName("tables")
        TableConfig[] tables
) {
}
