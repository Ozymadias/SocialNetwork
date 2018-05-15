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

    @Relationship(type = "INVITATION", direction = Relationship.INCOMING)
    private Set<Person> inviters = new HashSet<>();

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
        friends.add(friendship);
    }

    public void removeFriendship(Person person) {
        friends.remove(person);
    }

    public Set<String> getInviters() {
        return inviters.stream().map(Person::getName).collect(Collectors.toSet());
    }

    public void addInviter(Person friendship) {
        inviters.add(friendship);
    }

    public void removeInviter(Person person) {
        inviters.remove(person);
    }

    public boolean hasInvitationFrom(Person person) {
        return inviters.contains(person);
    }

    public boolean hasFriend(Person person) {
        return friends.contains(person);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        if (id != null ? !id.equals(person.id) : person.id != null) return false;
        return name != null ? name.equals(person.name) : person.name == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
