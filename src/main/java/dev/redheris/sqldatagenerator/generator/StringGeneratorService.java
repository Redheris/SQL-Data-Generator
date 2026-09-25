package dev.redheris.sqldatagenerator.generator;

import dev.redheris.sqldatagenerator.generator.syntax.PatternParser;
import dev.redheris.sqldatagenerator.generator.syntax.pattern.PatternElement;
import dev.redheris.sqldatagenerator.request.model.GeneratorRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class StringGeneratorService {
    private final PatternParser patternParser;

    public StringGeneratorService(PatternParser patternParser) {
        this.patternParser = patternParser;
    }

    public String generateByPattern(GeneratorRequest requestContext, String pattern, boolean plainValue) {
        Random random = new Random();
        List<PatternElement> elements = patternParser.parsePatternString(
                random,
                requestContext.placeholdersMap(),
                requestContext.valueModelsMap(),
                pattern,
                plainValue
        );

        StringBuilder valueBuilder = new StringBuilder();
        for (PatternElement element : elements) {
            valueBuilder.append(element.generateValue(random));
        }

        return valueBuilder.toString();
    }
}
