package com.noqapp.domain.neo4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

/**
 * hitender
 * 1/19/21 12:50 AM
 */
@NodeEntity("Property")
public class PropertyN4j {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyN4j.class);

    @Id @GeneratedValue
    private Long id;

    @Property("name")
    private String fullName;

    @Property("age")
    private int age;

    public Long getId() {
        return id;
    }

    public PropertyN4j setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public PropertyN4j setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public int getAge() {
        return age;
    }

    public PropertyN4j setAge(int age) {
        this.age = age;
        return this;
    }
}
