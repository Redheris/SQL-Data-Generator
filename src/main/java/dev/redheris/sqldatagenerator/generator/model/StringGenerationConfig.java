package dev.redheris.sqldatagenerator.generator.model;

import dev.redheris.sqldatagenerator.request.model.ColumnConfig;

public record StringGenerationConfig(
        String pattern,
        boolean plainValue,
        boolean unique,
        double nullOccurrence
) {
    public static StringGenerationConfig fromColumnConfig(ColumnConfig columnConfig) {
        return new StringGenerationConfig(
                columnConfig.value(),
                columnConfig.plainValue(),
                columnConfig.unique(),
                columnConfig.nullOccurrence()
        );
    }
}

