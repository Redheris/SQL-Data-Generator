package dev.redheris.sqldatagenerator.generator.syntax;

import dev.redheris.sqldatagenerator.generator.syntax.pattern.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class PatternParser {
    public List<PatternElement> parsePatternString(
            Random random,
            Map<String, String[]> placeholdersRaw,
            Map<String, String> models,
            String valueGenPattern,
            boolean plainByDefault
    ) {
        List<PatternElement> elements = new ArrayList<>();

        Map<String, ElementsListChoice> placeholders = convertPlaceholders(placeholdersRaw);
        AtomicInteger index = new AtomicInteger(0);

        for (String modelName : models.keySet()) {
            valueGenPattern = valueGenPattern.replace(
                    "<%s>".formatted(modelName),
                    models.get(modelName)
            );
        }

        while (index.get() < valueGenPattern.length()) {
            elements.add(parseElement(
                    random,
                    placeholders,
                    valueGenPattern,
                    index,
                    false,
                    plainByDefault
            ));
        }

        return elements;
    }

    private Map<String, ElementsListChoice> convertPlaceholders(Map<String, String[]> placeholdersRaw) {
        return placeholdersRaw.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new ElementsListChoice(entry.getValue())
                ));
    }

    private PatternElement parseElement(
            Random random,
            Map<String, ElementsListChoice> placeholders,
            String string,
            AtomicInteger index,
            boolean escape,
            boolean plainByDefault
    ) {
        if (plainByDefault ^ escape) {
            return new PlainTextElement(string.charAt(index.getAndIncrement()));
        }

        // TODO: "(...)" wrapper binding a list of elements to use in "[...]"

        char ch = string.charAt(index.getAndIncrement());
        PatternElement element = switch (ch) {
            case '\\' -> parseElement(random, placeholders, string, index, true, plainByDefault);
            case '^' -> new UpperCaseModifier(parseElement(random, placeholders, string, index, false, plainByDefault));
            case '_' -> new LowerCaseModifier(parseElement(random, placeholders, string, index, false, plainByDefault));
            case 'E' -> new EnglishLetter();
            case 'R' -> new RussianLetter();
            case 'D' -> new NumberValueElement();
            case '%' -> {
                int endIndex = string.indexOf('%', index.get());
                if (endIndex == -1) {
                    throw new IllegalArgumentException("Unclosed '%...%' placeholder ");
                }

                String content = string.substring(index.get(), endIndex);
                index.addAndGet(content.length() + 1);

                if (!placeholders.containsKey(content)) {
                    throw new IllegalArgumentException("Unknown placeholder: %%%s%%".formatted(content));
                }

                yield placeholders.get(content).getElement(random);
            }
            case '[' -> parseElementsList(random, placeholders, string, index, plainByDefault);
            case '{' -> throw new IllegalArgumentException(
                    "'{' must be used with either a suitable pattern element or escape character");
            default -> new PlainTextElement(ch);
        };

        if (index.get() >= string.length()) {
            return element;
        }

        char suffix = string.charAt(index.get());
        if (suffix == '{') {
            int endIndex;
            if (string.charAt(index.get() + 1) == '{') {
                endIndex = string.indexOf("}}", index.get());
                if (endIndex == -1) {
                    throw new IllegalArgumentException("Unclosed '{{...}}' suffix");
                }
                String rangeString = string.substring(index.get() + 2, endIndex);

                Range range = parseRange(rangeString, false);
                ((HasRange) element).setRange(range.min, range.max);

                index.addAndGet(rangeString.length() + 4);
            } else {
                endIndex = string.indexOf("}", index.get());
                if (endIndex == -1) {
                    throw new IllegalArgumentException("Unclosed '{...}' suffix");
                }
                String rangeString = string.substring(index.get() + 1, endIndex);

                Range range = parseRange(rangeString, true);
                ((Stretchable) element).setLength(range.min, range.max);

                index.addAndGet(rangeString.length() + 2);
            }
        }

        return element;
    }

    private ElementsListChoice parseElementsList(
            Random random,
            Map<String, ElementsListChoice> placeholders,
            String string,
            AtomicInteger index,
            boolean plainByDefault
    ) {
        int endIndex = string.indexOf("]", index.get());
        if (endIndex == -1) {
            throw new IllegalArgumentException("Unclosed '[...]' elements list");
        }

        String content = string.substring(index.get(), endIndex);

        List<PatternElement> elements = new ArrayList<>();
        AtomicInteger subindex = new AtomicInteger(0);
        while (subindex.get() < content.length()) {
            elements.add(parseElement(
                    random,
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


    private record Range(int min, int max) {}
}
