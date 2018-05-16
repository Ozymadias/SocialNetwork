package com.socialnet.routes;

import com.socialnet.repository.MessageRepository;
import com.socialnet.users.Message;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MessageRouteBuilder extends RouteBuilder {
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    MessageRepository messageRepository;

    @Override
    public void configure() {
        from("direct:postMessage").process(exchange -> {
            String userId = (String) exchange.getIn().getHeader("userId");
            String message = (String) exchange.getIn().getHeader("message");
            messageRepository.save(new Message(userId, message));
        });

        from("direct:allMessages").process(exchange -> exchange.getOut().setBody(messageRepository.findAll()));

        from("direct:messages").process(exchange -> {
            Set<String> ids = (Set<String>) exchange.getIn().getBody();
            Query query = new Query(Criteria.where("authorId").in(ids));
            exchange.getOut().setBody(mongoTemplate.find(query, Message.class));
        });
    }
}
