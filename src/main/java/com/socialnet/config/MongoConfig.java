package com.socialnet.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import java.util.ArrayList;
import java.util.List;

@Configuration
//@EnableMongoRepositories(basePackages = "com.socialnet.repository")
public class MongoConfig extends AbstractMongoConfiguration {
    @Override
    protected String getDatabaseName() {
        return "test";
    }

    @Bean
    @Override
    public Mongo mongo() {
        List<ServerAddress> seeds = new ArrayList<>();
        seeds.add(new ServerAddress("172.30.137.92", 27017));
        List<MongoCredential> credentials = new ArrayList<>();
        credentials.add(MongoCredential.createMongoCRCredential("Dominik", "test", "password".toCharArray()));
//        return new MongoClient(seeds, credentials);
        return new MongoClient("localhost", 27017);
    }
}
