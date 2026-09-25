package dev.redheris.sqldatagenerator.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.redheris.sqldatagenerator.generator.DataGeneratorService;
import dev.redheris.sqldatagenerator.generator.StringGeneratorService;
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
    private final DataGeneratorService dataGeneratorService;
    private final StringGeneratorService stringGeneratorService;
    @Value("${generator.request_file}")
    private String requestFile;

    private static final Logger log = LoggerFactory.getLogger(RequestService.class);
    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public RequestService(DataGeneratorService dataGeneratorService, StringGeneratorService stringGeneratorService) {
        this.dataGeneratorService = dataGeneratorService;
        this.stringGeneratorService = stringGeneratorService;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) {
        GeneratorRequest request = getRequestFromFile(requestFile);
        try {
            request.validate();
            String pattern = """
                    name: <full_name>
                    phone: <phone>
                    email: <email>
                    grade: D{{2,4}}.DD
                    string: [RD_E]{10}
                    """;
            System.out.println(stringGeneratorService.generateByPattern(request, pattern, true));
            List<GeneratedTableData> tables = dataGeneratorService.generateDataByRequest(request);
            System.out.println("*** ");
        } catch (RequestValidationException e) {
            log.info("Request validation failed:", e);
        } catch (Exception e) {
            log.info("Error during generation data:", e);
        }
//        ColumnData<Object>[] values = dummyGenerator.generateValues(5);
//        dataInserter.insertValues(
//                "reservations",
//                new String[]{"id"},
//                values
//        );
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
