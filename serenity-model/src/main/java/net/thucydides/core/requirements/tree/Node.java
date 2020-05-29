package net.thucydides.core.requirements.tree;

import com.google.common.base.Objects;

import java.util.Arrays;
import java.util.List;

public class Node implements Comparable<Node> {

    private final String text;
    private String type;
    private final String href;
    private final List<Node> nodes;
    private final boolean selectable = false;
    private final List<String> tags;


    public Node(String text, String type, String href, String result, String childCount, List<Node> nodes) {
        this.text = text;
        this.type = type;
        this.href = href;
        this.nodes = nodes;
        this.tags = Arrays.asList(result, childCount);
    }

    public String getText() {
        return text;
    }

    public String getHref() {
        return href;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equal(text, node.text) &&
                Objects.equal(href, node.href);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(text, href);
    }


    @Override
    public int compareTo(Node otherNode) {
        return this.text.compareTo(otherNode.text);
    }
}
