package dev.redheris.sqldatagenerator.generator.model;

import java.util.ArrayList;
import java.util.List;

public record GeneratedTableData(
        String tableName,
        String[] generatedKeyColumns,
        ColumnData[] data
) {

    public static Builder builder(String table, String[] generatedKeyColumns) {
        return new Builder(table, generatedKeyColumns);
    }

    public static class Builder {
        private final String table;
        private final String[] generatedKeyColumns;
        private final List<ColumnData> columns = new ArrayList<>();

        public Builder(String table, String[] generatedKeyColumns) {
            this.table = table;
            this.generatedKeyColumns = generatedKeyColumns;
        }

        public void addColumnData(ColumnData columnData) {
            this.columns.add(columnData);
        }

        public GeneratedTableData build() {
            ColumnData[] columnsData = columns.toArray(ColumnData[]::new);
            return new GeneratedTableData(table, generatedKeyColumns, columnsData);
        }
    }
}
