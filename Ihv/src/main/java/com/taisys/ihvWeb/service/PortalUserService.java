package com.taisys.ihvWeb.service;

import java.util.List;

import com.taisys.ihvWeb.model.PortalUser;

public interface PortalUserService {

	public boolean createUser(PortalUser user);

	public PortalUser getUserByUserName(String userName);

	public PortalUser getUserByEmployeeID(String employeeID);

	public boolean updateUser(PortalUser user);

	public List<PortalUser> getAllTellersByBankID(String bankID);

	public List<PortalUser> getAllTellersByBranchId(String bankBranchId);

	public List<PortalUser> getAllTellersByBankCode(String bankCode);

	public List<PortalUser> getAllYesBankUsers();

	@SuppressWarnings("rawtypes")
	public List getAllBankHQUsers();
}
