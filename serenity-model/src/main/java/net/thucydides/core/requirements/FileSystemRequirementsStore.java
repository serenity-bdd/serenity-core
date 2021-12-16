package net.thucydides.core.requirements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.thucydides.core.reports.json.gson.CollectionAdapter;
import net.thucydides.core.reports.json.gson.OptionalTypeAdapter;
import net.thucydides.core.requirements.model.Requirement;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class FileSystemRequirementsStore implements RequirementsStore {
    private final File outputDirectory;
    private final String storeName;
    Gson gson;

    public FileSystemRequirementsStore(File outputDirectory, String storeName) {
        this.outputDirectory = outputDirectory;
        this.storeName = storeName;
        this.gson = new GsonBuilder()
                .registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY)
                .registerTypeHierarchyAdapter(Collection.class, new CollectionAdapter()).create();

    }

    public Optional<List<Requirement>> read() throws IOException{
        File jsonFile = jsonRequirementsFile();
        if(!jsonFile.exists()) {
            return Optional.empty();
        }

        List<Requirement> requirements;
        Type requirementsListType = new TypeToken<List<Requirement>>(){}.getType();
        try(InputStreamReader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8)) {
            requirements = gson.fromJson(reader, requirementsListType);
            if (requirements == null) {
                requirements = new ArrayList<>();
            }
        }

        if (requirements.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(requirements);
        }
    }

    private File jsonRequirementsFile() {
        return new File(outputDirectory, storeName);
    }


    public void write(List<Requirement> requirements) throws IOException {

        if (requirements.isEmpty()) { return; }

        ensureThatTheOutputDirectoryExists();

        try( Writer writer = new OutputStreamWriter(new FileOutputStream(jsonRequirementsFile()), StandardCharsets.UTF_8)) {
            gson.toJson(requirements, writer);
        }
    }

    private void ensureThatTheOutputDirectoryExists() throws IOException {
        if (!outputDirectory.exists()) {
            Files.createDirectory(outputDirectory.toPath());
        }
    }

    public void clear() {
        try {
            if (jsonRequirementsFile().exists()) {
                Files.delete(jsonRequirementsFile().toPath());
            }
        } catch (IOException ignore) {}
    }
}
