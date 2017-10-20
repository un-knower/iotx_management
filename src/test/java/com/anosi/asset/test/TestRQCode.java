package com.anosi.asset.test;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.anosi.asset.util.CodeUtil;

public class TestRQCode {

	@Test
	public void createRQCode() throws IOException {
		StringBuilder sb = new StringBuilder();
		Calendar calendar=Calendar.getInstance();  
		for (int i = 1; i <= 100; i++) {
			sb.append(calendar.get(GregorianCalendar.YEAR));
			sb.append(calendar.get(GregorianCalendar.MONTH) + 1);
			sb.append(calendar.get(GregorianCalendar.DATE));
			sb.append(calendar.get(GregorianCalendar.HOUR));
			sb.append("-");
			sb.append(String.format("%04d", i));
			CodeUtil.getRQWriteFile(sb.toString(), 200, new File("F:/RQCode/"+sb.toString()+".png"));
			sb.delete(0, sb.length());
		}
	}

}
