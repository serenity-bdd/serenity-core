package net.thucydides.core.requirements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.thucydides.core.requirements.model.Requirement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;

public class RequirementPersister {
    private final File outputDirectory;
    private final String rootDirectory;
    Gson gson;

    public RequirementPersister(File outputDirectory, String rootDirectory) {
        this.outputDirectory = outputDirectory;
        this.rootDirectory = rootDirectory;
        this.gson = new GsonBuilder().create();
    }

    public SortedMap<String, Requirement> read() throws IOException{
        SortedMap<String, Requirement> map = new ChildrenFirstOrderedMap();
        File jsonFile = new File(outputDirectory, rootDirectory + ".json");
        if(!jsonFile.exists()) {
            return map;
        }

        JavaType type = mapper.getTypeFactory().constructMapType(map.getClass(), String.class, Requirement.class);
        SortedMap<String, Requirement> storedRequirementsMap = mapper.readValue(jsonFile, type);

        map.putAll(storedRequirementsMap);
        //reset the parents
        for (Map.Entry<String, Requirement> entry : storedRequirementsMap.entrySet()) {
            String key = entry.getKey();
            if (key.contains(".")) {
                String parent = key.substring(0, key.lastIndexOf("."));
                Requirement child = entry.getValue();
                updateParentChildren(map, parent, child);
            }
        }
        return map;
    }

    private void updateParentChildren(SortedMap<String, Requirement> map, String parent, Requirement entry) {
        Requirement parentRequirement = map.get(parent);
        Requirement updatedParentRequirement = parentRequirement.withChild(entry);
        map.remove(parent);
        map.put(parent, updatedParentRequirement);
    }

    public void write(SortedMap<String, Requirement> map) throws IOException {
        try( FileOutputStream os = new FileOutputStream(new File(outputDirectory, rootDirectory + ".json"))) {

        }
        FileOutputStream os = new FileOutputStream(new File(outputDirectory, rootDirectory + ".json"));

        mapper.writeValue(os, map);
        os.close();
    }
}
