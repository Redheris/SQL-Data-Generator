package dev.redheris.sqldatagenerator.generator;

import dev.redheris.sqldatagenerator.generator.syntax.PatternParser;
import dev.redheris.sqldatagenerator.generator.syntax.pattern.Pattern;
import dev.redheris.sqldatagenerator.request.model.GeneratorRequest;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class StringGeneratorService {
    private final PatternParser patternParser;

    public StringGeneratorService(PatternParser patternParser) {
        this.patternParser = patternParser;
    }

    public String generateByPattern(GeneratorRequest requestContext, String patternString, boolean plainValue) {
        Random random = new Random();
        Pattern pattern = patternParser.parsePatternString(
                random,
                requestContext.placeholdersMap(),
                requestContext.valueModelsMap(),
                patternString,
                plainValue
        );

        return pattern.generateValue(random);
    }
}
