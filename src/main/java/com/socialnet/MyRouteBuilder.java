package com.socialnet;

import com.mongodb.DBObject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MyRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        restConfiguration()
                .component("servlet")
                .host("0.0.0.0").port("8080")
                .bindingMode(RestBindingMode.json);

        rest().produces("application/json")
                .put("/register").to("direct:register")
                .get("/showAll").route().to("direct:show");

        from("direct:register").convertBodyTo(DBObject.class)
                .to("mongodb:mongo?database=test&collection=user&operation=insert");

        from("direct:show")
                .to("mongodb:mongo?database=test&collection=user&operation=findAll");//.marshal().json(JsonLibrary.Jackson);

    }
}
