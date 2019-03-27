package com.taisys.ihvWeb.service;

import com.taisys.ihvWeb.model.Captcha;

public interface CaptchaService {

	public boolean createCaptcha(Captcha captcha);

	public Captcha getCaptchaByUserId(String userId);

	public boolean deleteCaptcha(Captcha captcha);
}
