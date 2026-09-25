package dev.redheris.sqldatagenerator.generator.syntax.pattern;

import java.util.Random;

public class RussianLetter extends AbstractStretchableElement {
    public final static String LETTERS = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";

    @Override
    public String generateSingleValue(Random random) {
        int index = random.nextInt(LETTERS.length());
        return LETTERS.substring(index, index + 1);
    }
}
