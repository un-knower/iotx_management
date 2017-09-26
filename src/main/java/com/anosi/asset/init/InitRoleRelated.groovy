package com.anosi.asset.init

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.anosi.asset.model.jpa.Account
import com.anosi.asset.model.jpa.Role
import com.anosi.asset.service.AccountService
import com.anosi.asset.service.RoleService

/***
 * 初始化部门，部门内部组，角色
 * 因为涉及xml操作，所以用groovy来写比较优雅
 * @author jinyao
 *
 */
@Component
class InitRoleRelated {

	@Autowired
	private RoleService roleService

	protected void initDepRelated(){
		def roles = new XmlSlurper().parse(this.getClass().getResourceAsStream("/initResources/initRole.xml"))
		
		// 初始化前，需要判断是否在数据库中存在
		// 闭包
		roles.role.each{role->
			checkRole(role.@code.toString(), role.@name.toString())
		}
		
	}

	/***
	 * 判断role是否存在
	 * @param code
	 * @param name
	 * @param account
	 * @return
	 */
	private Role checkRole(String code,String name){
		def role = roleService.findByRoleCode(code)
		if(role==null){
			role = new Role(roleCode:code,name:name)
			role = roleService.save(role)
		}
		return role
	}

}
