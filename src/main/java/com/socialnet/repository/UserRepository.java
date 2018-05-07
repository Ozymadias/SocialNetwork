package com.socialnet.repository;

import com.socialnet.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByCity(String city);

    List<User> findByName(String name);

    List<User> findByNameAndCity(String name, String city);

    List<User> findUsersByNameRegex(String regexp);

    List<User> findUsersByBirthDateBetween(String dateGT, String dateLT);

    List<User> findUsersByCityAndBirthDateBetween(String city, String dateGT, String dateLT);
}
