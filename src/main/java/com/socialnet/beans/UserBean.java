package com.socialnet.beans;

import com.socialnet.repository.UserRepository;
import com.socialnet.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class UserBean {
    @Autowired
    UserRepository userRepository;

    public String register(String name, String city, String birthDate) {
        User user = new User(name, city, birthDate);
        userRepository.save(user);
        return user.getId();
    }

    public Object[] neo() {
        return userRepository.findAll().stream().map(User::getId).toArray();
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByMongoId(String id) {
        return userRepository.findById(id);
    }

    public List<User> findByCity(String city) {
        return userRepository.findByCity(city);
    }

    public List<User> findByNameAndCity(String name, String city) {
        return userRepository.findByNameAndCity(name, city);
    }

    public List<User> findUsersByNameRegex(String name) {
        return userRepository.findUsersByNameRegex(name);
    }

    public List<User> findUsersByBirthDateBetween(String ageGT, String ageLT) {
        String dateLT = LocalDate.now().minusYears(Integer.valueOf(ageGT)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String dateGT = LocalDate.now().minusYears(Integer.valueOf(ageLT)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return userRepository.findUsersByBirthDateBetween(dateGT, dateLT);
    }


    public List<User> findUsersByCityAndBirthDateBetween(String city, String ageGT, String ageLT) {
        String dateLT = LocalDate.now().minusYears(Integer.valueOf(ageGT)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String dateGT = LocalDate.now().minusYears(Integer.valueOf(ageLT)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return userRepository.findUsersByCityAndBirthDateBetween(city, dateGT, dateLT);
    }
}
