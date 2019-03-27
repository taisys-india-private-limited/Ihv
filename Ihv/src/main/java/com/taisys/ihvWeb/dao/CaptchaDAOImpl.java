package com.taisys.ihvWeb.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.taisys.ihvWeb.model.Captcha;

@Repository("CaptchaDAO")
public class CaptchaDAOImpl implements CaptchaDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public boolean createCaptcha(Captcha captcha){
		try {
			getSession().save(captcha);
			return true;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public boolean deleteCaptcha(Captcha captcha) {
		try {
			getSession().delete(captcha);
			return true;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public Captcha getCaptchaByUserId(String userId) {
		try {
			Criteria criteria = getSession().createCriteria(Captcha.class);
			criteria.add(Restrictions.eq("userId", userId));
			List<Captcha> captcha = criteria.list();
			if (captcha.isEmpty()) {
				return null;
			} else {
				return captcha.get(0);
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}

}
