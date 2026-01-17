package dev.tagless.craft.closedblocks.common.engine;

import dev.tagless.craft.closedblocks.api.engine.Javatype;
import dev.tagless.craft.closedblocks.api.engine.ScriptEngine;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.IOAccess;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

public class ScriptingEngine extends ScriptEngine {

    private static final String LANGUAGE_JAVASCRIPT = "js";
    private static final String[] SUPPORTED_LANGUAGES = {
            LANGUAGE_JAVASCRIPT
    };


    public ScriptingEngine(final Javatype... allowedPackages) {
        super(allowedPackages);
    }

    /**
     * Executes the script
     *
     * @param file           the script file
     * @param javatypes      the javatypes
     * @param mappedBindings the mapped bindings
     */
    @Override
    protected void executeScript(final @NotNull Path file,
                                 final @NotNull Collection<Javatype> javatypes,
                                 final @NotNull Map<String, Object> mappedBindings) {
        try(Context context = Context.newBuilder(SUPPORTED_LANGUAGES)
            .allowHostAccess(HostAccess.ALL)
            .allowHostClassLookup(c -> javatypes.stream().anyMatch(p -> p.test(c)))
            .allowIO(IOAccess.NONE)
            .allowCreateProcess(false)
            .allowCreateThread(false)
            .allowEnvironmentAccess(EnvironmentAccess.NONE)
            .allowNativeAccess(false)
            .build()) {

            File f = file.toFile();
            String languageId = Source.findLanguage(f);
            Source source = Source.newBuilder(languageId, f)
                .cached(true)
                .interactive(false)
                .internal(false)
                .build();

            Value bindings = context.getBindings(languageId);
            if (bindings != null) {
                mappedBindings.forEach(bindings::putMember);
            }

            context.eval(source);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}