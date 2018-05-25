package com.socialnet;

import com.socialnet.pojos.Node;
import com.socialnet.pojos.User;
import com.socialnet.pojos.UserMessage;
import com.socialnet.repositories.NodeRepository;
import com.socialnet.repositories.UserRepository;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Before
    public void setUp() {
        userRepository.deleteAll();
        nodeRepository.deleteAll();
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
    public void findAllShouldContainsUserAfterRegistration() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-2-2";
        given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate).then().statusCode(200);

        Response response = given().port(port).when().get("/findAll");

        response.then().statusCode(200);
        response.then().body("name", contains(name));
        response.then().body("city", contains(city));
        response.then().body("birthDate", contains(birthDate));
    }

    @Test
    public void databaseShouldContainsUserAfterRegistration() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-2-2";
        given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate).then().statusCode(200);

        List<User> users = userRepository.findAll();
        assertThat(users.size(), is(1));
        User user = users.get(0);
        assertThat(user.getName(), is(name));
        assertThat(user.getCity(), is(city));
        assertThat(user.getBirthDate(), is(birthDate));
    }

    @Test
    public void registrationShouldBePossible() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-2-2";
        given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate).then().statusCode(200);
    }

    @Test
    public void findAllShouldContainsUserAfterInsertingOne() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-2-2";
        User userToSave = new User(name, city, birthDate);
        userRepository.save(userToSave);

        User[] users = given().port(port).when().get("/findAll").as(User[].class);
        assertThat(users.length, is(1));
        assertThat(users[0], is(userToSave));
    }

    @Test
    public void findByCityShouldContainsUserAfterInsertingOne() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-2-2";
        User userToSave = new User(name, city, birthDate);
        userRepository.save(userToSave);

        Response response = given().port(port).when().get("/findByCity?city=" + city);

        response.then().statusCode(200);
        User[] users = response.as(User[].class);
        assertThat(users.length, is(1));
        assertThat(users[0], is(userToSave));
    }

    @Test
    public void findByCityShouldContainsOneUserAfterInsertingTwo() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";
        User userToSave = new User(name, city, birthDate);
        String secondName = "Name2";
        String secondCity = "City2";
        String secondBirthDate = "0-2-2";
        User secondUserToSave = new User(secondName, secondCity, secondBirthDate);
        userRepository.save(secondUserToSave);
        userRepository.save(userToSave);

        Response response = given().port(port).when().get("/findByCity?city=" + city);

        response.then().statusCode(200);
        User[] users = response.as(User[].class);
        assertThat(users.length, is(1));
        assertThat(Arrays.asList(users), contains(userToSave));
    }

    @Test
    public void databaseShouldContainsBothUsersAfterInsertingThem() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";
        User userToSave = new User(name, city, birthDate);
        String secondName = "Name2";
        String secondCity = "City2";
        String secondBirthDate = "0-2-2";
        User secondUserToSave = new User(secondName, secondCity, secondBirthDate);
        User[] usersToSave = {userToSave, secondUserToSave};
        userRepository.save(secondUserToSave);
        userRepository.save(userToSave);

        Response response = given().port(port).when().get("/findAll");

        response.then().statusCode(200);
        User[] users = response.as(User[].class);
        assertThat(users.length, is(2));
        assertThat(Arrays.asList(users), containsInAnyOrder(usersToSave));
    }

    @Test
    public void neo4jDatabaseShouldContainsUserAfterRegistration() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Iterable<Node> all = nodeRepository.findAll();

        assertTrue(all.iterator().hasNext());
        assertThat(all.iterator().next().getMongoId(), is(mongoId));
    }

    @Test
    public void neo4jDatabaseShouldContainsInvitation() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().port(port).when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);

        given().port(port).when().get("/" + mongoId + "/invitations").then().body("mongoId", contains(secondMongoId));
    }

    @Test
    public void whenSendingAnotherInvitationToSamePersonNothingShouldHappened() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().port(port).when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().port(port).when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);

        given().port(port).when().get("/" + mongoId + "/invitations").then().body("mongoId", contains(secondMongoId));
    }

    @Test
    public void whenSendingInvitationToYourselfNothingShouldHappened() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        given().port(port).when().post("/" + mongoId + "/invite?inviteeId=" + mongoId);

        given().port(port).when().get("/" + mongoId + "/invitations").then().body("mongoId", not(contains(mongoId)));
    }

    @Test
    public void whenTwoPersonSendInvitationToEachOtherShouldBecomeFriends() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().port(port).when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().port(port).when().post("/" + mongoId + "/invite?inviteeId=" + secondMongoId);

        given().port(port).when().get("/" + mongoId + "/friends").then().body("mongoId", contains(secondMongoId));
        given().port(port).when().get("/" + secondMongoId + "/friends").then().body("mongoId", contains(mongoId));
    }

    @Test
    public void whenTwoPersonSendInvitationToEachOtherAllInvitationShouldBeRemoved() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().port(port).when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().port(port).when().post("/" + mongoId + "/invite?inviteeId=" + secondMongoId);

        given().port(port).when().get("/" + mongoId + "/invitations").then().body("mongoId", not(contains(secondMongoId)));
        given().port(port).when().get("/" + secondMongoId + "/invitations").then().body("mongoId", not(contains(mongoId)));
    }

    @Test
    public void whenInvitationIsAcceptInviterAndInviteeShouldBeFriends() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().port(port).when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().port(port).when().post("/" + mongoId + "/acceptInvitation?inviterId=" + secondMongoId);

        given().port(port).when().get("/" + mongoId + "/friends").then().body("mongoId", contains(secondMongoId));
        given().port(port).when().get("/" + secondMongoId + "/friends").then().body("mongoId", contains(mongoId));
    }

    @Test
    public void whenInvitationIsAcceptInviterAndInviteeAllInvitationShouldBeRemoved() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().port(port).when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().port(port).when().post("/" + mongoId + "/acceptInvitation?inviterId=" + secondMongoId);

        given().port(port).when().get("/" + mongoId + "/invitations").then().body("mongoId", not(contains(secondMongoId)));
        given().port(port).when().get("/" + secondMongoId + "/invitations").then().body("mongoId", not(contains(mongoId)));
    }

    @Test
    public void whenOneAcceptInvitationFromPersonWhoDidNotSendInvitationNothingShouldHappened() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().port(port).when().post("/" + mongoId + "/acceptInvitation?inviterId=" + secondMongoId);

        given().port(port).when().get("/" + mongoId + "/invitations").then().body("mongoId", not(contains(secondMongoId)));
        given().port(port).when().get("/" + secondMongoId + "/invitations").then().body("mongoId", not(contains(mongoId)));
        given().port(port).when().get("/" + mongoId + "/friends").then().body("mongoId", not(contains(secondMongoId)));
        given().port(port).when().get("/" + secondMongoId + "/friends").then().body("mongoId", not(contains(mongoId)));
    }

    @Test
    public void whenOneInviteOnesFriendNotingShouldHappened() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().port(port).when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().port(port).when().post("/" + mongoId + "/acceptInvitation?inviterId=" + secondMongoId);
        given().port(port).when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);

        given().port(port).when().get("/" + mongoId + "/invitations").then().body("mongoId", not(contains(secondMongoId)));
    }

    @Test
    public void whenOneDeclineInvitationOneShouldHaveNotHaveThatInvitation() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().port(port).when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().port(port).when().post("/" + mongoId + "/declineInvitation?inviterId=" + secondMongoId);

        given().port(port).when().get("/" + mongoId + "/invitations").then().body("mongoId", not(contains(secondMongoId)));
    }

    @Test
    public void friendsShouldBePartOfNetwork() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().port(port).when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().port(port).when().post("/" + mongoId + "/invite?inviteeId=" + secondMongoId);

        given().port(port).when().get("/" + mongoId + "/network").then().body("mongoId", contains(secondMongoId));
        given().port(port).when().get("/" + secondMongoId + "/network").then().body("mongoId", contains(mongoId));
    }

    @Test
    public void friendsOfFriendsShouldBePartOfNetwork() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        Response thirdResponse = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String thirdMongoId = thirdResponse.getHeader("mongoId");
        given().port(port).when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().port(port).when().post("/" + mongoId + "/invite?inviteeId=" + secondMongoId);
        given().port(port).when().post("/" + secondMongoId + "/invite?inviteeId=" + thirdMongoId);
        given().port(port).when().post("/" + thirdMongoId + "/invite?inviteeId=" + secondMongoId);

        given().port(port).when().get("/" + mongoId + "/network").then().body("mongoId", contains(secondMongoId, thirdMongoId));
    }
}
