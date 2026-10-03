package dev.redheris.sqldatagenerator.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.redheris.sqldatagenerator.gson.adapter.LocalDateAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class GsonConfiguration {
    @Bean
    public Gson gson(LocalDateAdapter localDateAdapter) {
        return new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, localDateAdapter)
                .create();
    }
}
