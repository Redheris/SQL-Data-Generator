package dev.redheris.sqldatagenerator.query;

import dev.redheris.sqldatagenerator.db.ColumnData;
import dev.redheris.sqldatagenerator.db.TableDataInserter;
import dev.redheris.sqldatagenerator.generator.DummyGenerator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

@Controller
public class QueryController {
    private final DummyGenerator dummyGenerator;
    private final TableDataInsertService dataInserter;

    public QueryController(DummyGenerator dummyGenerator, TableDataInserter dataInserter) {
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
