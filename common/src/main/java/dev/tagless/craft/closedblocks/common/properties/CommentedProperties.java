package dev.tagless.craft.closedblocks.common.properties;

import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.util.*;

@SuppressWarnings("unused")
@ApiStatus.Experimental
public class CommentedProperties extends Properties {

    private final Map<String, PropertyValue[]> valueMap = new LinkedHashMap<>();

    /**
     * Carga el archivo .properties procesando comentarios, multilíneas y comentarios inline.
     */
    public void load(Reader reader) throws IOException {
        try (BufferedReader br = new BufferedReader(reader)) {
            List<String> currentComments = new ArrayList<>();
            String line;

            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();

                if (trimmed.isEmpty()) {
                    continue;
                }
                if (trimmed.startsWith("#") || trimmed.startsWith("!")) {
                    currentComments.add(line);
                    continue;
                }

                StringBuilder fullLine = new StringBuilder(line);
                while (hasTrailerBackslash(fullLine.toString()) && (line = br.readLine()) != null) {
                    fullLine.setLength(fullLine.length() - 1);
                    fullLine.append(line.stripLeading());
                }

                parseLogicalLine(fullLine.toString(), currentComments);
                currentComments = new ArrayList<>();
            }
        }
    }

    private void parseLogicalLine(String rawLine, List<String> comments) {
        int sepIdx = findKeyValueSeparator(rawLine);
        if (sepIdx == -1) return;

        String key = rawLine.substring(0, sepIdx).trim();
        String rawValuePart = rawLine.substring(sepIdx + 1);

        String[] valueAndInline = splitInlineComment(rawValuePart);
        String cleanValue = valueAndInline[0].trim();
        String inlineComment = valueAndInline[1];

        PropertyValue propValue;
        String[] commentsArray = comments.toArray(new String[0]);

        if (cleanValue.contains(",")) {
            String[] parts = Arrays.stream(cleanValue.split(","))
                    .map(String::trim)
                    .toArray(String[]::new);
            propValue = PropertyValue.create(commentsArray, inlineComment, parts);
        } else {
            propValue = PropertyValue.create(commentsArray, inlineComment, cleanValue);
        }

        valueMap.computeIfAbsent(key, k -> new PropertyValue[0]);
        PropertyValue[] existing = valueMap.get(key);
        PropertyValue[] extended = Arrays.copyOf(existing, existing.length + 1);
        extended[existing.length] = propValue;

        valueMap.put(key, extended);
        super.put(key, cleanValue);
    }

    private boolean hasTrailerBackslash(String str) {
        if (!str.endsWith("\\")) {
            return false;
        }

        int count = 0;
        for (int i = str.length() - 1; i >= 0 && str.charAt(i) == '\\'; i--) {
            count++;
        }
        return count % 2 != 0;
    }

    private int findKeyValueSeparator(String str) {
        boolean escaped = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\\') {
                escaped = !escaped;
            } else if ((c == '=' || c == ':') && !escaped) {
                return i;
            } else {
                escaped = false;
            }
        }
        return -1;
    }

    private String[] splitInlineComment(String rawValue) {
        boolean escaped = false;
        for (int i = 0; i < rawValue.length(); i++) {
            char c = rawValue.charAt(i);
            if (c == '\\') {
                escaped = !escaped;
            } else if ((c == '#' || c == '!') && !escaped) {
                String value = rawValue.substring(0, i);
                String comment = rawValue.substring(i).trim();
                return new String[]{value, comment};
            } else {
                escaped = false;
            }
        }

        return new String[]{rawValue, null};
    }

    public Map<String, PropertyValue[]> getValueMap() {
        return Collections.unmodifiableMap(this.valueMap);
    }

    public void store(Writer writer) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(writer)) {
            for (Map.Entry<String, PropertyValue[]> entry : valueMap.entrySet()) {
                String key = entry.getKey();
                PropertyValue[] values = entry.getValue();

                for (PropertyValue propValue : values) {
                    if (propValue.getComments() != null) {
                        for (String comment : propValue.getComments()) {
                            bw.write(comment);
                            bw.newLine();
                        }
                    }

                    String fullValue;
                    if (propValue.isArray() && propValue.getValue() != null) {
                        fullValue = String.join(", ", propValue.getValue());
                    } else if (propValue.getValue() != null && propValue.getValue().length > 0) {
                        fullValue = propValue.getValue()[0];
                    } else {
                        fullValue = "";
                    }

                    bw.write(key);
                    bw.write(" = ");

                    bw.write(fullValue);

                    if (propValue.getInlineComment() != null) {
                        bw.write(" ");
                        bw.write(propValue.getInlineComment());
                    }

                    bw.newLine();
                }
            }
            bw.flush();
        }
    }
}