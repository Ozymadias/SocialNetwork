package com.socialnet.routes;

import com.socialnet.repository.NodeRepository;
import com.socialnet.users.Node;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class NodeRouteBuilder extends RouteBuilder {
    @Autowired
    NodeRepository nodeRepository;

    @Override
    public void configure() {
        from("direct:insert")
                .to("bean:nodeBean?method=insert(${header.mongoId})");

        from("direct:ins").process(exchange ->
                Arrays.stream(exchange.getIn().getBody(String[].class)).forEach(u -> nodeRepository.save(new Node(u))));

        from("direct:unfriend")
                .to("bean:nodeBean?method=unfriend(${header.userId}, ${header.friendId})");

        from("direct:invite")
                .choice()
                .when(isThePersonWhoUserWantToInviteNotFriendOfHis)
                .choice()
                .when(didUserReceiveInvitationFromPersonWhoHeWantsInvite)
                .to("direct:acceptPreviouslySendInvitation")
                .otherwise()
                .to("direct:sendInvitation")
                .endChoice()
                .endChoice();

        from("direct:acceptPreviouslySendInvitation")
                .process(exchange -> exchange.getIn().setHeader("inviterId", exchange.getIn().getHeader("inviteeId")))
                .to("direct:acceptInvitation");

        from("direct:sendInvitation")
                .choice()
                .when(header("userId").isEqualTo(header("inviteeId")))
                .process(exchange -> exchange.getOut().setBody("You can not sent invitation to yourself"))
                .otherwise()
                .to("bean:nodeBean?method=sendInvitation(${header.userId}, ${header.inviteeId})");

        from("direct:invitations")
                .to("bean:nodeBean?method=invitations(${header.userId})");

        from("direct:friends")
                .to("bean:nodeBean?method=friends(${header.userId})");

        from("direct:acceptInvitation")
                .to("bean:nodeBean?method=acceptInvitation(${header.userId}, ${header.inviterId})");

        from("direct:network")
                .to("bean:nodeBean?method=network(${header.userId})");

        from("direct:refuseInvitation")
                .to("bean:nodeBean?method=refuseInvitation(${header.userId}, ${header.inviterId})");

        from("direct:findFriends")
                .to("bean:nodeBean?method=findFriends(${header.userId})")
                .to("direct:messages");

        from("direct:findNetwork")
                .to("bean:nodeBean?method=findNetwork(${header.userId})")
                .to("direct:messages");

        from("direct:distance")
                .wireTap("activemq:queue:distance?concurrentConsumers=10")
                .process(exchange -> exchange.getOut().setBody("Request received"));

        from("activemq:queue:distance")
                .to("bean:nodeBean?method=computeDistance(${header.userId}, ${header.id})")
                .to("direct:saveResult");
    }

    Predicate isThePersonWhoUserWantToInviteNotFriendOfHis =
            exchange -> nodeRepository
                    .friends((String) exchange.getIn().getHeader("userId")).stream()
                    .map(Node::getMongoId).noneMatch(name -> name.equals(exchange.getIn().getHeader("inviteeId")));

    Predicate didUserReceiveInvitationFromPersonWhoHeWantsInvite =
            exchange -> nodeRepository.findByMongoId((String) exchange.getIn().getHeader("userId"))
                    .hasInvitationFrom(nodeRepository.findByMongoId((String) exchange.getIn().getHeader("inviteeId")));
}
