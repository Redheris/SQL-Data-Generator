package dev.redheris.sqldatagenerator.request.model;

import com.google.gson.annotations.SerializedName;

public final class ColumnConfig {
    @SerializedName("name")
    private String name;
    @SerializedName("type")
    private ColumnTypes type;
    @SerializedName("min")
    private Integer minValue;
    @SerializedName("max")
    private Integer maxValue;
    @SerializedName("precision")
    private Integer precision;
    @SerializedName(value = "minLength", alternate = "length")
    private Integer minLength;
    @SerializedName(value = "maxLength")
    private Integer maxLength;
    @SerializedName("unique")
    private Boolean unique = false;
    @SerializedName("null")
    private Double nullOccurrence = 0.0;
    @SerializedName("plain_value")
    private Boolean plainValue = false;
    @SerializedName("value")
    private String value;
    @SerializedName("foreign_key")
    private ForeignKeyPointer foreignKey;

    public String name() {
        return name;
    }

    public ColumnTypes type() {
        return type;
    }

    public Integer min() {
        return minValue;
    }

    public Integer max() {
        return maxValue;
    }

    public Integer precision() {
        return precision;
    }

    public Integer minLength() {
        return minLength;
    }

    public Integer maxLength() {
        return maxLength;
    }

    public Boolean unique() {
        return unique;
    }

    public Double nullOccurrence() {
        return nullOccurrence;
    }

    public Boolean plainValue() {
        return plainValue;
    }

    public String value() {
        return value;
    }

    public ForeignKeyPointer foreignKey() {
        return foreignKey;
    }
}
