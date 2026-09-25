package dev.redheris.sqldatagenerator.generator.syntax.pattern;

import java.util.Random;

public class NumberValueElement extends AbstractStretchableElement implements HasRange {
    private int minValue = 0;
    private int maxValue = 9;

    @Override
    public String generateSingleValue(Random random) {
        return Integer.toString(random.nextInt(minValue, maxValue + 1));
    }

    @Override
    public int minValue() {
        return minValue;
    }

    @Override
    public int maxValue() {
        return maxValue;
    }

    public void setRange(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}
