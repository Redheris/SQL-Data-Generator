package dev.redheris.sqldatagenerator.db;

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
