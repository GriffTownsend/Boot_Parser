package com.ef.Parser;

import com.ef.Parser.components.LogRecordCSVParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.net.URL;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ParserApplicationTests {

	@Resource
	LogRecordCSVParser logRecordCSVParser;

	@Test
	public void contextLoads() {
		URL resource = Thread.currentThread().getContextClassLoader().getResource("test.log");
		logRecordCSVParser.importCSVFile(resource.getPath());
	}

}
