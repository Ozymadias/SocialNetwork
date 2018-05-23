package com.socialnet.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MessageRouteBuilder extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:postMessage")
                .to("bean:messageService?method=postMessage(${header.userId}, ${body})");

        from("direct:allMessages")
                .to("bean:messageService?method=receiveAllMessages()");

        from("direct:messages")
                .to("bean:messageService?method=receiveMessages(${body})");
    }
}
