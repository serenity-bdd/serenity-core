package net.thucydides.core.requirements;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.thucydides.core.reports.json.gson.CollectionAdapter;
import net.thucydides.core.reports.json.gson.OptionalTypeAdapter;
import net.thucydides.core.requirements.model.Requirement;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;

public class RequirementsStore {
    private final File outputDirectory;
    private final String storeName;
    Gson gson;

    public RequirementsStore(File outputDirectory, String storeName) {
        this.outputDirectory = outputDirectory;
        this.storeName = storeName;
        this.gson = new GsonBuilder()
                .registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY)
                .registerTypeHierarchyAdapter(Collection.class, new CollectionAdapter()).create();

    }

    public Optional<List<Requirement>> read() throws IOException{
        File jsonFile = jsonRequirementsFile();
        if(!jsonFile.exists()) {
            return Optional.absent();
        }

        System.out.println("READING REQUIREMENTS FROM " + jsonRequirementsFile());

        List<Requirement> requirements;
        Type requirementsListType = new TypeToken<List<Requirement>>(){}.getType();
        try(FileReader reader = new FileReader(jsonFile)) {
            requirements = gson.fromJson(reader, requirementsListType);
            if (requirements == null) {
                requirements = Lists.newArrayList();
            }
        }

        return Optional.of(requirements);
    }

    private File jsonRequirementsFile() {
        return new File(outputDirectory, storeName);
    }


    public void write(List<Requirement> requirements) throws IOException {

        if (requirements.isEmpty()) { return; }

        ensureThatTheOutputDirectoryExists();

        try( Writer writer = new FileWriter(jsonRequirementsFile())) {
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
