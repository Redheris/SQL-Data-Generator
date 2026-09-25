package dev.redheris.sqldatagenerator.generator.syntax.pattern;

import java.util.Random;

public abstract class AbstractStretchableElement extends PatternElement implements Stretchable {
    private int minLength = 1;
    private int maxLength = 1;

    @Override
    public int minLength() {
        return minLength;
    }

    @Override
    public int maxLength() {
        return maxLength;
    }

    @Override
    public void setLength(int minLength, int maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public final String generateValue(Random random) {
        StringBuilder sb = new StringBuilder();
        int length = random.nextInt(minLength(), maxLength() + 1);

        for (int i = 0; i < length; i++) {
            sb.append(generateSingleValue(random));
        }

        return sb.toString();
    }

    protected abstract String generateSingleValue(Random random);
}
