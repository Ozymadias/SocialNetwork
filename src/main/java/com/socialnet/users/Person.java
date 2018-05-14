package com.socialnet.users;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NodeEntity
public class Person {
    private Long id;
    private String name;

    @Relationship(type = "IS_FRIEND")
    private Set<Person> friends = new HashSet<>();

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    //    @JsonIgnore
    public Set<String> getFriends() {
        return friends.stream().map(Person::getName).collect(Collectors.toSet());
    }

    public void addFriendship(Person friendship) {
        this.friends.add(friendship);
    }

    public void removeFriendship(Person person) {
        this.friends.remove(person);
    }
}