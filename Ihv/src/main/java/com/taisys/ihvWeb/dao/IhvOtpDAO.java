package com.taisys.ihvWeb.dao;

import com.taisys.ihvWeb.model.IhvOTP;

public interface IhvOtpDAO {

	public boolean createOTP(IhvOTP otp);
	
	public boolean updateOTP(IhvOTP otp);

	public IhvOTP getOTPByMobile(String mobileNo);
	
}