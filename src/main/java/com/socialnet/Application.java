package com.socialnet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@EnableMongoRepositories
@EnableNeo4jRepositories("com.socialnet.repository")
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
