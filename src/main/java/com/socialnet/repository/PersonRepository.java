package com.socialnet.repository;

import com.socialnet.users.Person;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface PersonRepository extends Neo4jRepository<Person, Long> {
    Person findByName(String name);

    @Query("MATCH (p1{name: {firstName}})-[r:IS_FRIEND]-(p2{name: {secondName}}) DELETE r")
    void unfriend(@Param("firstName") String firstName, @Param("secondName") String secondName);

    @Query("MATCH (p1)-[r:INVITATION]->(p2{name: {userId}}) RETURN p1")
    Collection<Person> invitations(@Param("userId") String userId);

    @Query("MATCH (p1)-[r:IS_FRIEND]-(p2{name: {userId}}) RETURN p1")
    Collection<Person> friends(@Param("userId") String userId);

    @Query("MATCH (p1{name: {userId}})-[r:INVITATION]->(p2{name: {inviteeId}}) DELETE r")
    void refuseInvitation(@Param("userId") String userId, @Param("inviteeId") String inviteeId);

    @Query("MATCH (p1)-[r:IS_FRIEND*]-(p2{name: {userId}}) RETURN p1")
    Collection<Person> network(@Param("userId") String userId);
}
