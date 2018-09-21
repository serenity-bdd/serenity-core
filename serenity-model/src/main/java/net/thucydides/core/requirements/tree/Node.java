package net.thucydides.core.requirements.tree;

import java.util.Arrays;
import java.util.List;

public class Node {

    private final String text;
    private final String href;
    private final List<Node> nodes;
    private final boolean selectable = false;
    private final List<String> tags;


    public Node(String text, String href, String result, List<Node> nodes) {
        this.text = text;
        this.href = href;
        this.nodes = nodes;
        this.tags = Arrays.asList(result);
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

}
