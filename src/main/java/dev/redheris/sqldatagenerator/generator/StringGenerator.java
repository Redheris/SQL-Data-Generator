package dev.redheris.sqldatagenerator.generator;

import dev.redheris.sqldatagenerator.generator.syntax.PatternParser;
import dev.redheris.sqldatagenerator.generator.syntax.pattern.Pattern;
import dev.redheris.sqldatagenerator.request.model.GeneratorRequest;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class StringGenerator {
    private final PatternParser patternParser;

    public StringGenerator(PatternParser patternParser) {
        this.patternParser = patternParser;
    }

    public String generateByPattern(GeneratorRequest requestContext, String patternString, boolean plainValue) {
        Random random = new Random();
        Pattern pattern = patternParser.parsePatternString(
                requestContext.placeholdersMap(),
                requestContext.valueModelsMap(),
                patternString,
                plainValue
        );

        return pattern.generateValue(random);
    }
}
