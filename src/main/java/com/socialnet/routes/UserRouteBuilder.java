package com.socialnet.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class UserRouteBuilder extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:register")
                .to("bean:userService?method=register(${header.name}, ${header.city}, ${header.birthDate})")
                .process(exchange -> exchange.getOut().setHeader("mongoId", exchange.getIn().getBody(String.class)))
                .to("direct:insert");

        from("direct:findAll")
                .to("bean:userService?method=findAll()");

        from("direct:findByMongoId")
                .to("bean:userService?method=findByMongoId(${header.id})");

        from("direct:findByCity")
                .to("bean:userService?method=findByCity(${header.city})");

        from("direct:findByNameAndCity")
                .to("bean:userService?method=findByNameAndCity(${header.name}, ${header.city})");

        from("direct:findUsersByNameRegex")
                .to("bean:userService?method=findUsersByNameRegex(${header.name})");

        from("direct:findUsersByBirthDateBetween")
                .to("bean:userService?method=findUsersByBirthDateBetween(${header.ageGT}, ${header.ageLT})");

        from("direct:findUsersByCityAndBirthDateBetween")
                .to("bean:userService?method=findUsersByCityAndBirthDateBetween(${header.city}, ${header.ageGT}, ${header.ageLT})");
    }
}
