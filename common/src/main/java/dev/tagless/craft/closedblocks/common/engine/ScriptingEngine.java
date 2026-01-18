package dev.tagless.craft.closedblocks.common.engine;

import dev.tagless.craft.closedblocks.api.engine.Javatype;
import dev.tagless.craft.closedblocks.api.engine.ScriptEngine;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.IOAccess;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;

public class ScriptingEngine extends ScriptEngine {

    private static final String LANGUAGE_JAVASCRIPT = "js";

    public ScriptingEngine(final Javatype... allowedPackages) {
        super(allowedPackages);
    }

    /**
     * Executes the script
     *
     * @param script         the script
     * @param javatypes      the javatypes
     * @param mappedBindings the mapped bindings
     */
    @Override
    protected void executeScript(final @NotNull CharSequence script,
                                 final @NotNull Collection<Javatype> javatypes,
                                 final @NotNull Map<String, Object> mappedBindings) {
        try(Context context = Context.newBuilder(LANGUAGE_JAVASCRIPT)
            .option("engine.WarnInterpreterOnly", "false")
            .allowHostAccess(HostAccess.ALL)
            .allowHostClassLookup(c -> javatypes.stream().anyMatch(p -> p.test(c)))
            .allowIO(IOAccess.NONE)
            .allowCreateProcess(false)
            .allowCreateThread(false)
            .allowEnvironmentAccess(EnvironmentAccess.NONE)
            .allowNativeAccess(false)
            .build()) {

            String name = String.format("%s-%s", LANGUAGE_JAVASCRIPT, md5(script));
            Source source = Source.newBuilder(LANGUAGE_JAVASCRIPT, script, name)
                .cached(true)
                .interactive(false)
                .internal(false)
                .build();

            Value bindings = context.getBindings(LANGUAGE_JAVASCRIPT);
            if (bindings != null) {
                mappedBindings.forEach(bindings::putMember);
            }

            context.eval(source);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String md5(final CharSequence script) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();

            int bufferSize = 4096;
            char[] charArray = new char[bufferSize];

            ByteBuffer byteBuffer = ByteBuffer.allocate((int) (bufferSize * encoder.maxBytesPerChar()));
            int length = script.length();
            for (int i = 0; i < length; i += bufferSize) {
                int end = Math.min(i + bufferSize, length);

                if (script instanceof String scriptString) {
                    scriptString.getChars(i, end, charArray, 0);
                } else {
                    for (int j = i; j < end; j++) {
                        charArray[j - i] = script.charAt(j);
                    }
                }

                byteBuffer.clear();
                encoder.encode(CharBuffer.wrap(charArray, 0, end - i), byteBuffer, end == length);
                byteBuffer.flip();
                md5.update(byteBuffer);
            }

            return bytesToHex(md5.digest());
        } catch (NoSuchAlgorithmException ex) {
            return "";
        }
    }

    private String bytesToHex(final byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}