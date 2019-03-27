package com.taisys.ihvWeb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taisys.ihvWeb.dao.IhvOtpDAO;
import com.taisys.ihvWeb.model.IhvOTP;

@Service("otpService")
@Transactional
public class IhvOTPServiceImpl implements IhvOTPService {

	@Autowired
	private IhvOtpDAO dao;

	public boolean createOTP(IhvOTP otp) {
		return dao.createOTP(otp);
	}
	
	public boolean updateOTP(IhvOTP otp) {
		return dao.updateOTP(otp);
	}

	public IhvOTP getOTP(String mobileNo) {
		return dao.getOTPByMobile(mobileNo);
	}

}