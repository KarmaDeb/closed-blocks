package dev.tagless.test.engine;

import dev.tagless.craft.closedblocks.api.ClosedBlocksAPI;
import dev.tagless.craft.closedblocks.api.engine.Javatype;
import dev.tagless.craft.closedblocks.common.engine.ScriptingEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

class ScriptingEngineTest {

    static ScriptingEngine engine;

    @BeforeAll
    static void setup() {
        engine = new ScriptingEngine(
            Javatype.pack(ClosedBlocksAPI.class),
            Javatype.pack("net.md_5.bungee."),

            Javatype.instance(Iterator.class),
            Javatype.instance(Collection.class),
            Javatype.instance(Map.class),
            Javatype.instance(CharSequence.class),

            Javatype.type(Character.class),
            Javatype.type(Character.TYPE),
            Javatype.type(Byte.class),
            Javatype.type(Byte.TYPE),
            Javatype.type(Short.class),
            Javatype.type(Short.TYPE),
            Javatype.type(Integer.class),
            Javatype.type(Integer.TYPE),
            Javatype.type(Long.class),
            Javatype.type(Long.TYPE),
            Javatype.type(Float.class),
            Javatype.type(Float.TYPE),
            Javatype.type(Double.class),
            Javatype.type(Double.TYPE),

            Javatype.type(BigInteger.class),
            Javatype.type(BigDecimal.class),

            Javatype.type(UUID.class)
        );
    }

    @Test
    void testCanRun() {
        Assertions.assertDoesNotThrow(() -> {
            engine.execute("""
                    const JavaString = Java.type('java.lang.String');
                    const text = new JavaString('Hello!');
                    console.log(text);
                    """);
        });
    }
}
