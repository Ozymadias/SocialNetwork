package com.socialnet.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class ExternalRouteBuilder extends RouteBuilder {
    @Override
    public void configure() {
        restConfiguration()
                .component("servlet")
                .host("0.0.0.0").port("8085")
                .bindingMode(RestBindingMode.json)
                .apiContextPath("/api-doc");

        rest().produces("application/json")
                .post("/register").to("direct:register")
                .get("/findAll").to("direct:findAll")
                .get("/findByName").to("direct:findByName")
                .get("/findByCity").to("direct:findByCity")
                .get("/findByNameAndCity").to("direct:findByNameAndCity")
                .get("/findUsersByNameRegex").to("direct:findUsersByNameRegex")
                .get("/findUsersByBirthDateBetween").to("direct:findUsersByBirthDateBetween")
                .get("/findUsersByCityAndBirthDateBetween").to("direct:findUsersByCityAndBirthDateBetween")

                .post("/insert").to("direct:insert")
                .post("/friend").to("direct:friend")
                .get("/people").to("direct:people")
                .get("/allPeopleWithFriends").to("direct:allPeopleWithFriends")

                .post("{userId}/invite").to("direct:invite")
                .post("{userId}/acceptInvitation").to("direct:acceptInvitation")
                .post("{userId}/unfriend").to("direct:unfriend")
                .get("{userId}/invitations").to("direct:invitations")
                .get("{userId}/friends").to("direct:friends");
    }
}
