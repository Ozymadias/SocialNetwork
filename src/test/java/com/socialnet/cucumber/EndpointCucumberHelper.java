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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndpointCucumberHelper {
    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    private User[] allUsers;
    private List<User> users;

    @Before
    public void setUp() {
        RestAssured.port = port;
        userRepository.deleteAll();
        users = new ArrayList<>();
    }

    @Given("^user with name ([^\"]*), city ([^\"]*) and birth date (\\d{1,4})-(\\d{1,2})-(\\d{1,2}) in database$")
    public void user_with_name_Name_city_City_and_birth_date_in_database(String name, String city, String birthdateYear, String birtdateMonth, String birthdateDay) {
        String birthDate = birthdateYear + "-" + birtdateMonth + "-" + birthdateDay;
        users.add(new User(name, city, birthDate));
        userRepository.save(users);
    }

    @Given("^user with name ([^\"]*), city ([^\"]*) and birth date (\\d{1,4})-(\\d{1,2})-(\\d{1,2})$")
    public void user_with_name_Name_city_City_and_birth_date(String name, String city, String birthdateYear, String birtdateMonth, String birthdateDay) {
        String birthDate = birthdateYear + "-" + birtdateMonth + "-" + birthdateDay;
        users.add(new User(name, city, birthDate));
    }

    @Given("users")
    public void user(List<Map<String, String>> dataTable) {
        dataTable.forEach(System.out::println);
        for (Map<String, String> map : dataTable) {
            users.add(new User(map.get("name"), map.get("city"), map.get("birthdate")));
        }
    }

    @When("^one calls /([^\\s]*)$")
    public void one_calls_findAll(String endpoint) {
        allUsers = given().when().get("/" + endpoint).as(User[].class);
    }

    @When("^one calls /([^\\s]*) with header city equal to ([^\\s]*)$")
    public void one_calls_findByCity_with_header_city_equal_to_City(String endpoint, String city) {
        allUsers = given().header("city", city).when().get("/" + endpoint).as(User[].class);
    }

    @When("^one registers that user\\(s\\)$")
    public void one_calls_register_that_user() {
        for (User user : users) {
            Response response = given().when().post("/register?name=" + user.getName() + "&city=" + user.getCity() + "&birthDate=" + user.getBirthDate());
            user.setId(response.getHeader("mongoId"));
        }
    }

    @Then("^one should receives in response body that user\\(s\\)$")
    public void one_should_receives_in_response_body_that_users() {
        assertThat(allUsers.length, is(users.size()));
        assertThat(Arrays.asList(allUsers), containsInAnyOrder(users.toArray()));
    }
}
