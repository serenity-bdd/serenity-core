package net.thucydides.model.requirements;

import java.util.List;

public class PathStartsWith {
    private List<String> storyPathElements;

    public PathStartsWith(List<String> storyPathElements) {
        this.storyPathElements = storyPathElements;
    }

    public boolean startsWith(List<String> rootElements) {
        if (storyPathElements.size() >= rootElements.size()) {
            int elementIndex = 0;
            for(String pathElement : rootElements) {
                if (!pathElement.equals(storyPathElements.get(elementIndex++))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
