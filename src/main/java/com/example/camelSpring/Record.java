package com.example.camelSpring;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;

@CsvRecord(separator="\\,")
public class Record {
    private String transId;
    private String transTms;
    private String rcNum;
    private String clientId;

    public Record () {}

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setRcNum(String rcNum) {
        this.rcNum = rcNum;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public void setTransTms(String transTms) {
        this.transTms = transTms;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRcNum() {
        return rcNum;
    }

    public String getTransId() {
        return transId;
    }

    public String getTransTms() {
        return transTms;
    }

    @Override
    public String toString() {
        return super.toString() + "asndalskdna";
    }
}
