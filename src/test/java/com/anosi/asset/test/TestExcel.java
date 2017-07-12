package com.anosi.asset.test;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.anosi.asset.util.ExcelUtil;
import com.google.common.collect.Table;

public class TestExcel {

	@Test
	public void testReadExcel() throws IOException{
		Table<Integer, String, Object> excelTable = ExcelUtil.readExcel(new File("F:/20161226155537.xlsx"), 0);
		excelTable.rowMap().forEach((key,value)->{
			value.forEach((subKey,subValue)->{
				System.out.println("rowNum:"+key+"\ttitle:"+subKey+"\tvalue:"+subValue);
			});
		});
	}
	
}
