package dev.redheris.sqldatagenerator.generator.model;

import dev.redheris.sqldatagenerator.request.model.ColumnConfig;

public record IntegerGenerationConfig(
        int min,
        int max,
        boolean unique,
        double nullOccurrence
) {
    public static IntegerGenerationConfig fromColumnConfig(ColumnConfig columnConfig) {
        return new IntegerGenerationConfig(
                columnConfig.minValue().intValue(),
                columnConfig.maxValue().intValue(),
                columnConfig.unique(),
                columnConfig.nullOccurrence()
        );
    }
}
