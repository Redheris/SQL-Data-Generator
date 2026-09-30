package dev.redheris.sqldatagenerator.generator.model;

import dev.redheris.sqldatagenerator.request.model.ColumnConfig;

import java.time.LocalDate;

public record DateGenerationConfig(
        LocalDate after,
        LocalDate before,
        boolean unique,
        double nullOccurrence
) {

    public static DateGenerationConfig fromColumnConfig(ColumnConfig columnConfig) {
        return new DateGenerationConfig(
                columnConfig.dateAfter(),
                columnConfig.dateBefore(),
                columnConfig.unique(),
                columnConfig.nullOccurrence()
        );
    }
}
