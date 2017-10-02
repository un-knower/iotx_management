package com.anosi.asset.test.groovy;

import static org.junit.Assert.*

import com.anosi.asset.model.jpa.RoleFunction
import com.anosi.asset.model.jpa.RoleFunctionBtn
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class GroovyTest_xml {

	private static final Logger logger = LoggerFactory.getLogger(GroovyTest_xml.class);
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testXmlSlurper(){
		def xml = """
<langs type='current' count='3' mainstream='true'>
  <language flavor='static' version='1.5'>Java</language>
  <language flavor='dynamic' version='1.6.0'>Groovy</language>
  <language flavor='dynamic' version='1.9'>JavaScript</language>
</langs>
"""
		
		def langs = new XmlSlurper().parseText(xml)
		langs.language.each{println it}
	}
	
	@Test
	public void testXmlSlurper2(){
		def xml = """
<roleFunctions>

	<!-- iotx节点管理 -->
	<roleFunction roleFunctionPageId="iotxManagement">
		<!-- 添加 -->
		<roleFunctionBtn btnId="add"></roleFunctionBtn>
		<!-- 修改 -->
		<roleFunctionBtn btnId="edit"></roleFunctionBtn>
		<!-- 删除 -->
		<roleFunctionBtn btnId="delete"></roleFunctionBtn>
		<!-- 批量配置 -->
		<roleFunctionBtn btnId="batadd"></roleFunctionBtn>
	</roleFunction>
	
	<!-- 传感器管理 -->
	<roleFunction roleFunctionPageId="sensorManagement">
		<!-- 添加 -->
		<roleFunctionBtn btnId="add"></roleFunctionBtn>
		<!-- 修改 -->
		<roleFunctionBtn btnId="edit"></roleFunctionBtn>
		<!-- 删除 -->
		<roleFunctionBtn btnId="delete"></roleFunctionBtn>
		<!-- 批量配置 -->
		<roleFunctionBtn btnId="batadd"></roleFunctionBtn>
	</roleFunction>
	
	<!-- 告警管理 -->
	<roleFunction roleFunctionPageId="iotxAlarmData">
	
	</roleFunction>
	
	<!-- 数据分析 -->
	<roleFunction roleFunctionPageId="dataAnalysis">
	
	</roleFunction>
</roleFunctions>
"""
				
				def roleFunctions = new XmlSlurper().parseText(xml)
				roleFunctions.roleFunction.each{println it.@roleFunctionPageId} 
				roleFunctions.roleFunction.roleFunctionBtn.each{println it.@btnId}
	}
	
	@Test
	public void testXmlSlurperFile(){
		def roleFunctions = new XmlSlurper().parse("src/main/resources/initResources/initRoleFunction.xml")
		roleFunctions.RoleFunction.each{
			println it.@roleFunctionPageId
			assert it.RoleFunction==""
		}
	}
	
	
	@Test
	public void testXmlSlurperFile2(){
		def add={}
		
		def roleFunctions = new XmlSlurper().parse("src/main/resources/initResources/roleFunctions.xml")
		//roleFunctions.roleFunction.each{new RoleFunction(roleFunctionPageId:it.@roleFunctionPageId)}
		//roleFunctions.roleFunction.roleFunctionBtn.each{println it.@btnId}
		//roleFunctions.roleFunction.each{it.roleFunctionBtn.each{println it.@btnId}}
		
		roleFunctions.roleFunction.each{
			def roleFunction=new RoleFunction(roleFunctionPageId:it.@roleFunctionPageId)
			println it.class
			it.roleFunctionBtn.each{
				new RoleFunctionBtn(roleFunction:roleFunction,btnId:it.@btnId)
			}
		}
		
	}

}
