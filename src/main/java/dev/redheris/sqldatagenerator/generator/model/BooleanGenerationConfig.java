package dev.redheris.sqldatagenerator.generator.model;

import dev.redheris.sqldatagenerator.request.model.ColumnConfig;

public record BooleanGenerationConfig(
        double trueOccurrence,
        boolean unique,
        double nullOccurrence
) {
    public static BooleanGenerationConfig fromColumnConfig(ColumnConfig columnConfig) {
        return new BooleanGenerationConfig(
                columnConfig.trueOccurrence(),
                columnConfig.unique(),
                columnConfig.nullOccurrence()
        );
    }
}

