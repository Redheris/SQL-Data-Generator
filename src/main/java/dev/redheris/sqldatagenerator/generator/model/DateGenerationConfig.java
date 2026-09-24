package dev.redheris.sqldatagenerator.generator.model;

import dev.redheris.sqldatagenerator.request.model.ColumnConfig;

import java.time.LocalDate;

public record DateGenerationConfig(
        LocalDate after,
        LocalDate before,
        boolean unique,
        double nullOccurrence
) {
    private final static LocalDate DEFAULT_MIN_DATE = LocalDate.of(1970, 1, 1);
    private final static LocalDate DEFAULT_MAX_DATE = LocalDate.of(2100, 12, 31);

    public static DateGenerationConfig fromColumnConfig(ColumnConfig columnConfig) {
        return new DateGenerationConfig(
                columnConfig.dateAfter() == null ? DEFAULT_MIN_DATE : columnConfig.dateAfter(),
                columnConfig.dateBefore() == null ? DEFAULT_MAX_DATE : columnConfig.dateBefore(),
                columnConfig.unique(),
                columnConfig.nullOccurrence()
        );
    }
}

