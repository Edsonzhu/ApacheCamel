package com.example.camelSpring.bean;

import org.apache.camel.Exchange;
import java.util.Map;

public class Convert {
    public void convertToCSV (Exchange exchange) {
        Map record = exchange.getIn().getBody(Map.class);
        String header = "";
        String body = "";

        header += record.keySet();
        body += record.values();
        String csv = header + '\n' + body;
        exchange.getIn().setHeader("fileType", "csv");
        exchange.getIn().setBody(csv);
    }
}
