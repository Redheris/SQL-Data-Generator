package dev.redheris.sqldatagenerator.generator.syntax.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Contains pattern elements array and used to generate full pattern value or as a part of another pattern
 */
public class Pattern extends PatternElement {
    private final List<PatternElement> elements = new ArrayList<>();

    public void addElement(PatternElement element) {
        elements.add(element);
    }

    @Override
    public String generateValue(Random random) {
        StringBuilder value = new StringBuilder();

        for (PatternElement element : elements) {
            value.append(element.generateValue(random));
        }

        return value.toString();
    }
}
