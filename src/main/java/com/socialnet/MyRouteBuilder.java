package com.socialnet;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MyRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        restConfiguration()
                .component("servlet")
                .host("0.0.0.0").port("8080")
                .bindingMode(RestBindingMode.json);

//        rest().produces("application/json")
////                .get("/register").to("direct:register")
//                .get("/showAll").route().to("direct:show");
//
////        from("servlet:/localhost:8080/show").to("direct:show");
//
//        from("direct:register")
////                .process(exchange -> {exchange.getIn().getBody(User.class);});
//                .to("mongodb:mongo?database=test&collection=user&operation=insert");
//
//        from("direct:show")
////                .to("mongodb:mongo?database=test&collection=user&operation=findAll");
//                .transform().constant("Hello");

        rest().produces("application/json").get("/hello").route()
                .to("direct:sth");

        from("direct:sth")
                .transform().constant("Hello");
    }
}
