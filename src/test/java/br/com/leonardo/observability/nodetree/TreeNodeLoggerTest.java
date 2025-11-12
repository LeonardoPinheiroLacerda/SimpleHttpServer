package br.com.leonardo.observability.nodetree;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class TreeNodeLoggerTest {

    @Test
    void shouldLogNode() {

        //Given
        Node node = new Node("root");
        Node firstChild = new Node("firstChild");
        Node secondChild = new Node("secondChild");
        Node firstGrandChild = new Node("firstGrandChild");

        node.addChild(firstChild);
        node.addChild(secondChild);
        firstChild.addChild(firstGrandChild);

        TreeNodeLogger treeNodeLogger = new TreeNodeLogger(log);

        //When + Then
        Assertions
                .assertThatNoException()
                .isThrownBy(() -> treeNodeLogger.logTree(node));

    }

}