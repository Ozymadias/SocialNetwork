package com.socialnet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.LocalDate;

@EnableMongoRepositories
@SpringBootApplication
public class Application {// implements CommandLineRunner {

//    @Autowired
//    private UserRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

//    @Override
//    public void run(String... strings) {
//        repository.deleteAll();
//
//        repository.save(new User("Alice Smith", "Krakow", LocalDate.of(1992, 11, 13)));
//        repository.save(new User("Bob Smith", "Krakow", LocalDate.of(1990, 1, 30)));
//
//        System.out.println("findAll()");
//        for (User user : repository.findAll()) {
//            System.out.println(user);
//        }
//
//        System.out.println("findByFirstName(Alice)");
//        System.out.println(repository.findByName("Alice"));
//
//        System.out.println("findByCity(Krakow)");
//        for (User user : repository.findByCity("Krakow")) {
//            System.out.println(user);
//        }
//    }
}
