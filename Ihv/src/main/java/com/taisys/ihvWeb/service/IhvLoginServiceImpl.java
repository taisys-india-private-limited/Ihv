package com.taisys.ihvWeb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taisys.ihvWeb.dao.IhvLoginDAO;
import com.taisys.ihvWeb.model.IhvLogin;

@Service("loginService")
@Transactional
public class IhvLoginServiceImpl implements IhvLoginService {

	@Autowired
	private IhvLoginDAO dao;


	@Override
	public boolean createUser(IhvLogin User) {
		// TODO Auto-generated method stub
		return dao.createUser(User);
	}
	
	@SuppressWarnings("rawtypes")
	public List registrationsReport(Date startDate, Date endDate, String bankCode, String branchId, String tellerId) {
		return dao.registrationsReport(startDate, endDate, bankCode, branchId, tellerId);
	}

}