package net.thucydides.core.requirements;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.thucydides.core.requirements.model.Requirement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;

public class RequirementPersister {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File outputDirectory;
    private final String rootDirectory;

    public RequirementPersister(File outputDirectory, String rootDirectory) {
        this.outputDirectory = outputDirectory;
        this.rootDirectory = rootDirectory;
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
        FileOutputStream os = new FileOutputStream(new File(outputDirectory, rootDirectory + ".json"));
        mapper.writeValue(os, map);
        os.close();
    }
}
