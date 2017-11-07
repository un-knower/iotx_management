package com.anosi.asset.test;

import org.junit.Test;

import com.anosi.asset.model.jpa.Iotx;

public class TestCharMatcher {

	@Test
	public void testCharMatcher(){
		Iotx iotx = new Iotx();
		iotx.setMemory("2GB");
		iotx.setUsedMemoryPer(0.4);
		iotx.setHardDisk("50GB");
		iotx.setUsedHardDiskPer(0.3);
		System.out.println(iotx.getFreeHardDisk());
		System.out.println(iotx.getUsedHardDisk());
		System.out.println(iotx.getFreeMemory());
		System.out.println(iotx.getUsedMemory());
	}
	
}
