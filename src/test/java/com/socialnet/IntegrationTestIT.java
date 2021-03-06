package com.socialnet;

import com.socialnet.pojos.Message;
import com.socialnet.pojos.Node;
import com.socialnet.pojos.User;
import com.socialnet.repositories.NodeRepository;
import com.socialnet.repositories.UserRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@DirtiesContext
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTestIT {
    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NodeRepository nodeRepository;
    private Response response;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        userRepository.deleteAll();
        nodeRepository.deleteAll();
    }

    @Test
    public void databaseShouldBeEmptyAtTheBeginning() {
        response = given().when().get("/findAll");

        response.then().statusCode(200);
        User[] users = given().when().get("/findAll").as(User[].class);
        assertThat(users.length, is(0));
    }

    @Test
    public void findAllShouldContainsUserAfterRegistration() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-2-2";
        given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate).then().statusCode(200);

        Response response = given().when().get("/findAll");

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
        given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate).then().statusCode(200);

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
        given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate).then().statusCode(200);
    }

    @Test
    public void findAllShouldContainsUserAfterInsertingOne() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-2-2";
        User userToSave = new User(name, city, birthDate);
        userRepository.save(userToSave);

        User[] users = given().when().get("/findAll").as(User[].class);
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

        Response response = given().when().get("/findByCity?city=" + city);

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

        Response response = given().when().get("/findByCity?city=" + city);

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

        Response response = given().when().get("/findAll");

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
        Response response = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
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

        Response response = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);

        given().when().get("/" + mongoId + "/invitations").then().body("mongoId", contains(secondMongoId));
    }

    @Test
    public void whenSendingAnotherInvitationToSamePersonNothingShouldHappened() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);

        given().when().get("/" + mongoId + "/invitations").then().body("mongoId", contains(secondMongoId));
    }

    @Test
    public void whenSendingInvitationToYourselfNothingShouldHappened() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        given().when().post("/" + mongoId + "/invite?inviteeId=" + mongoId);

        given().when().get("/" + mongoId + "/invitations").then().body("mongoId", not(contains(mongoId)));
    }

    @Test
    public void whenTwoPersonSendInvitationToEachOtherShouldBecomeFriends() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().when().post("/" + mongoId + "/invite?inviteeId=" + secondMongoId);

        given().when().get("/" + mongoId + "/friends").then().body("mongoId", contains(secondMongoId));
        given().when().get("/" + secondMongoId + "/friends").then().body("mongoId", contains(mongoId));
    }

    @Test
    public void whenTwoPersonSendInvitationToEachOtherAllInvitationShouldBeRemoved() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().when().post("/" + mongoId + "/invite?inviteeId=" + secondMongoId);

        given().when().get("/" + mongoId + "/invitations").then().body("mongoId", not(contains(secondMongoId)));
        given().when().get("/" + secondMongoId + "/invitations").then().body("mongoId", not(contains(mongoId)));
    }

    @Test
    public void whenInvitationIsAcceptInviterAndInviteeShouldBeFriends() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().when().post("/" + mongoId + "/acceptInvitation?inviterId=" + secondMongoId);

        given().when().get("/" + mongoId + "/friends").then().body("mongoId", contains(secondMongoId));
        given().when().get("/" + secondMongoId + "/friends").then().body("mongoId", contains(mongoId));
    }

    @Test
    public void whenInvitationIsAcceptInviterAndInviteeAllInvitationShouldBeRemoved() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().when().post("/" + mongoId + "/acceptInvitation?inviterId=" + secondMongoId);

        given().when().get("/" + mongoId + "/invitations").then().body("mongoId", not(contains(secondMongoId)));
        given().when().get("/" + secondMongoId + "/invitations").then().body("mongoId", not(contains(mongoId)));
    }

    @Test
    public void whenOneAcceptInvitationFromPersonWhoDidNotSendInvitationNothingShouldHappened() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().when().post("/" + mongoId + "/acceptInvitation?inviterId=" + secondMongoId);

        given().when().get("/" + mongoId + "/invitations").then().body("mongoId", not(contains(secondMongoId)));
        given().when().get("/" + secondMongoId + "/invitations").then().body("mongoId", not(contains(mongoId)));
        given().when().get("/" + mongoId + "/friends").then().body("mongoId", not(contains(secondMongoId)));
        given().when().get("/" + secondMongoId + "/friends").then().body("mongoId", not(contains(mongoId)));
    }

    @Test
    public void whenOneInviteOnesFriendNotingShouldHappened() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().when().post("/" + mongoId + "/acceptInvitation?inviterId=" + secondMongoId);
        given().when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);

        given().when().get("/" + mongoId + "/invitations").then().body("mongoId", not(contains(secondMongoId)));
    }

    @Test
    public void whenOneDeclineInvitationOneShouldHaveNotHaveThatInvitation() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().when().post("/" + mongoId + "/declineInvitation?inviterId=" + secondMongoId);

        given().when().get("/" + mongoId + "/invitations").then().body("mongoId", not(contains(secondMongoId)));
    }

    @Test
    public void friendsShouldBePartOfNetwork() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        given().when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().when().post("/" + mongoId + "/invite?inviteeId=" + secondMongoId);

        given().when().get("/" + mongoId + "/network").then().body("mongoId", contains(secondMongoId));
        given().when().get("/" + secondMongoId + "/network").then().body("mongoId", contains(mongoId));
    }

    @Test
    public void friendsOfFriendsShouldBePartOfNetwork() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        Response secondResponse = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String secondMongoId = secondResponse.getHeader("mongoId");
        Response thirdResponse = given().when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String thirdMongoId = thirdResponse.getHeader("mongoId");
        given().when().post("/" + secondMongoId + "/invite?inviteeId=" + mongoId);
        given().when().post("/" + mongoId + "/invite?inviteeId=" + secondMongoId);
        given().when().post("/" + secondMongoId + "/invite?inviteeId=" + thirdMongoId);
        given().when().post("/" + thirdMongoId + "/invite?inviteeId=" + secondMongoId);

        given().when().get("/" + mongoId + "/network").then().body("mongoId", contains(secondMongoId, thirdMongoId));
    }

    @Test
    public void postedMessageShouldBeSavedInDatabase() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        String messageContent = "Message Content";
        given().port(port).body("\"" + messageContent + "\"").when().post("/" + mongoId + "/postMessage");

        User user = userRepository.findById(mongoId);
        List<Message> messages = user.getMessages();
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0).getContent(), is(messageContent));
    }


    @Test
    public void postedMessageShouldBeSavedInDatabase2() {
        String name = "Name";
        String city = "City";
        String birthDate = "0-1-1";

        Response response = given().port(port).when().post("/register?name=" + name + "&city=" + city + "&birthDate=" + birthDate);
        String mongoId = response.getHeader("mongoId");
        String messageContent = "Message Content";


        String url = "/" + mongoId + "/postMessage";
        String body = "\"" + messageContent + "\"";
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body), String.class);

        System.out.println(responseEntity);

        User user = userRepository.findById(mongoId);
        List<Message> messages = user.getMessages();
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0).getContent(), is(messageContent));
    }
}
