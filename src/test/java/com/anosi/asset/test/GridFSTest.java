package com.anosi.asset.test;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.anosi.asset.IotxManagementApplication;
import com.anosi.asset.model.mongo.FileMetaData;
import com.anosi.asset.service.FileMetaDataService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IotxManagementApplication.class)
public class GridFSTest {
	
	private static final Logger logger = LoggerFactory.getLogger(GridFSTest.class);

	@Autowired
	private FileMetaDataService fileMetaDataService;
	
	@Test
	public void testUploadFile() throws Exception{
		File file = new File("g:/yum");
		FileMetaData saveFile = fileMetaDataService.saveFile("hello world", "test", new FileInputStream(file),file.length());
		logger.debug(saveFile.getObjectId().toString());
	}
	
}
