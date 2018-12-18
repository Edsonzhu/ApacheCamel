package com.example.camelSpring;

import com.example.camelSpring.bean.Convert;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class camelRouter extends RouteBuilder {

    static Logger LOGGER = LoggerFactory.getLogger(camelRouter.class);

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
                .log(LoggingLevel.INFO, LOGGER, "Records from batchId ${exchangeProperty[batchId]} were split")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        //String propId = (String) exchange.getProperty("batchId");
                        Map record = exchange.getIn().getBody(Map.class);
                        record.remove("event");
                        exchange.getIn().setBody(record);
                    }
                })
                .log(LoggingLevel.INFO, LOGGER, "Event was removed from record")
                .bean(new Convert())
                .log(LoggingLevel.INFO, LOGGER, "Record is successfully converted to csv")
                .to("direct:aggregateNwrite");

        from("direct:aggregateNwrite")
                .aggregate(new AggregationStrategy() {
                    @Override
                    /**
                     * Aggregate the CSV files creating a file with only only line of parameters name
                     */
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
                .log(LoggingLevel.INFO, LOGGER, "CSV files were successfully aggregated")
                .to("mock:output");

    }

}