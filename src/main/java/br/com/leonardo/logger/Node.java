package br.com.leonardo.logger;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Node {

    private String label;
    private List<Node> children;

    public Node(String label) {
        this.label = label;
        this.children = new ArrayList<>();
    }

    public void addChild(Node child) {
        this.children.add(child);
    }
}
