package dev.tagless.craft.closedblocks.engine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class RecipeNormalizer {

    private final String[][] shape;

    private final Map<String, Character> mappings = new HashMap<>();
    private final Map<Character, String> resolvers = new HashMap<>();

    private final String[] normalized = new String[3];

    private char index = 'A';

    public RecipeNormalizer(final String[][] shape) {
        if (shape.length != 3) {
            throw new IllegalArgumentException("Invalid recipe shape, expected 3 elements but got " + shape.length);
        }
        this.shape = shape;
    }

    public void normalize() {
        for (int i = 0; i < this.shape.length; i++) {
            String[] node = this.shape[i];
            String flatten = this.processNode(node);

            this.normalized[i] = flatten;
        }
    }

    public String[] result() {
        return Arrays.copyOf(this.normalized, this.normalized.length);
    }

    public String resolve(final char character) {
        return this.resolvers.get(character);
    }

    private String processNode(final String[] node) {
        if (node.length != 3) {
            throw new IllegalArgumentException("Invalid recipe node, expected 3 elements but got " + node.length);
        }

        String[] rs = new String[3];
        for (int i = 0; i < node.length; i++) {
            String namespaced = node[i];
            Character charValue = mappings.get(namespaced);
            if (charValue == null) {
                charValue = this.index;
                this.index = (char) (this.index + 1);

                mappings.put(namespaced, charValue);
                resolvers.put(charValue, namespaced);
            }

            rs[i] = String.valueOf(charValue);
        }

        return String.join("", rs);
    }
}
