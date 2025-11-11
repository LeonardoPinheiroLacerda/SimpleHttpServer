package br.com.leonardo.observability.nodetree;

import org.slf4j.Logger;

import java.util.List;

public record TreeNodeLogger(Logger logger) {

    public void logTree(Node node) {
        this.logTree(node, "", true);
    }

    private void logTree(Node node, String prefix, boolean isLast) {
        logger.info("{}{}{}", prefix, (isLast ? "└── " : "├── "), node.getLabel());
        List<Node> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
            boolean last = i == children.size() - 1;
            logTree(children.get(i), prefix + (isLast ? "    " : "│   "), last);
        }
    }

}
