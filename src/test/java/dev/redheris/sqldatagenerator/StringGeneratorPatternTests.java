package dev.redheris.sqldatagenerator;

import dev.redheris.sqldatagenerator.generator.syntax.PatternParser;
import dev.redheris.sqldatagenerator.generator.syntax.pattern.Pattern;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringGeneratorPatternTests {

    private final Random random = ThreadLocalRandom.current();

    private final PatternParser patternParser = new PatternParser();

    static String[][] patternCases() {
        return new String[][]{
                {"", ""},
                {"abc ABC АБВ 012", "abc ABC АБВ 012"},
                {"E", "[a-zA-Z]"},
                {"R", "[а-яА-ЯёЁ]"},
                {"D", "[0-9]"},
                {"_E", "[a-z]"},
                {"_R", "[а-яё]"},
                {"^E", "[A-Z]"},
                {"^R", "[А-ЯЁ]"},
                {"_E{6}", "[a-z]{6}"},
                {"_R{6}", "[а-яё]{6}"},
                {"D{6}", "[0-9]{6}"},
                {"^E{3,12}", "[A-Z]{3,12}"},
                {"^R{3,12}", "[А-ЯЁ]{3,12}"},
                {"D{3,12}", "[0-9]{3,12}"},
                {"[^R^ED]{12}", "[А-ЯЁA-Z0-9]{12}"},
                {"\\R \\E \\D", "R E D"},
                {"\\_R \\^\\E", "_[а-яА-ЯёЁ] \\^E"},
                {"[(abc)(bcd)]", "(abc)|(bcd)"},
                {"\\%\\%", "%%"},
                {"\\{}", "\\{}"},
                {"\\[]", "\\[]"},
                {"\\a\\b\\c\\d\\e", "abcde"},
                {"\\\\", "\\\\"},
                {"E{0}", ""},
                {"E{1}", "[a-zA-Z]"},
                {"D{{1,1}}", "1"},
                {"_(AbCd\\EfG)", "abcdefg"},
                {"^(AbCd\\EfG)", "ABCDEFG"},
        };
    }

    @ParameterizedTest
    @MethodSource("patternCases")
    void shouldGenerateExpectedValue(String generatorPattern, String expectedRegex) {
        Pattern pattern = patternParser.parsePatternString(
                new HashMap<>(),
                new HashMap<>(),
                generatorPattern,
                false
        );

        String generated = pattern.generateValue(random);

        assertThat(generated)
                .withFailMessage(() -> "Generated string \"%s\" doesn't match the expected regex \"%s\""
                        .formatted(generated, expectedRegex))
                .matches(expectedRegex);
    }


    @ParameterizedTest
    @CsvSource("""
            0, 0
            3, 3
            -10, -5
            -10, 10
            3, 12
            22, 222
            1, 10000
            """)
    void shouldGenerateNumberInRange(int min, int max) {
        Pattern pattern = patternParser.parsePatternString(
                new HashMap<>(),
                new HashMap<>(),
                "D{{%d,%d}}".formatted(min, max),
                false
        );

        for (int i = 0; i < 100; i++) {
            String generated = pattern.generateValue(random);
            int generatedInt = Integer.parseInt(generated);

            assertThat(generatedInt)
                    .withFailMessage(() -> "Generated number %d is not between [%d, %d]"
                            .formatted(generatedInt, min, max))
                    .isBetween(min, max);
        }
    }

    static Map<String, String[]> placeholders() {
        return Map.of(
                "RED", new String[]{"red", "green", "blue"},
                "first_name", new String[]{"Alex", "Dima"},
                "surname", new String[]{"Smith", "Andersen"}
        );
    }

    static Map<String, String> models() {
        return Map.of(
                "phone", "+D-DDD-DDD-DD-DD",
                "email", "[ED]{5,20}@_E{2,6}._E{2,6}",
                "full_name", "%first_name% %surname%"
        );
    }

    static String[][] placeholderAndModelCases() {
        return new String[][]{
                {"%RED%", "(red|green|blue)"},
                {"<phone>", "\\+\\d-\\d{3}-\\d{3}-\\d{2}-\\d{2}"},
                {"<email>", "[a-zA-Z\\d]{5,20}@[a-z]{2,6}\\.[a-z]{2,6}"},
                {"%first_name%, phone: <phone>", "(Alex|Dima), phone: \\+\\d-\\d{3}-\\d{3}-\\d{2}-\\d{2}"},
                {"<full_name>", "(Alex|Dima) (Smith|Andersen)"}
        };
    }

    @ParameterizedTest
    @MethodSource("placeholderAndModelCases")
    void shouldParsePlaceholdersAndModels(String generatorPattern, String expectedRegex) {
        Pattern pattern = patternParser.parsePatternString(
                placeholders(),
                models(),
                generatorPattern,
                false
        );

        for (int i = 0; i < 20; i++) {
            String generated = pattern.generateValue(random);

            assertThat(generated)
                    .withFailMessage(() -> "Generated string \"%s\" doesn't match the expected regex \"%s\""
                            .formatted(generated, expectedRegex))
                    .matches(expectedRegex);
        }
    }

    static String[] negativeCases() {
        return new String[] {
                "{",
                "[",
                "%",
//                "<", // FIXME: Failed with infinite loop
                "a{1,5}",
                "E{}",
                "E{5,0}",
                "E{{2,7}}",
                "D{{2,7}",
                "D{{7,3}}",
                "%unknown%",
//                "<unknown>",
                "[[abc]def]",
        };
    }

    @ParameterizedTest
    @MethodSource("negativeCases")
    void shouldFailGeneration(String generatorPattern) {
        assertThrows(
                Exception.class,
                () -> {
                    Pattern pattern = patternParser.parsePatternString(
                            new HashMap<>(),
                            new HashMap<>(),
                            generatorPattern,
                            false
                    );
                    pattern.generateValue(random);
                },
                () -> "Execution didn't fail with invalid pattern: \"%s\""
                        .formatted(generatorPattern)
        );
    }
}
