package dev.redheris.sqldatagenerator.request.model;

import com.google.gson.annotations.SerializedName;

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
}
