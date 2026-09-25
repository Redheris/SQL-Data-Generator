package dev.redheris.sqldatagenerator.generator.syntax.pattern;

import java.util.Random;

/**
 * Any syntax element that can be used in a pattern
 */
public abstract class PatternElement {
    public abstract String generateValue(Random random);
}
