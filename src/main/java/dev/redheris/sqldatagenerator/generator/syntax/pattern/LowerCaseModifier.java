package dev.redheris.sqldatagenerator.generator.syntax.pattern;

import java.util.Random;

public class LowerCaseModifier extends ModifierPatternElement {
    public LowerCaseModifier(PatternElement valuePattern) {
        super(valuePattern);
    }

    @Override
    public String generateValue(Random random) {
        return valuePattern.generateValue(random).toLowerCase();
    }
}
