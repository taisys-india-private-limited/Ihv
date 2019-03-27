package com.taisys.ihvWeb.dao;

import com.taisys.ihvWeb.model.Captcha;

public interface CaptchaDAO {
	public boolean createCaptcha(Captcha captcha);
	public Captcha getCaptchaByUserId(String userId);
	public boolean deleteCaptcha(Captcha captcha);
}
