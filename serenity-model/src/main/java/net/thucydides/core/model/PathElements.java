package net.thucydides.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

public class PathElements extends ArrayList<PathElement> {

    public static PathElements from(List<PathElement> elements) {
        PathElements pathElements = new PathElements();
        pathElements.addAll(elements);
        return pathElements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PathElements)) return false;

        PathElements that = (PathElements) o;

        return this.toString().equals(that.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder path = new StringBuilder();
        for (PathElement pathElement : this) {
            path.append(pathElement.getName()).append("/");
        }
        if (!path.toString().isEmpty()) {
            path.delete(path.length() - 1, path.length());
        }
        return path.toString();

    }
    public PathElements copy() {
        return PathElements.from(this);
    }


    public PathElements getParent() {
        if (isEmpty()) {
            return null;
        }
        return PathElements.from(subList(0, size() - 1));
    }

    public String asPath() {
        return stream().map(PathElement::getName).collect(joining("/"));
    }

    public PathElements stripFirstElements(List<String> rootElements) {
        if (size() < rootElements.size()) {
            return this;
        }
        for (int index = 0; index < rootElements.size(); index++) {
            if (!get(index).getName().equals(rootElements.get(index))) {
                return this;
            }
        }
        return PathElements.from(subList(rootElements.size(), size()));
    }
}
