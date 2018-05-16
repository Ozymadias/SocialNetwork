package com.socialnet.routes;

import com.socialnet.repository.NodeRepository;
import com.socialnet.users.Node;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class NodeRouteBuilder extends RouteBuilder {
    @Autowired
    NodeRepository nodeRepository;

    @Override
    public void configure() {
        from("direct:insert").process(exchange -> nodeRepository.save(new Node((String) exchange.getIn().getHeader("mongoId"))));

        from("direct:unfriend").process(exchange -> {
            Map<String, Object> headers = exchange.getIn().getHeaders();
            nodeRepository.unfriend((String) headers.get("userId"), (String) headers.get("friendId"));
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
            Node user = nodeRepository.findByMongoId((String) headers.get("userId"));
            Node invitee = nodeRepository.findByMongoId((String) headers.get("inviteeId"));
            invitee.addInviter(user);
            nodeRepository.save(invitee);
        });

        from("direct:invitations").process(exchange -> {
            String currentUser = (String) exchange.getIn().getHeader("userId");
            Collection<Node> inviters = nodeRepository.invitations(currentUser);
            exchange.getOut().setBody(inviters);
        });

        from("direct:friends").process(exchange -> {
            Collection<Node> friends = nodeRepository.friends((String) exchange.getIn().getHeader("userId"));
            exchange.getOut().setBody(friends);
        });

        from("direct:acceptInvitation").process(exchange -> {
            Map<String, Object> headers = exchange.getIn().getHeaders();
            String userId = (String) headers.get("userId");
            Node user = nodeRepository.findByMongoId(userId);
            String inviterId = (String) headers.get("inviterId");
            Node inviter = nodeRepository.findByMongoId(inviterId);

            if(nodeRepository.invitations(userId).contains(inviter)) {
                user.addFriendship(inviter);
                nodeRepository.save(user);
                nodeRepository.refuseInvitation(inviterId, userId);
            }
        });

        from("direct:network").process(exchange -> {
            exchange.getOut().setBody(nodeRepository.network((String) exchange.getIn().getHeader("userId")));
        });
    }

    private Predicate isThePersonWhoUserWantToInviteNotFriendOfHis() {
        return exchange -> nodeRepository
                .friends((String) exchange.getIn().getHeader("userId")).stream()
                .map(Node::getMongoId).noneMatch(name -> name.equals(exchange.getIn().getHeader("inviteeId")));
    }

    private Predicate didUserReceiveInvitationFromPersonWhoHeWantsInvite() {
        return exchange -> nodeRepository.findByMongoId((String) exchange.getIn().getHeader("userId"))
                .hasInvitationFrom(nodeRepository.findByMongoId((String) exchange.getIn().getHeader("inviteeId")));
    }
}
