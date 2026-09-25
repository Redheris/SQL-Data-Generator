package dev.redheris.sqldatagenerator.generator.syntax.pattern;

import java.util.Random;

/**
 * Chooses random element from the given list
 */
public class ElementsListChoice extends AbstractStretchableElement implements Stretchable {
    private final PatternElement[] elements;

    public ElementsListChoice(PatternElement[] elements) {
        this.elements = elements;
    }

    public ElementsListChoice(String[] plainValues) {
        PatternElement[] elements = new PatternElement[plainValues.length];
        for (int i = 0; i < plainValues.length; i++) {
            elements[i] = new PlainTextElement(plainValues[i]);
        }
        this.elements = elements;
    }

    @Override
    public String generateSingleValue(Random random) {
        int index = random.nextInt(elements.length);
        return elements[index].generateValue(random);
    }

    public PatternElement getElement(Random random) {
        int index = random.nextInt(elements.length);
        return elements[index];
    }
}
