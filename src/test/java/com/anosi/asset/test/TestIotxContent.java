package com.anosi.asset.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.IotxManagementApplication;
import com.anosi.asset.model.elasticsearch.IotxContent;
import com.anosi.asset.model.jpa.Iotx;
import com.anosi.asset.model.jpa.Iotx.NetworkCategory;
import com.anosi.asset.model.jpa.Iotx.Status;
import com.anosi.asset.service.CompanyService;
import com.anosi.asset.service.DistrictService;
import com.anosi.asset.service.IotxContentService;
import com.anosi.asset.service.IotxService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IotxManagementApplication.class)
@Transactional
public class TestIotxContent {

	@Autowired
	private IotxService iotxService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DistrictService districtService;
	@Autowired
	private IotxContentService iotxContentService;
	
	@Test
	@Rollback(false)
	public void initContent(){
		Iotx iotx = new Iotx();
		iotx.setCompany(companyService.findByName("北京安诺信通信科技有限公司"));
		iotx.setSerialNo("abc123ef");
		iotx.setInstallLocation("无锡");
		iotx.setNetworkCategory(NetworkCategory.WIFI);
		iotx.setStatus(Status.ONLINE);
		iotx.setDistrict(districtService.getOne((long) 1));
		iotxService.save(iotx);
	}
	
	@Test
	public void testContent(){
		Page<IotxContent> iotxContents = iotxContentService.findByContent("北京安诺信通信科技有限公司", "无锡", new PageRequest(0, 10));
		for (IotxContent iotxContent : iotxContents) {
			System.out.println(iotxContent.getId());
		}
	}
	
}
