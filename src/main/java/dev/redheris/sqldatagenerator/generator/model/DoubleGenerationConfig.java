package dev.redheris.sqldatagenerator.generator.model;

import dev.redheris.sqldatagenerator.request.model.ColumnConfig;

public record DoubleGenerationConfig(
        double min,
        double max,
        int precision,
        boolean unique,
        double nullOccurrence
) {
    public static DoubleGenerationConfig fromColumnConfig(ColumnConfig columnConfig) {
        return new DoubleGenerationConfig(
                columnConfig.minValue(),
                columnConfig.maxValue(),
                columnConfig.precision(),
                columnConfig.unique(),
                columnConfig.nullOccurrence()
        );
    }
}
