package dev.redheris.sqldatagenerator.request.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public record ForeignKeyPointer(
        @SerializedName("table")
        String table,
        @SerializedName("column")
        String column
) {
    public void validate() {
        Objects.requireNonNull(table, "'table' field is required");
        Objects.requireNonNull(column, "'column' field is required");
    }
}
