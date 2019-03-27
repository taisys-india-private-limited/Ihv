package com.taisys.ihvWeb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taisys.ihvWeb.dao.PortalUserDAO;
import com.taisys.ihvWeb.model.PortalUser;

@Service("portalUserService")
@Transactional
public class PortalUserServiceImpl implements PortalUserService {

	@Autowired
	private PortalUserDAO dao;

	public boolean createUser(PortalUser user) {
		return dao.createUser(user);
	}

	public PortalUser getUserByUserName(String userName) {
		return dao.getUserByUserName(userName);
	}

	public PortalUser getUserByEmployeeID(String employeeID) {
		return dao.getUserByEmployeeID(employeeID);
	}

	public boolean updateUser(PortalUser user) {
		return dao.updateUser(user);
	}

	public List<PortalUser> getAllTellersByBankID(String bankID) {
		return dao.getAllTellersByBankID(bankID);
	}

	public List<PortalUser> getAllTellersByBranchId(String bankBranchId) {
		return dao.getAllTellersByBranchId(bankBranchId);
	}

	public List<PortalUser> getAllTellersByBankCode(String bankCode) {
		return dao.getAllTellersByBankCode(bankCode);
	}

	public List<PortalUser> getAllYesBankUsers() {
		return dao.getAllYesBankUsers();
	}

	@SuppressWarnings("rawtypes")
	public List getAllBankHQUsers() {
		return dao.getAllBankHQUsers();
	}

}
