package com.example.camelSpring;

import com.example.camelSpring.bean.Convert;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.stereotype.Component;
import org.apache.camel.Converter;

import org.apache.camel.TypeConverter;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class camelRouter extends RouteBuilder {

    //final DataFormat bindy  = new BindyCsvDataFormat(LinkedHashMap.class);

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .component("restlet")
                .host("localhost").port("8000");

        rest("/insertpayload")
                .consumes("application/json")
                .produces("text/plain")
                .post()
                .to("direct:split");

        from("direct:split")
                .streamCaching() //Allows to read the exchange data multiple times because jsonpath only read once
                .setProperty("batchId", jsonpath("$.batchId")) //to be able to use later
                .split().jsonpath("$.records.*")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        //String propId = (String) exchange.getProperty("batchId");
                        Map record = exchange.getIn().getBody(Map.class);
                        record.remove("event");
                        exchange.getIn().setBody(record);
                    }
                })
                //.unmarshal(bindy)
                //.bean(new Convert())
                //TODO: convert to CSV
                .to("direct:aggregate");

        //${exchangeProperty[batchId]}

        from("direct:aggregate")

                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String record = exchange.getIn().getBody(String.class);
                        System.out.println(record);
                    }
                })
                .to("mock:fool");
    }

}
