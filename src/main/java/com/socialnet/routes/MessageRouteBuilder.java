package com.socialnet.routes;

import com.socialnet.repository.MessageRepository;
import com.socialnet.users.Message;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageRouteBuilder extends RouteBuilder {
    @Autowired
    MessageRepository messageRepository;

    @Override
    public void configure() {
        from("direct:postMessage").process(exchange -> {
            String userId = (String) exchange.getIn().getHeader("userId");
            String message = (String) exchange.getIn().getHeader("message");
            messageRepository.save(new Message(userId, message));
        });

        from("direct:messages").process(exchange -> exchange.getOut().setBody(messageRepository.findAll()));
    }
}
