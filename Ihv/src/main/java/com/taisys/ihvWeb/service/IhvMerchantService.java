package com.taisys.ihvWeb.service;

import java.util.List;

//import java.util.Date;
//import java.util.List;

import com.taisys.ihvWeb.model.IhvMerchant;
 
public interface IhvMerchantService {
 
	public boolean createMerchant(IhvMerchant merchant);
	
	public IhvMerchant getMerchant(String mobileNo);
	
	public IhvMerchant getMerchantEmail(String email);
	
	public IhvMerchant getMerchantPassword(String password);


	public IhvMerchant getMerchantByMerchantId(String MerchID);
	
	public boolean updateMerchant(IhvMerchant merchant);
	
	public List getMerchantByType();
 
	
//	@SuppressWarnings("rawtypes")
//	public List registrationsReport(Date startDate, Date endDate, String bankCode, String branchId, String tellerId);
	
}