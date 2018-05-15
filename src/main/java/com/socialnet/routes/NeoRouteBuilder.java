package com.socialnet.routes;

import com.socialnet.repository.PersonRepository;
import com.socialnet.users.Person;
import org.apache.camel.Predicate;
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
        from("direct:insert").process(exchange -> personRepository.save(new Person((String) exchange.getIn().getHeader("neo4jId"))));

        from("direct:unfriend").process(exchange -> {
            Map<String, Object> headers = exchange.getIn().getHeaders();
            personRepository.unfriend((String) headers.get("userId"), (String) headers.get("friendId"));
        });

        from("direct:invite")
                .choice()
                .when(isThePersonWhoUserWantToInviteNotFriendOfHis())
                .choice()
                .when(didUserReceiveInvitationFromPersonWhoHeWantsInvite())
                .to("direct:acceptPreviouslySendInvitation")
                .otherwise()
                .to("direct:sendInvitation")
                .endChoice()
                .endChoice();

        from("direct:acceptPreviouslySendInvitation")
                .process(exchange -> exchange.getIn().setHeader("inviterId", exchange.getIn().getHeader("inviteeId")))
                .to("direct:acceptInvitation");

        from("direct:sendInvitation").process(exchange -> {
            Map<String, Object> headers = exchange.getIn().getHeaders();
            Person user = personRepository.findByName((String) headers.get("userId"));
            Person invitee = personRepository.findByName((String) headers.get("inviteeId"));
            invitee.addInviter(user);
            personRepository.save(invitee);
        });

        from("direct:invitations").process(exchange -> {
            String currentUser = (String) exchange.getIn().getHeader("userId");
            Collection<Person> inviters = personRepository.invitations(currentUser);
            exchange.getOut().setBody(inviters);
        });

        from("direct:friends").process(exchange -> {
            Collection<Person> friends = personRepository.friends((String) exchange.getIn().getHeader("userId"));
            exchange.getOut().setBody(friends);
        });

        from("direct:acceptInvitation").process(exchange -> {
            Map<String, Object> headers = exchange.getIn().getHeaders();
            String userId = (String) headers.get("userId");
            Person user = personRepository.findByName(userId);
            String inviterId = (String) headers.get("inviterId");
            user.addFriendship(personRepository.findByName(inviterId));
            personRepository.save(user);
            personRepository.refuseInvitation(inviterId, userId);
        });

        from("direct:network").process(exchange -> {
            exchange.getOut().setBody(personRepository.network((String) exchange.getIn().getHeader("userId")));
        });
    }

    private Predicate isThePersonWhoUserWantToInviteNotFriendOfHis() {
        return exchange -> personRepository
                .friends((String) exchange.getIn().getHeader("userId")).stream()
                .map(Person::getName).noneMatch(name -> name.equals(exchange.getIn().getHeader("inviteeId")));
    }

    private Predicate didUserReceiveInvitationFromPersonWhoHeWantsInvite() {
        return exchange -> personRepository.findByName((String) exchange.getIn().getHeader("userId"))
                .hasInvitationFrom(personRepository.findByName((String) exchange.getIn().getHeader("inviteeId")));
    }
}
