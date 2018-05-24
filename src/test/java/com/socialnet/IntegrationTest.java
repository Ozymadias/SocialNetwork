package com.socialnet;

import com.socialnet.pojos.User;
import com.socialnet.repositories.UserRepository;
import com.socialnet.routes.*;
import io.restassured.RestAssured;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.BeforeTest;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest extends CamelTestSupport {
    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @BeforeTest
    public void setUp() {
//        RestAssured.baseURI = "http://localhost";
        userRepository.deleteAll();
    }

    @Test
    public void databaseShouldBeEmptyAtTheBeginning() {
        given().port(port).when()
                .get("/findAll")
                .then()
                .statusCode(200);

        User[] users = given().port(port).when().get("/findAll").as(User[].class);
        assertThat(users.length, is(0));
    }

    @Test
    public void databaseShouldContainsUserAfterRegistration() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-2-2";
        given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate).then().statusCode(200);

        given().port(port).when().get("/findAll").then().statusCode(200);
        User[] users = given().port(port).when().get("/findAll").as(User[].class);
        assertThat(users.length, is(1));
        User user = users[0];
        assertThat(user, is(name));
        System.out.println(user.getId());
    }

    @Test
    public void databaseShouldContainsUserAfterInsertingOne() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-2-2";
        User userToSave = new User(name, city, birthDate);
        userRepository.save(userToSave);

        given().port(port).when().get("/findAll").then().statusCode(200);
        User[] users = given().port(port).when().get("/findAll").as(User[].class);
        assertThat(users.length, is(1));
        assertThat(users[0], is(userToSave));
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new ExternalRouteBuilder();
    }
}
