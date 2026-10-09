package dev.redheris.sqldatagenerator.cli;

import com.google.gson.Gson;
import dev.redheris.sqldatagenerator.db.ConnectionService;
import dev.redheris.sqldatagenerator.db.TableDataInsertService;
import dev.redheris.sqldatagenerator.generator.DataGeneratorService;
import dev.redheris.sqldatagenerator.generator.TablePrioritizer;
import dev.redheris.sqldatagenerator.generator.model.ColumnData;
import dev.redheris.sqldatagenerator.generator.model.GeneratedTableData;
import dev.redheris.sqldatagenerator.request.model.GeneratorRequest;
import dev.redheris.sqldatagenerator.request.model.RequestValidationException;
import dev.redheris.sqldatagenerator.request.model.TableConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Component
public class GeneratorRequestExecutor {
    private static final Logger log = LoggerFactory.getLogger(GeneratorRequestExecutor.class);
    private final Gson gson;
    private final ConnectionService connectionService;
    private final TablePrioritizer tablePrioritizer;
    private final DataGeneratorService dataGeneratorService;
    private final TableDataInsertService tableDataInsertService;

    public GeneratorRequestExecutor(Gson gson,
                                    ConnectionService connectionService,
                                    TablePrioritizer tablePrioritizer,
                                    DataGeneratorService dataGeneratorService, TableDataInsertService tableDataInsertService) {
        this.gson = gson;
        this.connectionService = connectionService;
        this.tablePrioritizer = tablePrioritizer;
        this.dataGeneratorService = dataGeneratorService;
        this.tableDataInsertService = tableDataInsertService;
    }

    public void executeWithFile(Scanner scanner, Path filepath) {
        GeneratorRequest request = getRequestFromFile(filepath);

        validateRequest(request);
        connectToDatabase(request);

        List<TableConfig> tables = tablePrioritizer.prioritizeTables(request.tables());

        boolean skipAndGenerateAll = false;

        for (TableConfig table : tables) {
            System.out.println();
            GeneratedTableData generatedTable = dataGeneratorService.generateTableData(request, table);

            if (!skipAndGenerateAll) {
                List<String> columnNames = Arrays.stream(generatedTable.data())
                        .map(ColumnData::name)
                        .toList();

                System.out.println("\nTable: " + table.name());
                System.out.println("Columns: " + columnNames);
                System.out.println("Records count: " + generatedTable.data()[0].value().length);

                int choice;
                do {
                    System.out.printf("""
                            \n\tActions for table %s:
                            1 - Insert this table data
                            2 - Preview generated data
                            3 - Skip this table
                            4 - Generate and insert all remaining tables
                            5 - Skip all remaining tables
                            Choose action:\s""",
                            table.name());
                    choice = scanner.nextInt();
                    if (choice == 2) {
                        previewData(scanner, generatedTable);
                    }
                } while (choice == 2 || choice < 1 || choice > 5);

                if (choice == 3) {
                    continue;
                }
                if (choice == 5) {
                    break;
                }
                if (choice == 4) {
                    skipAndGenerateAll = true;
                }
            }

            tableDataInsertService.insertValues(
                    table.name(),
                    table.generatedKeyColumns(),
                    generatedTable.data()
            );
        }
    }

    private void previewData(Scanner scanner, GeneratedTableData tableData) {
        ColumnData[] columns = tableData.data();

        while (true) {
            System.out.println("\nChoose column to view generated data");
            System.out.println("0 - exit from preview");
            for (int i = 0; i < columns.length; i++) {
                System.out.println((i + 1) + " - " + columns[i].name());
            }

            int input = scanner.nextInt();
            if (input == 0) {
                break;
            }
            ColumnData column = columns[input - 1];
            Object[] data = column.value();
            for (int i = 0; i < data.length; i++) {
                System.out.println((i + 1) + ": " + data[i]);
            }
        }
    }

    private void validateRequest(GeneratorRequest request) {
        try {
            request.validate();
        } catch (RequestValidationException e) {
            log.info("Request validation failed:", e);
            throw new RequestExecutionException("Request validation failed:", e);
        }
    }

    private void connectToDatabase(GeneratorRequest request) {
        try {
            connectionService.connect(request.dbAuth());
        } catch (Exception e) {
            log.info("Database connection failed:", e);
            throw new RequestExecutionException("Database connection failed:", e);
        }
    }

    private GeneratorRequest getRequestFromFile(Path filepath) {
        try (FileReader reader = new FileReader(filepath.toString())) {
            return gson.fromJson(reader, GeneratorRequest.class);
        } catch (Exception e) {
            log.error("Error during reading files:", e);
            throw new RequestExecutionException("Error during reading files:", e);
        }
    }
}
