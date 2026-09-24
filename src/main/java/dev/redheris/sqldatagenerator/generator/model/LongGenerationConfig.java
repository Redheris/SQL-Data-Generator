package dev.redheris.sqldatagenerator.generator.model;

import dev.redheris.sqldatagenerator.request.model.ColumnConfig;

public record LongGenerationConfig(
        long min,
        long max,
        boolean unique,
        double nullOccurrence
) {
    public static LongGenerationConfig fromColumnConfig(ColumnConfig columnConfig) {
        return new LongGenerationConfig(
                columnConfig.minValue().longValue(),
                columnConfig.maxValue().longValue(),
                columnConfig.unique(),
                columnConfig.nullOccurrence()
        );
    }
}

