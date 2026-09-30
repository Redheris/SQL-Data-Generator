package dev.redheris.sqldatagenerator.gson.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    private final static DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final static DateTimeFormatter DATE_FORMAT_2 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final static DateTimeFormatter DATE_FORMAT_3 = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        out.jsonValue(DEFAULT_DATE_FORMAT.format(value));
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        String value = in.nextString();
        try {
            return LocalDate.parse(value, DEFAULT_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(value, DATE_FORMAT_2);
            } catch (DateTimeParseException e1) {
                return LocalDate.parse(value, DATE_FORMAT_3);
            }
        }
    }
}
