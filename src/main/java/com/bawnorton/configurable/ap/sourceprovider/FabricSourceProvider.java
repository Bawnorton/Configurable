package com.bawnorton.configurable.ap.sourceprovider;

//? if fabric {

/*import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javax.annotation.processing.Filer;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FabricSourceProvider extends SourceProvider {
    private JsonObject fmj;

    public FabricSourceProvider(Filer filer, Path buildPath) {
        this(filer, buildPath, new GsonBuilder().setPrettyPrinting().create());
    }

    public FabricSourceProvider(Filer filer, Path buildPath, Gson gson) {
        super(filer, buildPath);
        try {
            fmj = gson.fromJson(getConfigFile(), JsonObject.class);
        } catch (IOException e) {
            fmj = null;
        }
    }

    @Override
    protected Reader getConfigFile() throws IOException {
        Path projectRoot = findProjectRoot();
        String json = Files.readString(projectRoot.resolve("src/main/resources/fabric.mod.json"));
        return new StringReader(json);
    }

    @Override
    public boolean matches() {
        return fmj != null;
    }

    @Override
    public String getName() {
        return fmj.get("id").getAsString();
    }
}
*///?}
