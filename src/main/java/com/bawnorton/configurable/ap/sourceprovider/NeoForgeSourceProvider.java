package com.bawnorton.configurable.ap.sourceprovider;

//? if neoforge {

/*import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;
import javax.annotation.processing.Filer;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public final class NeoForgeSourceProvider extends SourceProvider {
    private Config config;

    public NeoForgeSourceProvider(Filer filer, Path buildPath) {
        super(filer, buildPath);
        try {
            Reader reader = getConfigFile();
            TomlParser parser = new TomlParser();
            config = parser.parse(reader);
        } catch (IOException ignored) {
        }
    }

    @Override
    protected Reader getConfigFile() throws IOException {
        Path projectRoot = findProjectRoot();
        String toml = Files.readString(projectRoot.resolve("src/main/resources/META-INF/neoforge.mods.toml"));
        return new StringReader(toml);
    }

    @Override
    public boolean matches() {
        return config != null;
    }

    @Override
    public String getName() {
        return config.<ArrayList<Config>>get("mods").getFirst().get("modId");
    }
}
*///?}
