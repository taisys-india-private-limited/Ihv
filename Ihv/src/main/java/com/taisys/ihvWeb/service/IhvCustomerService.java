package com.taisys.ihvWeb.service;

import java.util.Date;
import java.util.List;

import com.taisys.ihvWeb.model.IhvCustomer;
 
public interface IhvCustomerService {
 
	public boolean createCustomer(IhvCustomer customer);
	
	public IhvCustomer getCustomer(String mobileNo);
	
	public IhvCustomer getCustomerByCustomerId(String custId);
	
	public boolean updateCustomer(IhvCustomer customer);
	
	@SuppressWarnings("rawtypes")
	public List registrationsReport(Date startDate, Date endDate, String bankCode, String branchId, String tellerId);
	
}