package dev.redheris.sqldatagenerator.request.model;

import com.google.gson.annotations.SerializedName;

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
    private Integer precision = Double.PRECISION;
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

    public void validate() {
        Objects.requireNonNull(name, "'name' field is required");
        Objects.requireNonNull(type, "'type' field is required");
        Objects.requireNonNull(unique, "'unique' field is required");
        Objects.requireNonNull(nullOccurrence, "'null_occurrence' field is required");
        if (nullOccurrence < 0.0 || nullOccurrence > 1.0) {
            throw new IllegalArgumentException("'null' must be in range [0.0, 1.0]");
        }

        if (type == ColumnTypes.STRING) {
            Objects.requireNonNull(value, "'value' field is required for String column");
            Objects.requireNonNull(minLength, "'min_length' field is required for String column");
            Objects.requireNonNull(plainValue, "'plain_value' field is required for String column");
        }

        if (type == ColumnTypes.DOUBLE) {
            Objects.requireNonNull(precision, "'precision' field is required for Double column");
            Objects.requireNonNull(minValue, "'min_value' field is required for numeric column");
            Objects.requireNonNull(maxValue, "'max_value' field is required for numeric column");
        }

        if (type == ColumnTypes.INTEGER) {
            Objects.requireNonNull(minValue, "'min_value' field is required for numeric column");
            Objects.requireNonNull(maxValue, "'max_value' field is required for numeric column");

        }

        if (type == ColumnTypes.DATE || type == ColumnTypes.TIME) {
            Objects.requireNonNull(value, "'value' field is required for given column type");
        }

        if (minLength != null) {
            if (minLength < 0) {
                throw new IllegalArgumentException("'min_length' must be not negative");
            }
            if (maxLength != null && maxLength < minLength) {
                throw new IllegalArgumentException("'max_length' must be >= 'min_length'");
            }
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

    public Double min() {
        return minValue;
    }

    public Double max() {
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
