package com.example.camelSpring;

import com.example.camelSpring.bean.Convert;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.processor.aggregate.AggregationStrategy;
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
                .bean(new Convert())
                .to("direct:aggregate");

        //${exchangeProperty[batchId]}

        from("direct:aggregate")
                .aggregate(new AggregationStrategy() {
                    @Override
                    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                        if (oldExchange == null)
                        {
                            // the first time we only have the new exchange so it wins the first round
                            return newExchange;
                        }
                        String oldData = oldExchange.getIn().getBody(String.class);
                        String newData = newExchange.getIn().getBody(String.class);
                        String[] newDataArray = newData.split("[\n]");
                        oldData += '\n' + newDataArray[1];
                        oldExchange.getIn().setBody(oldData);
                        return oldExchange;
                    }
                })
                .header("fileType")
                .completionInterval(60000)
                .completionSize(10)
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
