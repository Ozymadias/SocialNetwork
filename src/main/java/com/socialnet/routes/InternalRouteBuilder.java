package com.socialnet.routes;

import com.mongodb.DBObject;
import com.socialnet.repository.PersonRepository;
import com.socialnet.repository.UserRepository;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class InternalRouteBuilder extends RouteBuilder {
    @Autowired
    UserRepository repository;

    @Override
    public void configure() {
        from("direct:register").convertBodyTo(DBObject.class)
                .to("mongodb:mongo?database=test&collection=user&operation=insert");

        from("direct:findAll").process(exchange -> {
            exchange.getOut().setBody(repository.findAll());
        });

        from("direct:findByName").process(exchange -> {
            String name = (String) exchange.getIn().getHeaders().get("name");
            exchange.getOut().setBody(repository.findByName(name));
        });

        from("direct:findByCity").process(exchange -> {
            String city = (String) exchange.getIn().getHeaders().get("city");
            exchange.getOut().setBody(repository.findByCity(city));
        });

        from("direct:findByNameAndCity").process(exchange -> {
            String name = (String) exchange.getIn().getHeaders().get("name");
            String city = (String) exchange.getIn().getHeaders().get("city");
            exchange.getOut().setBody(repository.findByNameAndCity(name, city));
        });

        from("direct:findUsersByNameRegex").process(exchange -> {
            String name = (String) exchange.getIn().getHeaders().get("name");
            exchange.getOut().setBody(repository.findUsersByNameRegex(name));
        });

        from("direct:findUsersByBirthDateBetween").process(exchange -> {
            int ageGT = Integer.valueOf((String) exchange.getIn().getHeaders().get("ageGT"));
            int ageLT = Integer.valueOf((String) exchange.getIn().getHeaders().get("ageLT"));
            String dateLT = LocalDate.now().minusYears(ageGT).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String dateGT = LocalDate.now().minusYears(ageLT).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            exchange.getOut().setBody(repository.findUsersByBirthDateBetween(dateGT, dateLT));
        });

        from("direct:findUsersByCityAndBirthDateBetween").process(exchange -> {
            String city = (String) exchange.getIn().getHeaders().get("city");
            int ageGT = Integer.valueOf((String) exchange.getIn().getHeaders().get("ageGT"));
            int ageLT = Integer.valueOf((String) exchange.getIn().getHeaders().get("ageLT"));
            String dateLT = LocalDate.now().minusYears(ageGT).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String dateGT = LocalDate.now().minusYears(ageLT).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            exchange.getOut().setBody(repository.findUsersByCityAndBirthDateBetween(city, dateGT, dateLT));
        });
    }
}
