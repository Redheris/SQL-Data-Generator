package dev.redheris.sqldatagenerator.generator.syntax;

import dev.redheris.sqldatagenerator.generator.syntax.pattern.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class PatternParser {
    private final static String KEY_CHARACTERS = "^_ERD<%([{";

    public Pattern parsePatternString(
            Map<String, String[]> placeholdersRaw,
            Map<String, String> models,
            String valueGenPattern,
            boolean plainByDefault
    ) {
        Pattern pattern = new Pattern();

        Map<String, ElementsListChoice> placeholders = convertPlaceholders(placeholdersRaw);
        AtomicInteger index = new AtomicInteger(0);
        AtomicBoolean escape = new AtomicBoolean(false);

        while (index.get() < valueGenPattern.length()) {
            pattern.addElement(parseElement(
                    placeholders,
                    models,
                    valueGenPattern,
                    index,
                    escape,
                    plainByDefault,
                    true
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
            Map<String, String> models,
            String substring,
            AtomicBoolean escape,
            boolean plainByDefault
    ) {
        Pattern pattern = new Pattern();
        AtomicInteger index = new AtomicInteger(0);
        escape.set(false);

        while (index.get() < substring.length()) {
            pattern.addElement(parseElement(
                    placeholders,
                    models,
                    substring,
                    index,
                    escape,
                    plainByDefault,
                    true
            ));
        }

        return pattern;
    }

    private PatternElement parseElement(
            Map<String, ElementsListChoice> placeholders,
            Map<String, String> models,
            String string,
            AtomicInteger index,
            AtomicBoolean escape,
            boolean plainByDefault,
            boolean concatPlainTextElements
    ) {
        if (!escape.get() && string.charAt(index.get()) == '\\') {
            index.incrementAndGet();
            escape.set(true);
            return parseElement(placeholders, models, string, index, escape, plainByDefault, concatPlainTextElements);
        }

        if (plainByDefault ^ escape.get()) {
            return parsePlainText(string, index, escape, plainByDefault, concatPlainTextElements);
        }

        escape.set(false);

        char ch = string.charAt(index.getAndIncrement());
        PatternElement element = switch (ch) {
            case '^' -> new UpperCaseModifier(parseElement(
                        placeholders, models, string, index, escape, plainByDefault, concatPlainTextElements
                ));
            case '_' -> new LowerCaseModifier(parseElement(
                        placeholders, models, string, index, escape, plainByDefault, concatPlainTextElements
                ));
            case 'E' -> new EnglishLetter();
            case 'R' -> new RussianLetter();
            case 'D' -> new NumberValueElement();
            case '(' -> {
                String content = extractWrapperContent(string, index, "(", ")");
                try {
                    Pattern pattern = parsePatternSubstring(placeholders, models, content, escape, plainByDefault);
                    index.addAndGet(content.length() + 1);
                    yield pattern;
                } catch (RuntimeException e) {
                    throw new IllegalArgumentException(
                            "Exception during parsing pattern group: index %d: %s"
                                    .formatted(index.get(), e.getMessage()),
                            e
                    );
                }
            }
            case '<' -> {
                String content = extractWrapperContent(string, index, "<", ">");

                if (!models.containsKey(content)) {
                    throw new IllegalArgumentException("Unknown model: <%s>, index %d"
                            .formatted(content, index.get()));
                }
                index.addAndGet(content.length() + 1);

                try {
                    yield parsePatternSubstring(
                            placeholders,
                            models,
                            models.get(content),
                            new AtomicBoolean(false),
                            false
                    );
                } catch (RuntimeException e) {
                    throw new IllegalArgumentException(
                            "Exception during parsing model <%s>: %s".formatted(content, e.getMessage()),
                            e
                    );
                }
            }
            case '%' -> {
                String content = extractWrapperContent(string, index, "%", "%");
                index.addAndGet(content.length() + 1);

                if (!placeholders.containsKey(content)) {
                    throw new IllegalArgumentException("Unknown placeholder: %%%s%%, index %d"
                            .formatted(content, index.get()));
                }

                yield placeholders.get(content);
            }
            case '[' -> parseElementsList(placeholders, models, string, index, escape, plainByDefault);
            case '{' -> throw new IllegalArgumentException(
                    "'{' must be used with either a suitable pattern element or escape character: index %d"
                            .formatted(index.get()));
            default -> {
                index.decrementAndGet();
                yield parsePlainText(string, index, escape, plainByDefault, concatPlainTextElements);
            }
        };

        if (index.get() >= string.length()) {
            return element;
        }

        char suffix = string.charAt(index.get());
        if (suffix == '{') {
            if (string.charAt(index.get() + 1) == '{') {
                index.addAndGet(2);
                String rangeString = extractWrapperContent(string, index, "{{", "}}");

                if (element instanceof HasRange ranged) {
                    Range range = parseRange(rangeString, false, index);
                    ranged.setRange(range.min, range.max);
                    index.addAndGet(rangeString.length() + 2);
                } else {
                    throw new IllegalStateException("Element doesn't support {{...}} modifier: index %d"
                            .formatted(index.get()));
                }
            } else {
                index.addAndGet(1);
                String rangeString = extractWrapperContent(string, index, "{", "}");

                if (element instanceof Stretchable stretchable) {
                    Range range = parseRange(rangeString, true, index);
                    stretchable.setLength(range.min, range.max);
                    index.addAndGet(rangeString.length() + 1);
                } else {
                    throw new IllegalStateException("Element doesn't support {...} modifier: index %d"
                            .formatted(index.get()));
                }
            }
        }

        return element;
    }

    private PlainTextElement parsePlainText(
            String string, AtomicInteger index, AtomicBoolean escape, boolean plainByDefault,
            boolean concatPlainTextElements
    ) {
        if (!concatPlainTextElements) {
            char ch = string.charAt(index.getAndIncrement());
            return new PlainTextElement(ch);
        }

        StringBuilder text = new StringBuilder();
        char ch;

        while (index.get() < string.length()) {
            ch = string.charAt(index.get());

            if (!escape.get() && ch == '\\') {
                escape.set(true);
                index.incrementAndGet();
                continue;
            }

            if (plainByDefault == escape.get() && KEY_CHARACTERS.indexOf(ch) != -1) {
                break;
            }

            text.append(ch);
            escape.set(false);
            index.incrementAndGet();
        }

        return new PlainTextElement(text.toString());
    }

    private ElementsListChoice parseElementsList(
            Map<String, ElementsListChoice> placeholders,
            Map<String, String> models,
            String string,
            AtomicInteger index,
            AtomicBoolean escape,
            boolean plainByDefault
    ) {
        String content = extractWrapperContent(string, index, "[", "]");

        List<PatternElement> elements = new ArrayList<>();
        AtomicInteger subindex = new AtomicInteger(0);
        while (subindex.get() < content.length()) {
            try {
                elements.add(parseElement(
                        placeholders,
                        models,
                        content,
                        subindex,
                        escape,
                        plainByDefault,
                        false
                ));
            } catch (RuntimeException e) {
                throw new IllegalArgumentException(
                        "Exception during parsing elements list: index %d: %s"
                                .formatted(index.get(), e.getMessage()),
                        e
                );
            }
        }

        index.addAndGet(content.length() + 1);
        return new ElementsListChoice(elements.toArray(new PatternElement[0]));
    }

    private Range parseRange(String substring, boolean allowSingleValue, AtomicInteger index) {
        String[] parts = substring.split(",");
        try {
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
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid integer number format: index %d: %s".formatted(index.get(), e.getMessage())
            );
        }
        if (allowSingleValue) {
            throw new IllegalArgumentException("Range must have exactly 1 or 2 arguments: index %d"
                    .formatted(index.get()));
        }
        throw new IllegalArgumentException("Range must have exactly 2 arguments: index %d"
                .formatted(index.get()));
    }

    private String extractWrapperContent(String string, AtomicInteger index, String opening, String ending) {
        int endIndex = string.indexOf(ending, index.get());
        if (endIndex == -1) {
            throw new IllegalArgumentException("Unclosed '%s...%s' structure: index %d"
                    .formatted(opening, ending, index.get()));
        }
        return string.substring(index.get(), endIndex);
    }

    private record Range(int min, int max) {}
}
