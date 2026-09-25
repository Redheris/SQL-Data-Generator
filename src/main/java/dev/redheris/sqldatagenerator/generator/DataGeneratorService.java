package dev.redheris.sqldatagenerator.generator;

import dev.redheris.sqldatagenerator.generator.model.*;
import dev.redheris.sqldatagenerator.request.model.ColumnConfig;
import dev.redheris.sqldatagenerator.request.model.GeneratorRequest;
import dev.redheris.sqldatagenerator.request.model.TableConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DataGeneratorService {
    private static final Logger log = LoggerFactory.getLogger(DataGeneratorService.class);
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final StringGeneratorService stringGeneratorService;

    public DataGeneratorService(StringGeneratorService stringGeneratorService) {
        this.stringGeneratorService = stringGeneratorService;
    }

    public List<GeneratedTableData> generateDataByRequest(GeneratorRequest request) {
        log.info("Generating data for {} tables...", request.tables().length);
        long startTime = System.currentTimeMillis();

        List<GeneratedTableData> generatedTables = new ArrayList<>();

        for (TableConfig table : request.tables()) {
            generatedTables.add(generateTableData(request, table));
        }

        double time = (System.currentTimeMillis() - startTime) / 1000.0;
        log.info("Completed generation data for {} in {} seconds", request.tables().length, time);

        return generatedTables;
    }

    private GeneratedTableData generateTableData(GeneratorRequest request, TableConfig tableConfig) {
        log.info("Generating {} records for table \"{}\"...", tableConfig.count(), tableConfig.name());
        long startTime = System.currentTimeMillis();

        var tableBuilder = GeneratedTableData
                .builder(tableConfig.name(), tableConfig.generatedKeyColumns());

        for (ColumnConfig column : tableConfig.columns()) {
            ColumnData columnData = generateColumnData(request, tableConfig.count(), column);
            tableBuilder.addColumnData(columnData);
        }

        double time = (System.currentTimeMillis() - startTime) / 1000.0;
        log.info("Completed generation of {} records for table \"{}\" in {} seconds",
                tableConfig.count(), tableConfig.name(), time);

        return tableBuilder.build();
    }

    public ColumnData generateColumnData(GeneratorRequest request, int count, ColumnConfig columnConfig) {
        Object[] data = new Object[count];

        // TODO: Unique values generation
        // TODO: Foreign keys generation
        switch (columnConfig.type()) {
            case STRING -> generateStringData(request, data, columnConfig);
            case BIGINT -> generateLongData(data, columnConfig);
            case INTEGER -> generateIntegerData(data, columnConfig);
            case DOUBLE -> generateDoubleData(data, columnConfig);
            case BOOLEAN -> generateBooleanData(data, columnConfig);
            case DATE -> generateDateData(data, columnConfig);
            case DATETIME -> generateDateTimeData(data, columnConfig);
        }

        return new ColumnData(columnConfig.name(), data);
    }

    private void generateStringData(GeneratorRequest request, Object[] data, ColumnConfig columnConfig) {
        StringGenerationConfig config = StringGenerationConfig.fromColumnConfig(columnConfig);

        for (int i = 0; i < data.length; i++) {
            if (config.nullOccurrence() > 0 && random.nextDouble() < config.nullOccurrence()) {
                data[i] = null;
            } else {
                data[i] = stringGeneratorService.generateByPattern(request, config.pattern(), config.plainValue());
            }
        }
    }

    private void generateLongData(Object[] data, ColumnConfig columnConfig) {
        LongGenerationConfig config = LongGenerationConfig.fromColumnConfig(columnConfig);

        for (int i = 0; i < data.length; i++) {
            if (config.nullOccurrence() > 0 && random.nextDouble() < config.nullOccurrence()) {
                data[i] = null;
            } else {
                data[i] = random.nextLong(config.min(), config.max() + 1);
            }
        }
    }

    private void generateIntegerData(Object[] data, ColumnConfig columnConfig) {
        IntegerGenerationConfig config = IntegerGenerationConfig.fromColumnConfig(columnConfig);

        for (int i = 0; i < data.length; i++) {
            if (config.nullOccurrence() > 0 && random.nextDouble() < config.nullOccurrence()) {
                data[i] = null;
            } else {
                data[i] = random.nextInt(config.min(), config.max() + 1);
            }
        }
    }

    private void generateDoubleData(Object[] data, ColumnConfig columnConfig) {
        DoubleGenerationConfig config = DoubleGenerationConfig.fromColumnConfig(columnConfig);
        double precisionRound = Math.pow(10, config.precision());

        for (int i = 0; i < data.length; i++) {
            if (config.nullOccurrence() > 0 && random.nextDouble() < config.nullOccurrence()) {
                data[i] = null;
            } else {
                double value = random.nextDouble(config.min(), Math.nextUp(config.max()));
                if (config.precision() < Double.PRECISION) {
                    data[i] = Math.round(value * precisionRound) / precisionRound;
                } else {
                    data[i] = value;
                }
            }
        }
    }

    private void generateBooleanData(Object[] data, ColumnConfig columnConfig) {
        BooleanGenerationConfig config = BooleanGenerationConfig.fromColumnConfig(columnConfig);

        for (int i = 0; i < data.length; i++) {
            if (config.nullOccurrence() > 0 && random.nextDouble() < config.nullOccurrence()) {
                data[i] = null;
            } else {
                data[i] = random.nextDouble() < config.trueOccurrence();
            }
        }
    }

    private void generateDateData(Object[] data, ColumnConfig columnConfig) {
        DateGenerationConfig config = DateGenerationConfig.fromColumnConfig(columnConfig);

        int daysCount = Period.between(config.after(), config.before()).getDays();

        for (int i = 0; i < data.length; i++) {
            if (config.nullOccurrence() > 0 && random.nextDouble() < config.nullOccurrence()) {
                data[i] = null;
            } else {
                data[i] = config.after().plusDays(random.nextInt(daysCount + 1));
            }
        }
    }

    private void generateDateTimeData(Object[] data, ColumnConfig columnConfig) {
        DateGenerationConfig config = DateGenerationConfig.fromColumnConfig(columnConfig);

        int daysCount = Period.between(config.after(), config.before()).getDays();

        for (int i = 0; i < data.length; i++) {
            if (config.nullOccurrence() > 0 && random.nextDouble() < config.nullOccurrence()) {
                data[i] = null;
            } else {
                data[i] = config.after()
                        .plusDays(random.nextInt(daysCount + 1))
                        .atTime(LocalTime.ofSecondOfDay(random.nextInt(86400)));
            }
        }
    }

}
