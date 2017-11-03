package com.anosi.asset.init;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.anosi.asset.component.I18nComponent;
import com.anosi.asset.component.PasswordEncry;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.model.jpa.Account;
import com.anosi.asset.model.jpa.Company;
import com.anosi.asset.mqtt.MqttServer;
import com.anosi.asset.service.AccountService;
import com.anosi.asset.service.CompanyService;
import com.anosi.asset.service.RoleService;

/***
 * 进行一些数据的初始化工作
 * 
 * @author jinyao
 *
 */
@Component
public class InitData {

	private static final Logger logger = LoggerFactory.getLogger(InitData.class);

	@Autowired
	private AccountService accountService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private TransactionTemplate transactionTemplate;
	@Autowired
	private I18nComponent i18nComponent;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private InitRoleRelated initRoleRelated;
	@Autowired
	private InitRoleFunctionRelated initRoleFunctionRelated;
	@Autowired
	private MqttServer mqttServer;

	@PostConstruct
	public void init() {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				logger.debug("init start");
				initRoleRelated.initDepRelated();
				initAdmin();
				initCompany();
				initRoleFunctionRelated.initRoleFunctionRelated();

				// mqtt初始化连接
				try {
					mqttServer.connect();
				} catch (MqttSecurityException e) {
					e.printStackTrace();
					throw new CustomRunTimeException(i18nComponent.getMessage("mqtt.init.fail"));
				} catch (MqttException e) {
					e.printStackTrace();
					throw new CustomRunTimeException(i18nComponent.getMessage("mqtt.init.fail"));
				}

				logger.debug("init end");
			}
		});
	}

	/***
	 * 初始化admin
	 */
	private void initAdmin() {
		Account account = this.accountService.findByLoginId("admin");

		if (account == null) {
			account = new Account();
			account.setName("admin");
			account.setLoginId("admin");
			account.setPassword("123456");
			account.getRoleList().add(roleService.findByRoleCode("admin"));
			try {
				// 设置密码
				PasswordEncry.encrypt(account);
			} catch (Exception e) {
				throw new CustomRunTimeException();
			}
			accountService.save(account);
		}
	}

	private void initCompany() {
		/*String companyName = i18nComponent.getMessage("anosi");
		if (companyService.findByName(companyName) == null) {
			Company company = new Company();
			company.setName(companyName);
			companyService.save(company);
			Account account = this.accountService.findByLoginId("admin");
			account.setCompany(company);
		}*/
		String goaland = i18nComponent.getMessage("goaland");
		if (companyService.findByName(goaland) == null) {
			Company company = new Company();
			company.setName(goaland);
			companyService.save(company);
			Account account = this.accountService.findByLoginId("admin");
			account.setCompany(company);
		}
	}

}
