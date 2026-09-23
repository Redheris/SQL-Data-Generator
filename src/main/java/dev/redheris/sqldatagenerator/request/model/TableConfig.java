package dev.redheris.sqldatagenerator.request.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public record TableConfig(
        @SerializedName("name")
        String name,
        @SerializedName("count")
        Integer count,
        @SerializedName("generated_key_columns")
        String[] generatedKeyColumns,
        @SerializedName("columns")
        ColumnConfig[] columns
) {
    public void validate() {
        Objects.requireNonNull(name, "'name' field is required");
        Objects.requireNonNull(count, "'count' field is required");
        if (count <= 0) {
            throw new IllegalArgumentException("'count' must be positive");
        }
        Objects.requireNonNull(columns, "'columns' field is required");
        for (int i = 0; i < columns.length; i++) {
            try {
                columns[i].validate();
            } catch (Exception e) {
                throw new IllegalStateException("Validation failed for \"columns[%d]\": ".formatted(i) + e.getMessage(), e);
            }
        }
    }
}
