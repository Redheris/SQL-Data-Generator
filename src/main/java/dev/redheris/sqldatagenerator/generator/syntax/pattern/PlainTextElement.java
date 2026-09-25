package dev.redheris.sqldatagenerator.generator.syntax.pattern;

import java.util.Random;

/**
 * Plain text - should not be replaced with any values
 */
public class PlainTextElement extends PatternElement {
    private final String value;

    public PlainTextElement(String value) {
        this.value = value;
    }

    public PlainTextElement(char value) {
        this(Character.toString(value));
    }

    @Override
    public String generateValue(Random random) {
        return value;
    }
}
