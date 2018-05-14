package com.socialnet.repository;

import com.socialnet.Person;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface PersonRepository extends Neo4jRepository<Person, Long> {
    Person findByName(String name);

    @Query("MATCH (p1) RETURN p1")
    Collection<Person> getAll();

    //    @Query("MATCH (p1:Person)-[r:IS_FRIEND]->(p2:Person) RETURN p1,r,p2 LIMIT {limit}")
    @Query("MATCH (p1)-[r:IS_FRIEND]->(p2) RETURN p1,r,p2")
    Collection<Person> getPeople();

    @Query("MATCH (p1{name: {firstName}})-[r:IS_FRIEND]-(p2{name: {secondName}}) DELETE r")
    void unfriend(@Param("firstName") String firstName, @Param("secondName") String secondName);
}
