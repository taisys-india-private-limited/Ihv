package com.taisys.ihvWeb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taisys.ihvWeb.dao.CaptchaDAO;
import com.taisys.ihvWeb.model.Captcha;

@Service("CaptchaService")
@Transactional
public class CaptchaServiceImpl implements CaptchaService {

	@Autowired
	private CaptchaDAO dao;

	@Override
	public boolean createCaptcha(Captcha captcha){
		return dao.createCaptcha(captcha);
	}

	public boolean deleteCaptcha(Captcha captcha) {
		return dao.deleteCaptcha(captcha);
	}

	public Captcha getCaptchaByUserId(String userId){
		return dao.getCaptchaByUserId(userId);
	}

}
