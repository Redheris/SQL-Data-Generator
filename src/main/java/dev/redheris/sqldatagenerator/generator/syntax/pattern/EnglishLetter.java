package dev.redheris.sqldatagenerator.generator.syntax.pattern;

import java.util.Random;

public class EnglishLetter extends AbstractStretchableElement implements Stretchable {
    public final static String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    public String generateSingleValue(Random random) {
        int index = random.nextInt(LETTERS.length());
        return LETTERS.substring(index, index + 1);
    }
}
