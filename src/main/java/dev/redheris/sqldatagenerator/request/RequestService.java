package dev.redheris.sqldatagenerator.request;

import dev.redheris.sqldatagenerator.db.ColumnData;
import dev.redheris.sqldatagenerator.db.TableDataInsertService;
import dev.redheris.sqldatagenerator.generator.DummyGenerator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
    private final DummyGenerator dummyGenerator;
    private final TableDataInsertService dataInserter;

    public RequestService(DummyGenerator dummyGenerator, TableDataInsertService dataInserter) {
        this.dummyGenerator = dummyGenerator;
        this.dataInserter = dataInserter;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) {
        ColumnData<Object>[] values = dummyGenerator.generateValues(5);
        dataInserter.insertValues(
                "reservations",
                new String[]{"id"},
                values
        );
    }
}
