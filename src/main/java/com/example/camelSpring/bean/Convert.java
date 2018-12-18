package com.example.camelSpring.bean;

import com.example.camelSpring.Record;
import org.apache.camel.Exchange;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileWriter;
import java.io.IOException;

public class Convert {
    public void convertToCSV (Exchange exchange) {
        Record record = exchange.getIn().getBody(Record.class);
        String file = "holderFiler";
/*
        CellProcessor[] processors = new CellProcessor[] {
                new NotNull(), // ISBN
                new NotNull(), // title
                new NotNull(), // author
                new NotNull(), // publisher


            : "10002",
            "": "RPS-00001",
        };*/
        try {
            FileWriter fileWriter = new FileWriter(file);
            ICsvBeanWriter beanWriter = new CsvBeanWriter(fileWriter,
                    CsvPreference.STANDARD_PREFERENCE);
            String[] header = {"transId", "transTms", "rcNum", "clientId"};
            beanWriter.writeHeader(header);
            beanWriter.write(record, header);
            exchange.getIn().setBody(fileWriter);

        }catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
