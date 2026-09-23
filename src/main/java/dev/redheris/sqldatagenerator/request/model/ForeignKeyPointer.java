package dev.redheris.sqldatagenerator.request.model;

import com.google.gson.annotations.SerializedName;

public record ForeignKeyPointer(
        @SerializedName("table")
        String table,
        @SerializedName("column")
        String column
) {
}
