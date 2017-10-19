package com.anosi.asset.test;

import java.io.File;
import java.io.FileInputStream;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.junit.Test;

public class TestIni {

	@Test
	public void testReadIni() throws Exception {
		Ini ini = new Ini(
				new FileInputStream(new File("C:/Users/jinyao/Documents/Tencent Files/573380618/FileRecv/config.ini")));
		Section section = ini.get("system_conf");
		System.out.println(section.get("unique_id"));
		
	}

}
