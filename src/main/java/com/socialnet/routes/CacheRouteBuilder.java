package com.socialnet.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cache.CacheConstants;
import org.springframework.stereotype.Component;

@Component
public class CacheRouteBuilder extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:saveResult")
                .setHeader(CacheConstants.CACHE_OPERATION, constant("ADD"))
                .process(exchange -> {
                    String userId = (String) exchange.getIn().getHeader("userId");
                    String otherId = (String) exchange.getIn().getHeader("id");
                    exchange.getIn().setHeader(CacheConstants.CACHE_KEY, userId + otherId);
                    exchange.getIn().setBody(exchange.getIn().getBody(Integer.class));
                }).to("cache://cache");

        from("direct:lastRequestResult")
                .setHeader(CacheConstants.CACHE_OPERATION, constant("GET"))
                .process(exchange -> {
                    String userId = (String) exchange.getIn().getHeader("userId");
                    String otherId = (String) exchange.getIn().getHeader("id");
                    exchange.getIn().setHeader(CacheConstants.CACHE_KEY, userId + otherId);
                }).to("cache://cache")
                .choice()
                .when(exchange -> exchange.getIn().getBody(String.class).equals(""))
                .process(exchange -> exchange.getOut().setBody("Result is not ready yet"));
    }
}
