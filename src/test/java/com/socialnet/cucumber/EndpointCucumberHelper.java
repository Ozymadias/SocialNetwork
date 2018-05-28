package com.socialnet.cucumber;

import com.socialnet.pojos.User;
import com.socialnet.repositories.UserRepository;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndpointCucumberHelper {
    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User[] users;

    @Before
    public void setUp() {
        RestAssured.port = port;
        userRepository.deleteAll();
    }

    @Given("^user with name ([^\"]*), city ([^\"]*) and birth date (\\d+)-(\\d+)-(\\d+) in database$")
    public void user_with_name_Name_city_City_and_birth_date_in_database(String name, String city, int birthdateYear, int birtdateMonth, int birthdateDay) {
        String birthDate = birthdateYear + "-" + birtdateMonth + "-" + birthdateDay;
        user = new User(name, city, birthDate);
        userRepository.save(user);
    }

    @Given("^user with name ([^\"]*), city ([^\"]*) and birth date (\\d+)-(\\d+)-(\\d+)$")
    public void user_with_name_Name_city_City_and_birth_date(String name, String city, int birthdateYear, int birtdateMonth, int birthdateDay) {
        String birthDate = birthdateYear + "-" + birtdateMonth + "-" + birthdateDay;
        user = new User(name, city, birthDate);
    }

    @When("^one calls \\/([^\\s]*)$")
    public void one_calls_findAll(String endpoint) {
        users = given().when().get("/" + endpoint).as(User[].class);
    }

    @When("^one calls \\/([^\\s]*) with header city equal to ([^\\s]*)$")
    public void one_calls_findByCity_with_header_city_equal_to_City(String endpoint, String city) {
        users = given().header("city", city).when().get("/" + endpoint).as(User[].class);
    }

    @When("^one calls \\/register that user$")
    public void one_calls_register_that_user() {
        Response response = given().when().post("/register?name=" + user.getName() + "&city=" + user.getCity() + "&birthDate=" + user.getBirthDate());
        user.setId(response.getHeader("mongoId"));
    }

    @Then("^one should receives in response body that user$")
    public void one_should_receives_in_response_body_that_user() {
        assertThat(users.length, is(1));
        assertThat(users[0], is(user));
    }
}
