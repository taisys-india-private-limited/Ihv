package com.taisys.ihvWeb.service;

import java.util.Date;
import java.util.List;

import com.taisys.ihvWeb.model.IhvLogin;
 
public interface IhvLoginService {
 
	public boolean createUser(IhvLogin User);
	

	@SuppressWarnings("rawtypes")
	public List registrationsReport(Date startDate, Date endDate, String bankCode, String branchId, String tellerId);
	
	
}