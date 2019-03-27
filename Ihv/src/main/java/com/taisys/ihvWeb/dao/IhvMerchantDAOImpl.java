package com.taisys.ihvWeb.dao;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.taisys.ihvWeb.model.IhvMerchant;

@Repository("merchantDao")
public class IhvMerchantDAOImpl implements IhvMerchantDAO {

	public static final Logger logger = Logger.getLogger(IhvMerchantDAOImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public boolean createMerchant(IhvMerchant wallet) {
		try {
			getSession().save(wallet);
			return true;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public IhvMerchant getMerchantByMobile(String mobileNo) {
		try {
			Criteria criteria = getSession().createCriteria(IhvMerchant.class);
			criteria.add(Restrictions.eq("mobileNo", mobileNo));
			List<IhvMerchant> wallets = criteria.list();
			if (wallets.isEmpty()) {
				logger.info("Merchant not found.");
				return null;
			} else {
				logger.info("Merchant found successfully.");
				return wallets.get(0);
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public IhvMerchant getMerchantByEmail(String email) {
		try {
			Criteria criteria = getSession().createCriteria(IhvMerchant.class);
			criteria.add(Restrictions.eq("email", email));
			List<IhvMerchant> wallets = criteria.list();
			if (wallets.isEmpty()) {
				logger.info("Merchant not found.");
				return null;
			} else {
				logger.info("Merchant found successfully.");
				return wallets.get(0);
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}

	/*@SuppressWarnings("unchecked")
	public List<IhvMerchant> getMerchantByType() {
		try {
			Criteria criteria = getSession().createCriteria(IhvMerchant.class);
			criteria.add(Restrictions.eq("type", "Merchant"));
			List<IhvMerchant> merchants = criteria.list();
			if (merchants.isEmpty()) {
				logger.info("Merchants not found.");
				return null;
			} else {
				logger.info("Merchants found successfully.");
				return merchants;
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}*/

	@SuppressWarnings("unchecked")
	public IhvMerchant getMerchantByMerchantId(String MerchID) {
		try {
			Criteria criteria = getSession().createCriteria(IhvMerchant.class);
			criteria.add(Restrictions.eq("MerchID", MerchID));
			List<IhvMerchant> wallets = criteria.list();
			if (wallets.isEmpty()) {
				logger.info("Merchant not found.");
				return null;
			} else {
				logger.info("Merchant found successfully.");
				return wallets.get(0);
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public IhvMerchant getMerchantByPassword(String password) {
		try {
			Criteria criteria = getSession().createCriteria(IhvMerchant.class);
			criteria.add(Restrictions.eq("password", password));
			List<IhvMerchant> wallets = criteria.list();
			if (wallets.isEmpty()) {
				logger.info("Merchant not found.");
				return null;
			} else {
				logger.info("Merchant found successfully.");
				return wallets.get(0);
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public boolean updateMerchant(IhvMerchant wallet) {
		try {
			getSession().update(wallet);
			return true;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@SuppressWarnings("rawtypes")
	public List getMerchantByType() {
		logger.info("Retreiving Merchants");
		try {
			String hql = "SELECT c.MerchID, c.accountNo , c.firstName , c.email , c.mobileNo  , c.ifsc , c.dob , c.status , c.registrationDate, c.accountType FROM IhvMerchant c WHERE  c.type = :merchant ";

			Query query = getSession().createQuery(hql).setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			query.setParameter("merchant", "Merchant");
			
			
			List results = query.list();
			return results;
		} catch (RuntimeException e) {
			logger.info("Error Getting Transaction Detailed Report");
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

}