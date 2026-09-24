package dev.redheris.sqldatagenerator.request.model;

import com.google.gson.annotations.SerializedName;

public enum ColumnTypes {
    @SerializedName(value = "varchar", alternate = {"char", "string", "text"})
    STRING,
    @SerializedName("bigint")
    BIGINT,
    @SerializedName(value = "integer", alternate = "int")
    INTEGER,
    @SerializedName("double")
    DOUBLE,
    @SerializedName("boolean")
    BOOLEAN,
    @SerializedName("date")
    DATE,
    @SerializedName(value = "datetime", alternate = "time")
    DATETIME
}
