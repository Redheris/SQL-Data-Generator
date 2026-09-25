package dev.redheris.sqldatagenerator.generator.syntax.pattern;

/**
 * Pattern symbol can have leading {@code {length}} or {@code {minLength, maxLength}} suffix modifier
 */
public interface Stretchable {
    int minLength();

    int maxLength();

    void setLength(int minLength, int maxLength);
}
