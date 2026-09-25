package dev.redheris.sqldatagenerator.request.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.Objects;

public final class ColumnConfig {
    @SerializedName("name")
    private String name;
    @SerializedName("type")
    private ColumnTypes type;
    @SerializedName("min")
    private Double minValue;
    @SerializedName("max")
    private Double maxValue;
    @SerializedName("precision")
    private int precision = Double.PRECISION;
    @SerializedName("unique")
    private boolean unique = false;
    @SerializedName("null")
    private double nullOccurrence = 0.0;
    @SerializedName("is_plain_value")
    private boolean plainValue = false;
    @SerializedName("value")
    private String value;
    @SerializedName("boolean_true")
    private double trueOccurrence = 0.5;
    @SerializedName("after")
    private LocalDate dateAfter;
    @SerializedName("before")
    private LocalDate dateBefore;
    @SerializedName("foreign_key")
    private ForeignKeyPointer foreignKey;

    public void validate() {
        Objects.requireNonNull(name, "'name' field is required");
        Objects.requireNonNull(type, "'type' field is required");
        if (nullOccurrence < 0.0 || nullOccurrence > 1.0) {
            throw new IllegalArgumentException("'null' must be in range [0.0, 1.0]");
        }

        if (type == ColumnTypes.STRING) {
            Objects.requireNonNull(value, "'value' field is required for String column");
        }

        if (type == ColumnTypes.DOUBLE) {
            Objects.requireNonNull(minValue, "'min_value' field is required for numeric column");
            Objects.requireNonNull(maxValue, "'max_value' field is required for numeric column");
        }

        if (type == ColumnTypes.INTEGER) {
            Objects.requireNonNull(minValue, "'min_value' field is required for numeric column");
            Objects.requireNonNull(maxValue, "'max_value' field is required for numeric column");

        }

        if (type == ColumnTypes.DATE || type == ColumnTypes.DATETIME) {
            Objects.requireNonNull(value, "'value' field is required for given column type");
        }

        if (minValue != null) {
            if (minValue < 0) {
                throw new IllegalArgumentException("'min_value' must be not negative");
            }
            if (maxValue != null && maxValue < minValue) {
                throw new IllegalArgumentException("'max_value' must be >= 'min_value'");
            }
        }

        if (foreignKey != null) {
            try {
                foreignKey.validate();
            } catch (Exception e) {
                throw new IllegalStateException("Validation failed for \"foreign_key\": " + e.getMessage(), e);
            }
        }
    }

    public String name() {
        return name;
    }

    public ColumnTypes type() {
        return type;
    }

    public Double minValue() {
        return minValue;
    }

    public Double maxValue() {
        return maxValue;
    }

    public int precision() {
        return precision;
    }

    public boolean unique() {
        return unique;
    }

    public double nullOccurrence() {
        return nullOccurrence;
    }

    public boolean plainValue() {
        return plainValue;
    }

    public String value() {
        return value;
    }

    public double trueOccurrence() {
        return trueOccurrence;
    }

    public LocalDate dateAfter() {
        return dateAfter;
    }

    public LocalDate dateBefore() {
        return dateBefore;
    }

    public ForeignKeyPointer foreignKey() {
        return foreignKey;
    }
}
