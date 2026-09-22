package dev.redheris.sqldatagenerator.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Inserts the given data into the specified table
 */
@Service
public class TableDataInsertService {
    private static final Logger log = LoggerFactory.getLogger(TableDataInsertService.class);
    private final JdbcTemplate jdbcTemplate;

    public TableDataInsertService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Inserts the given columns data into the specified table
     *
     * @param table               Name of the table that should be inserted into
     * @param generatedKeyColumns Names of the columns that has database-generated values, so the program can skip them
     * @param columns             Data for each column with a specified column name and values
     */
    public void insertValues(String table, String[] generatedKeyColumns, ColumnData<Object>[] columns) {
        int valuesCount = columns[0].value().length;
        log.info("Inserting {} records into \"{}\"...", valuesCount, table);
        long startTime = System.currentTimeMillis();

        var valuesMap = columnsDataToBatches(columns);
        try {
            new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName(table)
                    .usingGeneratedKeyColumns(generatedKeyColumns)
                    .executeBatch(valuesMap);
        } catch (Exception e) {
            log.error("Exception during insertion:", e);
        }

        double time = (System.currentTimeMillis() - startTime) / 1000.0;
        log.info("Completed insertion of {} records into \"{}\" in {} seconds", valuesCount, table, time);
    }

    /**
     * Converts array of ColumnData into array of Maps where key is the column name and value is the column value
     * of the record
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object>[] columnsDataToBatches(ColumnData<Object>[] columnsData) {
        int valuesCount = columnsData[0].value().length;
        Map<String, Object>[] batches = new HashMap[valuesCount];

        for (int i = 0; i < valuesCount; i++) {
            batches[i] = new HashMap<>();
            for (ColumnData<Object> column : columnsData) {
                batches[i].put(column.name(), column.value()[i]);
            }
        }

        return batches;
    }
}