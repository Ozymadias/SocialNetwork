package com.socialnet.routes;

import com.socialnet.repository.PersonRepository;
import com.socialnet.users.Person;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class NeoRouteBuilder extends RouteBuilder {
    @Autowired
    PersonRepository personRepository;

    @Override
    public void configure() {
        from("direct:insert").process(exchange -> {
            personRepository.save(new Person((String) exchange.getIn().getHeaders().get("name")));
        });

        from("direct:friend").process(exchange -> {
            Map<String, Object> headers = exchange.getIn().getHeaders();
            Person firstPerson = personRepository.findByName((String) headers.get("firstName"));
            Person secondPerson = personRepository.findByName((String) headers.get("secondName"));
            firstPerson.addFriendship(secondPerson);
            personRepository.save(firstPerson);
        });

        from("direct:unfriend").process(exchange -> {
            Map<String, Object> headers = exchange.getIn().getHeaders();
            personRepository.unfriend((String) headers.get("firstName"), (String) headers.get("secondName"));
        });

        from("direct:allPeopleWithFriends").process(exchange -> exchange.getOut().setBody(personRepository.getPeople()));

        from("direct:people").process(exchange -> exchange.getOut().setBody(personRepository.getAll()));

        from("direct:invite").process(exchange -> {
            Map<String, Object> headers = exchange.getIn().getHeaders();
            Person firstPerson = personRepository.findByName((String) headers.get("userId"));
            Person secondPerson = personRepository.findByName((String) headers.get("inviteeId"));
            secondPerson.addInviter(firstPerson);
            personRepository.save(secondPerson);
        });

        from("direct:invitations").process(exchange -> {
            Map<String, Object> headers = exchange.getIn().getHeaders();
            Collection<Person> inviters = personRepository.invitations((String) headers.get("userId"));
            exchange.getOut().setBody(inviters);
        });

        from("direct:friends").process(exchange -> {
            Map<String, Object> headers = exchange.getIn().getHeaders();
            Collection<Person> friends = personRepository.friends((String) headers.get("userId"));
            exchange.getOut().setBody(friends);
        });
    }
}
