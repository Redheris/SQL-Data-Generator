package dev.redheris.sqldatagenerator.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.redheris.sqldatagenerator.request.model.GeneratorRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.FileReader;

@Service
public class RequestService {
    @Value("${generator.request_file}")
    private String requestFile;

    private static final Logger log = LoggerFactory.getLogger(RequestService.class);
    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @EventListener
    public void on(ApplicationReadyEvent event) {
        GeneratorRequest request = getRequestFromFile(requestFile);
        try {
            request.validate();
            System.out.println(gson.toJson(request));
        } catch (Exception e) {
            log.info("Request validation failed:", e);
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
