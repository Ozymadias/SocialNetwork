package com.socialnet.routes;

import com.socialnet.repository.UserRepository;
import com.socialnet.users.Message;
import com.socialnet.users.User;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class InternalRouteBuilder extends RouteBuilder {
    @Autowired
    UserRepository repository;

    @Override
    public void configure() {
//        from("direct:register").convertBodyTo(DBObject.class)
//                .to("mongodb:mongo?database=test&collection=user&operation=insert")
        from("direct:register").process(exchange -> {
            Map<String, Object> headers = exchange.getIn().getHeaders();
            User user = new User((String) headers.get("name"), (String) headers.get("city"), (String) headers.get("birthDate"));
            repository.save(user);
            exchange.getOut().setHeader("neo4jId", user.getId());
        }).to("direct:insert");

        from("direct:findAll").process(exchange -> {
            exchange.getOut().setBody(repository.findAll());
        });

        from("direct:findByName").process(exchange -> {
            String name = (String) exchange.getIn().getHeader("name");
            exchange.getOut().setBody(repository.findByName(name));
        });

        from("direct:findByCity").process(exchange -> {
            String city = (String) exchange.getIn().getHeader("city");
            exchange.getOut().setBody(repository.findByCity(city));
        });

        from("direct:findByNameAndCity").process(exchange -> {
            String name = (String) exchange.getIn().getHeader("name");
            String city = (String) exchange.getIn().getHeader("city");
            exchange.getOut().setBody(repository.findByNameAndCity(name, city));
        });

        from("direct:findUsersByNameRegex").process(exchange -> {
            String name = (String) exchange.getIn().getHeader("name");
            exchange.getOut().setBody(repository.findUsersByNameRegex(name));
        });

        from("direct:findUsersByBirthDateBetween").process(exchange -> {
            int ageGT = Integer.valueOf((String) exchange.getIn().getHeader("ageGT"));
            int ageLT = Integer.valueOf((String) exchange.getIn().getHeader("ageLT"));
            String dateLT = LocalDate.now().minusYears(ageGT).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String dateGT = LocalDate.now().minusYears(ageLT).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            exchange.getOut().setBody(repository.findUsersByBirthDateBetween(dateGT, dateLT));
        });

        from("direct:findUsersByCityAndBirthDateBetween").process(exchange -> {
            String city = (String) exchange.getIn().getHeader("city");
            int ageGT = Integer.valueOf((String) exchange.getIn().getHeader("ageGT"));
            int ageLT = Integer.valueOf((String) exchange.getIn().getHeader("ageLT"));
            String dateLT = LocalDate.now().minusYears(ageGT).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String dateGT = LocalDate.now().minusYears(ageLT).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            exchange.getOut().setBody(repository.findUsersByCityAndBirthDateBetween(city, dateGT, dateLT));
        });

        from("direct:postMessage").process(exchange -> {
            User userId = repository.findById((String) exchange.getIn().getHeader("userId"));
            userId.addMessage(new Message((String) exchange.getIn().getHeader("message"), System.currentTimeMillis()));
            repository.save(userId);
        });
    }
}
