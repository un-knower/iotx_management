package com.anosi.asset.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "account")
public class Account extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1503690620448714787L;

	private String name;

	private String loginId;

	private String password;

	private Company company;

	private List<Role> roleList = new ArrayList<Role>();

	private List<Privilege> privilegeList = new ArrayList<Privilege>();
	
	private List<RoleFunctionGroup> roleFunctionGroupList = new ArrayList<>();

	// 密码加盐
	private String salt;

	@Column(unique = true, nullable = false)
	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = Role.class)
	public List<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}

	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "account", targetEntity = Privilege.class)
	public List<Privilege> getPrivilegeList() {
		return privilegeList;
	}

	public void setPrivilegeList(List<Privilege> privilegeList) {
		this.privilegeList = privilegeList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Company.class)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
	@ManyToMany(fetch = FetchType.LAZY)
	public List<RoleFunctionGroup> getRoleFunctionGroupList() {
		return roleFunctionGroupList;
	}

	public void setRoleFunctionGroupList(List<RoleFunctionGroup> roleFunctionGroupList) {
		this.roleFunctionGroupList = roleFunctionGroupList;
	}

	/**
	 * 密码盐.
	 * 
	 * @return
	 */
	@Transient
	public String getCredentialsSalt() {
		return this.loginId + this.salt;
	}
	
	@Transient
	public boolean isAdmin(){
		Role role = new Role();
		role.setRoleCode("admin");
		return roleList.contains(role);
	}

}
