package dev.redheris.sqldatagenerator.request.model;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import dev.redheris.sqldatagenerator.gson.adapter.LocalDateAdapter;

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
    @JsonAdapter(LocalDateAdapter.class)
    private LocalDate dateAfter = LocalDate.of(1970, 1, 1);
    @SerializedName("before")
    @JsonAdapter(LocalDateAdapter.class)
    private LocalDate dateBefore = LocalDate.of(2100, 12, 31);
    @SerializedName("foreign_key")
    private ForeignKeyPointer foreignKey;

    public void validate() {
        Objects.requireNonNull(name, "'name' field is required");

        if (foreignKey != null) {
            try {
                foreignKey.validate();
            } catch (Exception e) {
                throw new IllegalStateException("Validation failed for \"foreign_key\": " + e.getMessage(), e);
            }
            if (type != null) {
                throw new IllegalStateException("Foreign key column's type are pulled from 'foreign_key' field");
            }
            return;
        }

        Objects.requireNonNull(type, "'type' field is required");

        if (nullOccurrence < 0.0 || nullOccurrence > 1.0) {
            throw new IllegalArgumentException("'null' must be in range [0.0, 1.0]");
        }

        if (type == ColumnTypes.STRING) {
            Objects.requireNonNull(value, "'value' field is required for String column");
        }

        if (type == ColumnTypes.DOUBLE || type == ColumnTypes.INTEGER) {
            Objects.requireNonNull(minValue, "'min_value' field is required for numeric column");
            Objects.requireNonNull(maxValue, "'max_value' field is required for numeric column");
        }

        if (minValue != null) {
            if (minValue < 0) {
                throw new IllegalArgumentException("'min_value' must be not negative");
            }
            if (maxValue != null && maxValue < minValue) {
                throw new IllegalArgumentException("'max_value' must be >= 'min_value'");
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
