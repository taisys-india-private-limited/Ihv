package com.taisys.ihvWeb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taisys.ihvWeb.dao.IhvMerchantDAO;
import com.taisys.ihvWeb.model.IhvMerchant;

@Service("merchantService")
@Transactional
public class IhvMerchantServiceImpl implements IhvMerchantService {

	@Autowired
	private IhvMerchantDAO dao;

	public boolean createMerchant(IhvMerchant merchant) {
		return dao.createMerchant(merchant);
	}
	
	public IhvMerchant getMerchant(String mobileNo) {
		return dao.getMerchantByMobile(mobileNo);
	}
	
	public IhvMerchant getMerchantEmail(String email) {
		return dao.getMerchantByEmail(email);
	}
	
	public IhvMerchant getMerchantPassword(String password) {
		return dao.getMerchantByPassword(password);
	}
	
	
		public IhvMerchant getMerchantByMerchantId(String MerchID){
		return dao.getMerchantByMerchantId(MerchID);
	}

	public boolean updateMerchant(IhvMerchant merchant) {
		return dao.updateMerchant(merchant);
	}


	@Override
	public List getMerchantByType() {
		return dao.getMerchantByType();
	}

}