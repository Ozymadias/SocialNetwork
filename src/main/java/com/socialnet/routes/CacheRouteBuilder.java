package com.socialnet.routes;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CacheRouteBuilder extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:saveResult")
                .setHeader("CamelEhcacheAction", constant("PUT"))
                .process(setCamelEhcacheKey())
                .to("ehcache://cache");

        from("direct:lastRequestResult")
                .setHeader("CamelEhcacheAction", constant("GET"))
                .process(setCamelEhcacheKey())
                .to("ehcache://cache")
                .choice()
                .when(exchange -> exchange.getIn().getBody(String.class).equals(""))
                .process(exchange -> exchange.getOut().setBody("Result is not ready yet"));
    }

    private Processor setCamelEhcacheKey() {
        return exchange -> {
            String userId = (String) exchange.getIn().getHeader("userId");
            String otherId = (String) exchange.getIn().getHeader("id");
            exchange.getIn().setHeader("CamelEhcacheKey", userId + otherId);
        };
    }
}
