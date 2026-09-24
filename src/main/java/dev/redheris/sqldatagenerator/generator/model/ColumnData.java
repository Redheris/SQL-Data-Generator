package dev.redheris.sqldatagenerator.generator.model;

import dev.redheris.sqldatagenerator.db.TableDataInsertService;

/**
 * Contains a single column data with a set of values
 *
 * @param name  Column's name
 * @param value Column's values
 * @param <T>   Type of the column's values
 *
 * @see TableDataInsertService
 */
public record ColumnData<T>(
        String name,
        T[] value
) {
}
