package com.socialnet.repositories;

import com.socialnet.pojos.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UserRepositoryTest {
    @Autowired
    UserRepository repository;

    @Before
    public void setUp() {
        repository.save(new User("Any Name", "Any City", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
    }

    @Test
    public void test() {
        //given
        User user = new User("Name Surname", "Krakow", LocalDate.of(1999, 9, 13).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        //when
        repository.save(user);

        //then
        List<User> result = repository.findByCity("Krakow");
        assertTrue(result.size() == 1);
        assertTrue(result.contains(user));
    }
}
