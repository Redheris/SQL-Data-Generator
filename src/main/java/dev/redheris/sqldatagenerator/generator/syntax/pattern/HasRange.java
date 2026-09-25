package dev.redheris.sqldatagenerator.generator.syntax.pattern;

/**
 * Pattern value symbol can be ranged with the leading {@code {{minValue, maxValue}}} suffix modifier
 */
public interface HasRange {
    int minValue();

    int maxValue();

    void setRange(int minValue, int maxValue);
}
