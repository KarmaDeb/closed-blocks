package dev.tagless.craft.closedblocks.common.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PropertyValue {

    private final String[] comments;
    private final String inlineComment;
    private final String[] value;
    private final boolean array;

    public static PropertyValue create(final String[] comments, final String inlineComment, final String[] value) {
        return new PropertyValue(comments, inlineComment, value, true);
    }

    public static PropertyValue create(final String[] comments, final String inlineComment, final String value) {
        return new PropertyValue(comments, inlineComment, new String[]{value}, false);
    }
}
