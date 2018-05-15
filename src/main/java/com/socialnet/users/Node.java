package com.socialnet.users;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NodeEntity
public class Node {
    private Long id;
    private String mongoId;

    @Relationship(type = "IS_FRIEND")
    private Set<Node> friends = new HashSet<>();

    @Relationship(type = "INVITATION", direction = Relationship.INCOMING)
    private Set<Node> inviters = new HashSet<>();

    public Node() {
    }

    public Node(String mongoId) {
        this.mongoId = mongoId;
    }

    public Long getId() {
        return id;
    }

    public String getMongoId() {
        return mongoId;
    }

    //    @JsonIgnore
    public Set<String> getFriends() {
        return friends.stream().map(Node::getMongoId).collect(Collectors.toSet());
    }

    public void addFriendship(Node friendship) {
        friends.add(friendship);
    }

    public void removeFriendship(Node node) {
        friends.remove(node);
    }

    public Set<String> getInviters() {
        return inviters.stream().map(Node::getMongoId).collect(Collectors.toSet());
    }

    public void addInviter(Node friendship) {
        inviters.add(friendship);
    }

    public void removeInviter(Node node) {
        inviters.remove(node);
    }

    public boolean hasInvitationFrom(Node node) {
        return inviters.contains(node);
    }

    public boolean hasFriend(Node node) {
        return friends.contains(node);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        Node node = (Node) o;

        if (id != null ? !id.equals(node.id) : node.id != null) return false;
        return mongoId != null ? mongoId.equals(node.mongoId) : node.mongoId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (mongoId != null ? mongoId.hashCode() : 0);
        return result;
    }
}
