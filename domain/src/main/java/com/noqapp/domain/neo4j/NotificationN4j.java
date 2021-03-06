package com.noqapp.domain.neo4j;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

/**
 * hitender
 * 2/17/21 2:39 PM
 */
@NodeEntity("Notification")
public class NotificationN4j {

    @Id
    private String id;

    @Property("accessCount")
    private int accessCount;

    public String getId() {
        return id;
    }

    public NotificationN4j setId(String id) {
        this.id = id;
        return this;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public NotificationN4j setAccessCount(int accessCount) {
        this.accessCount = accessCount;
        return this;
    }
}
