package com.socialnet.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class UserRouteBuilder extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:register")
                .to("bean:userBean?method=register(${header.name}, ${header.city}, ${header.birthDate})")
                .process(exchange -> exchange.getOut().setHeader("mongoId", exchange.getIn().getBody(String.class)))
                .to("direct:insert");

        from("direct:neo").to("bean:userBean?method=neo()").to("direct:ins");

        from("direct:findAll")
                .to("bean:userBean?method=findAll()");

        from("direct:findByMongoId")
                .to("bean:userBean?method=findByMongoId(${header.id})");

        from("direct:findByCity")
                .to("bean:userBean?method=findByCity(${header.city})");

        from("direct:findByNameAndCity")
                .to("bean:userBean?method=findByNameAndCity(${header.name}, ${header.city})");

        from("direct:findUsersByNameRegex")
                .to("bean:userBean?method=findUsersByNameRegex(${header.name})");

        from("direct:findUsersByBirthDateBetween")
                .to("bean:userBean?method=findUsersByBirthDateBetween(${header.ageGT}, ${header.ageLT})");

        from("direct:findUsersByCityAndBirthDateBetween")
                .to("bean:userBean?method=findUsersByCityAndBirthDateBetween(${header.city}, ${header.ageGT}, ${header.ageLT})");
    }
}
