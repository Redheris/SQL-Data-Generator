package dev.redheris.sqldatagenerator.request;

import com.google.gson.Gson;
import dev.redheris.sqldatagenerator.db.ConnectionService;
import dev.redheris.sqldatagenerator.generator.DataGeneratorService;
import dev.redheris.sqldatagenerator.generator.StringGenerator;
import dev.redheris.sqldatagenerator.generator.model.GeneratedTableData;
import dev.redheris.sqldatagenerator.request.model.GeneratorRequest;
import dev.redheris.sqldatagenerator.request.model.RequestValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.List;

@Service
public class RequestService {
    private static final Logger log = LoggerFactory.getLogger(RequestService.class);

    private final Gson gson;
    private final DataGeneratorService dataGeneratorService;
    private final StringGenerator stringGeneratorService;
    private final ConnectionService connectionService;

    @Value("${generator.request_file}")
    private String requestFile;

    public RequestService(Gson gson,
                          DataGeneratorService dataGeneratorService,
                          StringGenerator stringGeneratorService,
                          ConnectionService connectionService) {
        this.dataGeneratorService = dataGeneratorService;
        this.stringGeneratorService = stringGeneratorService;
        this.connectionService = connectionService;
        this.gson = gson;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) {
        GeneratorRequest request = getRequestFromFile(requestFile);

        try {
            request.validate();
        } catch (RequestValidationException e) {
            log.info("Request validation failed:", e);
            return;
        }

        try {
            connectionService.connect(request.dbAuth());
        } catch (Exception e) {
            log.info("Database connection failed:", e);
            return;
        }

        try {
            String pattern = """
                    name: <full_name>
                    phone: <phone>
                    email: <email>
                    grade: D{{2,4}}.DD
                    string: [RD_E]{10}
                    """;
            System.out.println(stringGeneratorService.generateByPattern(request, pattern, false));
            List<GeneratedTableData> tables = dataGeneratorService.generateDataByRequest(request);
            System.out.println("*** ");
        } catch (Exception e) {
            log.info("Error during generation data:", e);
        }
    }

    private GeneratorRequest getRequestFromFile(String filepath) {
        try (FileReader reader = new FileReader(filepath)) {

            return gson.fromJson(reader, GeneratorRequest.class);
        } catch (Exception e) {
            log.error("Error during reading files:", e);
            throw new RuntimeException("Error during reading files:", e);
        }
    }
}
