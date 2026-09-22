package dev.redheris.sqldatagenerator.generator;

import dev.redheris.sqldatagenerator.db.ColumnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

@Component
public class DummyGenerator {
    private static final Logger log = LoggerFactory.getLogger(DummyGenerator.class);

    @SuppressWarnings("unchecked")
    public ColumnData<Object>[] generateValues(int count) {
        log.info("Generating {} records...", count);
        long startTime = System.currentTimeMillis();

        ColumnData<Object>[] columns = new ColumnData[5];
        int columnNum = 0;

        String columnName = "room_id";
        Object[] values = new Integer[count];
        for (int i = 0; i < count; i++) {
            values[i] = i * 3;
        }
        columns[columnNum++] = new ColumnData<>(columnName, values);

        columnName = "user_id";
        values = new Integer[count];
        for (int i = 0; i < count; i++) {
            values[i] = i * 3;
        }
        columns[columnNum++] = new ColumnData<>(columnName, values);

        columnName = "start_date";
        values = new LocalDate[count];
        for (int i = 0; i < count; i++) {
            values[i] = LocalDate.now().plusDays(i * 2L);
        }
        columns[columnNum++] = new ColumnData<>(columnName, values);

        columnName = "end_date";
        values = new LocalDate[count];
        for (int i = 0; i < count; i++) {
            values[i] = LocalDate.now().plusDays(i * 2L + 4);
        }
        columns[columnNum++] = new ColumnData<>(columnName, values);

        columnName = "status";
        values = new String[count];
        String[] statuses = {"PENDING", "APPROVED", "CANCELED"};
        for (int i = 0; i < count; i++) {
            values[i] = statuses[new Random().nextInt(0, statuses.length)];
        }
        columns[columnNum] = new ColumnData<>(columnName, values);

        double time = (System.currentTimeMillis() - startTime) / 1000.0;
        log.info("Completed generation of {} records in {} seconds", count, time);

        return columns;
    }
}
