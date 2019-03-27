package com.taisys.ihvWeb.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.taisys.ihvWeb.model.IhvOTP;

@Repository("otpDao")
public class IhvOtpDAOImpl implements IhvOtpDAO {

	@Autowired
	private SessionFactory sessionFactory;

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public boolean createOTP(IhvOTP otp) {
		try {
			getSession().save(otp);
			return true;
		} catch (RuntimeException e) {
			throw e;
		}
	}
	
	public boolean updateOTP(IhvOTP otp) {
		try {
			getSession().update(otp);
			return true;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public IhvOTP getOTPByMobile(String mobileNo) {
		try {
			Criteria criteria = getSession().createCriteria(IhvOTP.class);
			criteria.add(Restrictions.eq("mobileNo", mobileNo));
			Date validity = new Date(System.currentTimeMillis());
			Date creation = new Date(System.currentTimeMillis()- 180000);
			criteria.add(Restrictions.ge("createTime", creation));
			criteria.add(Restrictions.le("createTime", validity));
			criteria.add(Restrictions.eq("status", "UNUSED"));
			List<IhvOTP> otp = criteria.list();
			if (otp.isEmpty()) {
				return null;
			} else {
				return otp.get(0);
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}

}