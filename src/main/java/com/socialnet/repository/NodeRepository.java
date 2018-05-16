package com.socialnet.repository;

import com.socialnet.users.Node;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface NodeRepository extends Neo4jRepository<Node, Long> {
    Node findByMongoId(String name);

    @Query("MATCH (p1{mongoId: {firstId}})-[r:IS_FRIEND]-(p2{mongoId: {secondId}}) DELETE r")
    void unfriend(@Param("firstId") String firstId, @Param("secondId") String secondId);

    @Query("MATCH (p1)-[r:INVITATION]->(p2{mongoId: {userId}}) RETURN p1")
    Collection<Node> invitations(@Param("userId") String userId);

    @Query("MATCH (p1)-[r:IS_FRIEND]-(p2{mongoId: {userId}}) RETURN p1")
    Collection<Node> friends(@Param("userId") String userId);

    @Query("MATCH (p1{mongoId: {userId}})-[r:INVITATION]-(p2{mongoId: {inviteeId}}) DELETE r")
    void refuseInvitation(@Param("userId") String userId, @Param("inviteeId") String inviteeId);

    @Query("MATCH (p1)-[r:IS_FRIEND*]-(p2{mongoId: {userId}}) RETURN p1")
    Collection<Node> network(@Param("userId") String userId);

    @Query("MATCH (a { mongoId: {userId}}),(b { mongoId: {otherId}}), p = shortestPath((a)-[*]-(b)) WHERE NONE (r IN relationships(p) WHERE type(r)= 'INVITATION') RETURN length(p)")
    Integer distanceFactor(@Param("userId") String userId, @Param("otherId") String otherId);
}
