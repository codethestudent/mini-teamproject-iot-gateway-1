package com.nhnacademy.node;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Node {

    // 현재 생성된 노드의 개수
    static int count;
    // 생성된 노드의 id
    String id;

    Node() {
        // 노드 생성시 count 증가, id 부여
        count++;
        id = String.format("%s-%02d", getClass().getSimpleName(), count);
        log.trace("create node : {}", id);
    }

    // ID getter
    public String getId() {
        return id;
    }

    // Name getter ,setter
    public abstract String getName();

    public abstract void setName(String name);

    // Count getter
    public static int getCount() {
        return count;
    }
}
