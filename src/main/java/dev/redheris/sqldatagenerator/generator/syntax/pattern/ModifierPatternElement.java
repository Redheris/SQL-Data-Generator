package dev.redheris.sqldatagenerator.generator.syntax.pattern;

/**
 * Pattern symbol that doesn't generate a value itself but affects another symbol's output
 */
public abstract class ModifierPatternElement extends PatternElement {
    protected final PatternElement valuePattern;

    public ModifierPatternElement(PatternElement valuePattern) {
        this.valuePattern = valuePattern;
    }
}
