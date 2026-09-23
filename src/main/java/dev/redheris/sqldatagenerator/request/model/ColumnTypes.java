package dev.redheris.sqldatagenerator.request.model;

import com.google.gson.annotations.SerializedName;

public enum ColumnTypes {
    @SerializedName("bigint")
    BIGINT,
    @SerializedName("boolean")
    BOOLEAN,
    @SerializedName(value = "varchar", alternate = {"char", "string", "text"})
    STRING,
    @SerializedName("double")
    DOUBLE,
    @SerializedName(value = "integer", alternate = "int")
    INTEGER,
    @SerializedName("decimal")
    DECIMAL,
    @SerializedName("real")
    REAL,
    @SerializedName("date")
    DATE,
    @SerializedName("time")
    TIME
}
