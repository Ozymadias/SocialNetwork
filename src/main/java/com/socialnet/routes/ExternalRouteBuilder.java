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
                .get("/findByMongoId").to("direct:findByMongoId")
                .get("/findByCity").to("direct:findByCity")
                .get("/findByNameAndCity").to("direct:findByNameAndCity")
                .get("/findUsersByNameRegex").to("direct:findUsersByNameRegex")
                .get("/findUsersByBirthDateBetween").to("direct:findUsersByBirthDateBetween")
                .get("/findUsersByCityAndBirthDateBetween").to("direct:findUsersByCityAndBirthDateBetween")

                .get("/messages").to("direct:allMessages");

        rest("{userId}")
                .post("/postMessage").to("direct:postMessage")
                .get("/friendMessages").to("direct:findFriends")
                .get("/networkMessages").to("direct:findNetwork")

                .post("/invite").to("direct:invite")
                .post("/acceptInvitation").to("direct:acceptInvitation")
                .post("/refuseInvitation").to("direct:refuseInvitation")
                .post("/unfriend").to("direct:unfriend")
                .get("/invitations").to("direct:invitations")
                .get("/friends").to("direct:friends")
                .get("/network").to("direct:network")

                .get("/distance").to("direct:distance")
                .get("/lastRequestResult").to("direct:lastRequestResult");
    }
}
