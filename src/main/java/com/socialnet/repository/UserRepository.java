package com.socialnet.repository;

import com.socialnet.users.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findById(String id);

    List<User> findByName(String name);

    List<User> findByCity(String city);

    List<User> findByNameAndCity(String name, String city);

    List<User> findUsersByNameRegex(String regexp);

    List<User> findUsersByBirthDateBetween(String dateGT, String dateLT);

    List<User> findUsersByCityAndBirthDateBetween(String city, String dateGT, String dateLT);
}
