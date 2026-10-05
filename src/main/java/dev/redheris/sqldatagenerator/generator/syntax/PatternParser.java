package dev.redheris.sqldatagenerator.generator.syntax;

import dev.redheris.sqldatagenerator.generator.syntax.pattern.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class PatternParser {
    public Pattern parsePatternString(
            Map<String, String[]> placeholdersRaw,
            Map<String, String> models,
            String valueGenPattern,
            boolean plainByDefault
    ) {
        Pattern pattern = new Pattern();

        Map<String, ElementsListChoice> placeholders = convertPlaceholders(placeholdersRaw);
        AtomicInteger index = new AtomicInteger(0);

        for (String modelName : models.keySet()) {
            valueGenPattern = valueGenPattern.replace(
                    "<%s>".formatted(modelName),
                    models.get(modelName)
            );
        }

        while (index.get() < valueGenPattern.length()) {
            pattern.addElement(parseElement(
                    placeholders,
                    valueGenPattern,
                    index,
                    false,
                    plainByDefault
            ));
        }

        return pattern;
    }

    private Map<String, ElementsListChoice> convertPlaceholders(Map<String, String[]> placeholdersRaw) {
        return placeholdersRaw.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new ElementsListChoice(entry.getValue())
                ));
    }

    private Pattern parsePatternSubstring(
            Map<String, ElementsListChoice> placeholders,
            String substring,
            boolean plainByDefault
    ) {
        Pattern pattern = new Pattern();
        AtomicInteger index = new AtomicInteger(0);

        while (index.get() < substring.length()) {
            pattern.addElement(parseElement(
                    placeholders,
                    substring,
                    index,
                    false,
                    plainByDefault
            ));
        }

        return pattern;
    }

    private PatternElement parseElement(
            Map<String, ElementsListChoice> placeholders,
            String string,
            AtomicInteger index,
            boolean escape,
            boolean plainByDefault
    ) {
        if (plainByDefault ^ escape) {
            return new PlainTextElement(string.charAt(index.getAndIncrement()));
        }

        char ch = string.charAt(index.getAndIncrement());
        PatternElement element = switch (ch) {
            case '\\' -> parseElement(placeholders, string, index, true, plainByDefault);
            case '^' -> new UpperCaseModifier(parseElement(placeholders, string, index, false, plainByDefault));
            case '_' -> new LowerCaseModifier(parseElement(placeholders, string, index, false, plainByDefault));
            case 'E' -> new EnglishLetter();
            case 'R' -> new RussianLetter();
            case 'D' -> new NumberValueElement();
            case '(' -> {
                String content = extractWrapperContent(string, index, "(", ")");
                index.addAndGet(content.length() + 1);
                yield parsePatternSubstring(placeholders, content, plainByDefault);
            }
            case '%' -> {
                String content = extractWrapperContent(string, index, "%", "%");
                index.addAndGet(content.length() + 1);

                if (!placeholders.containsKey(content)) {
                    throw new IllegalArgumentException("Unknown placeholder: %%%s%%".formatted(content));
                }

                yield placeholders.get(content);
            }
            case '[' -> parseElementsList(placeholders, string, index, plainByDefault);
            case '{' -> throw new IllegalArgumentException(
                    "'{' must be used with either a suitable pattern element or escape character");
            default -> new PlainTextElement(ch);
        };

        if (index.get() >= string.length()) {
            return element;
        }

        char suffix = string.charAt(index.get());
        if (suffix == '{') {
            if (string.charAt(index.get() + 1) == '{') {
                index.addAndGet(2);
                String rangeString = extractWrapperContent(string, index, "{{", "}}");

                Range range = parseRange(rangeString, false);
                ((HasRange) element).setRange(range.min, range.max);

                index.addAndGet(rangeString.length() + 2);
            } else {
                index.addAndGet(1);
                String rangeString = extractWrapperContent(string, index, "{", "}");

                Range range = parseRange(rangeString, true);
                ((Stretchable) element).setLength(range.min, range.max);

                index.addAndGet(rangeString.length() + 1);
            }
        }

        return element;
    }

    private ElementsListChoice parseElementsList(
            Map<String, ElementsListChoice> placeholders,
            String string,
            AtomicInteger index,
            boolean plainByDefault
    ) {
        String content = extractWrapperContent(string, index, "[", "]");

        List<PatternElement> elements = new ArrayList<>();
        AtomicInteger subindex = new AtomicInteger(0);
        while (subindex.get() < content.length()) {
            elements.add(parseElement(
                    placeholders,
                    content,
                    subindex,
                    false,
                    plainByDefault
            ));
        }

        index.addAndGet(content.length() + 1);
        return new ElementsListChoice(elements.toArray(new PatternElement[0]));
    }

    private Range parseRange(String substring, boolean allowSingleValue) {
        String[] parts = substring.split(",");
        if (parts.length == 2) {
            return new Range(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1])
            );
        }
        if (allowSingleValue && parts.length == 1) {
            int value = Integer.parseInt(parts[0]);
            return new Range(value, value);
        }
        if (allowSingleValue) {
            throw new IllegalArgumentException("Range must have exactly 1 or 2 arguments");
        }
        throw new IllegalArgumentException("Range must have exactly 2 arguments");
    }

    private String extractWrapperContent(String string, AtomicInteger index, String opening, String ending) {
        int endIndex = string.indexOf(ending, index.get());
        if (endIndex == -1) {
            throw new IllegalArgumentException("Unclosed '%s...%s' structure".formatted(opening, ending));
        }
        return string.substring(index.get(), endIndex);
    }


    private record Range(int min, int max) {}
}
