package com.taisys.ihvWeb.dao;

import java.util.List;

import com.taisys.ihvWeb.model.PortalUser;

public interface PortalUserDAO {
	public boolean createUser(PortalUser user);

	public boolean updateUser(PortalUser user);

	public PortalUser getUserByUserName(String userName);

	public PortalUser getUserByEmployeeID(String employeeID);

	public List<PortalUser> getAllTellersByBankID(String bankID);

	public List<PortalUser> getAllTellersByBranchId(String bankBranchId);

	public List<PortalUser> getAllTellersByBankCode(String bankCode);

	public List<PortalUser> getAllYesBankUsers();

	@SuppressWarnings("rawtypes")
	public List getAllBankHQUsers();

	public void setPasswordForLDAPUser(String password);

}
