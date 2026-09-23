package dev.redheris.sqldatagenerator.request.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.Objects;

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
    public void validate() {
        try {
            dbAuth.validate();
        } catch (Exception e) {
            throw new IllegalStateException("Validation failed for \"db_auth\": " + e.getMessage(), e);
        }

        Objects.requireNonNull(tables, "'tables' field is required");

        for (int i = 0; i < tables.length; i++) {
            try {
                tables[i].validate();
            } catch (Exception e) {
                throw new IllegalStateException("Validation failed for \"tables[%d]\": ".formatted(i) + e.getMessage(), e);
            }
        }
    }
}
