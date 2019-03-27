package com.taisys.ihvWeb.dao;

import java.util.Date;
import java.util.List;

import com.taisys.ihvWeb.model.IhvMerchant;
 
public interface IhvMerchantDAO {
 
	public boolean createMerchant(IhvMerchant merchant);
	
	public IhvMerchant getMerchantByMobile(String mobileNo);

	public IhvMerchant getMerchantByEmail(String email);
	
	public IhvMerchant getMerchantByPassword(String password);
	
	public IhvMerchant getMerchantByMerchantId(String MerchID);

	public boolean updateMerchant(IhvMerchant merchant);
	
	public List getMerchantByType();
	
}