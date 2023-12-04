package com.nhnacademy.node;

import lombok.extern.slf4j.Slf4j;

/**
 * @field count 생성된 노드 개수
 * @field id 각 노드의 id
 */
@Slf4j
public abstract class Node {

    static int count;
    String id;
    String name;

    Node() {
        count++;
        id = String.format("%s-%02d", getClass().getSimpleName(), count);
        name = id;
        log.trace("create " + getClass().getName() + " : {}", id);
    }

    Node(String id) {
        count++;
        this.id = id;
        name = id;
        log.trace("create " + getClass().getName() + " : {}", id);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static int getCount() {
        return count;
    }
}
