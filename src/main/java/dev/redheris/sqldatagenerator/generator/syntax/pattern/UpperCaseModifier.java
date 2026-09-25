package dev.redheris.sqldatagenerator.generator.syntax.pattern;

import java.util.Random;

public class UpperCaseModifier extends ModifierPatternElement {
    public UpperCaseModifier(PatternElement valuePattern) {
        super(valuePattern);
    }

    @Override
    public String generateValue(Random random) {
        return valuePattern.generateValue(random).toUpperCase();
    }
}
