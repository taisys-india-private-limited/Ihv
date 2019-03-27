package com.taisys.ihvWeb.service;

import com.taisys.ihvWeb.model.IhvOTP;
 
public interface IhvOTPService {
 
	public boolean createOTP(IhvOTP otp);
	
	public boolean updateOTP(IhvOTP otp);
	
	public IhvOTP getOTP(String mobileNo);
	
}