package com.example.camelSpring;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.apache.camel.test.junit4.CamelTestSupport;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CamelSpringApplicationTests extends CamelTestSupport{

	@Test
	public void contextLoads() {
        String expected = "transId,transTms,rcNum,clientId" + '\n' +
                "1,A,0,B";

        MockEndpoint mock = getMockEndpoint("mock:output");
        mock.expectedBodiesReceived(expected);
        String input = "{\n" +
                "\t\"batchId\": \"0310abf6-d1f5-a1b3-8fb0-36fe934b1f11\",\n" +
                "    \"records\": [\n" +
                "     { \n" +
                "\t\t\"transId\": \"1\",\n" +
                "\t\t\"transTms\": \"A\",\n" +
                "\t\t\"rcNum\": \"0\",\n" +
                "\t\t\"clientId\": \"B\",\n" +
                "\t\t\"event\": [\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"eventCnt\": 1,\n" +
                "\t\t\t\t\"locationCd\": \"DESTINATION\",\n" +
                "\t\t\t\t\"locationId1\": \"T8C\",\n" +
                "\t\t\t\t\"locationId2\": \"1J7\",\n" +
                "\t\t\t\t\"addrNbr\": \"0000000001\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"eventCnt\": 1,\n" +
                "\t\t\t\t\"locationCd\": \"CUSTOMER NUMBER\",\n" +
                "\t\t\t\t\"locationId1\": \"0007316971\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"eventCnt\": 1,\n" +
                "\t\t\t\t\"locationCd\": \"OUTLET ID\",\n" +
                "\t\t\t\t\"locationId1\": \"I029\"\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t}\n" +
                "\t]\t\n" +
                "}";
        template.sendBody("direct:sampleInput",input );
        System.out.println("Mock successfully tested");
	}
}

