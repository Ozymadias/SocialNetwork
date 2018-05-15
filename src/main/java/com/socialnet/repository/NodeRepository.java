package com.socialnet.repository;

import com.socialnet.users.Node;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface NodeRepository extends Neo4jRepository<Node, Long> {
    Node findByMongoId(String name);

    @Query("MATCH (p1{name: {firstName}})-[r:IS_FRIEND]-(p2{name: {secondName}}) DELETE r")
    void unfriend(@Param("firstName") String firstName, @Param("secondName") String secondName);

    @Query("MATCH (p1)-[r:INVITATION]->(p2{name: {userId}}) RETURN p1")
    Collection<Node> invitations(@Param("userId") String userId);

    @Query("MATCH (p1)-[r:IS_FRIEND]-(p2{name: {userId}}) RETURN p1")
    Collection<Node> friends(@Param("userId") String userId);

    @Query("MATCH (p1{name: {userId}})-[r:INVITATION]->(p2{name: {inviteeId}}) DELETE r")
    void refuseInvitation(@Param("userId") String userId, @Param("inviteeId") String inviteeId);

    @Query("MATCH (p1)-[r:IS_FRIEND*]-(p2{name: {userId}}) RETURN p1")
    Collection<Node> network(@Param("userId") String userId);
}
