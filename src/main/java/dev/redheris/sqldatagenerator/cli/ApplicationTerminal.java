package dev.redheris.sqldatagenerator.cli;

import dev.redheris.sqldatagenerator.db.ConnectionService;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Scanner;

@Component
public class ApplicationTerminal implements CommandLineRunner {
    private final ConnectionService connectionService;
    private final FileImporter fileImporter;
    private final GeneratorRequestExecutor generatorRequestExecutor;

    public ApplicationTerminal(ConnectionService connectionService,
                               FileImporter fileImporter,
                               GeneratorRequestExecutor generatorRequestExecutor) {
        this.connectionService = connectionService;
        this.fileImporter = fileImporter;
        this.generatorRequestExecutor = generatorRequestExecutor;
    }

    @Override
    public void run(String @NonNull ... args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== SQL Data Generator CLI ===");

        String input;
        do {
            System.out.print("""
                    \n\tActions:
                    generate <filename/filepath> - Read the request file and generate data
                    exit                         - Exit from CLI application
                    Choose action:\s""");
            input = scanner.next().toLowerCase();

            if (input.equals("generate")) {
                try {
                    String filename = scanner.next();
                    if (!filename.endsWith(".json")) {
                        filename = filename + ".json";
                    }
                    Path filepath = fileImporter.validateFilepath(filename);
                    generatorRequestExecutor.executeWithFile(scanner, filepath);
                } catch (NoSuchFileException e) {
                    System.out.println(e.getMessage());
                } catch (RequestExecutionException e) {
                    System.out.println(e.getMessage() + " " + e.getCause().getMessage());
                } finally {
                    connectionService.disconnect();
                }
            }
        } while (!input.equals("exit"));
    }
}
