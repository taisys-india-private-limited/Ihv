package com.taisys.ihvWeb.dao;

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

import com.taisys.ihvWeb.model.PortalUser;

@Repository("portalUserDao")
public class PortalUserDAOImpl implements PortalUserDAO {

	public static final Logger logger = Logger.getLogger(PortalUserDAOImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	private String password;

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public boolean createUser(PortalUser user) {
		try {
			getSession().save(user);
			return true;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public boolean updateUser(PortalUser user) {
		try {
			getSession().update(user);
			return true;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public PortalUser getUserByUserName(String userName) {
		try {
			Criteria criteria = getSession().createCriteria(PortalUser.class);
			criteria.add(Restrictions.eq("userName", userName.toLowerCase()));
			List<PortalUser> users = criteria.list();
			if (users.isEmpty()) {
				return null;
			} else {
				PortalUser user = users.get(0);
				if (!userName.contains("@")) {
					user.setPassword(password);
				}
				return user;
			}
		} catch (RuntimeException e) {
			throw e;
		}

	}

	@SuppressWarnings("unchecked")
	public PortalUser getUserByEmployeeID(String employeeID) {
		try {
			Criteria criteria = getSession().createCriteria(PortalUser.class);
			criteria.add(Restrictions.eq("employeeID", employeeID));
			List<PortalUser> users = criteria.list();
			if (users.isEmpty()) {
				return null;
			} else {
				return users.get(0);
			}
		} catch (RuntimeException e) {
			throw e;
		}

	}

	@SuppressWarnings("unchecked")
	public List<PortalUser> getAllTellersByBankID(String bankCode) {
		try {
			Criteria criteria = getSession().createCriteria(PortalUser.class);
			criteria.add(Restrictions.eq("bankCode", bankCode));
			criteria.add(Restrictions.isNotNull("bankBranchId"));
			criteria.addOrder(Order.desc("createDate"));
			List<PortalUser> allUsers = criteria.list();
			if (allUsers.isEmpty()) {
				return null;
			} else {
				return allUsers;
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<PortalUser> getAllTellersByBranchId(String bankBranchId) {
		try {
			Criteria criteria = getSession().createCriteria(PortalUser.class);
			criteria.add(Restrictions.eq("bankBranchId", bankBranchId));
			criteria.addOrder(Order.desc("createDate"));
			List<PortalUser> allUsers = criteria.list();
			if (allUsers.isEmpty()) {
				return null;
			} else {
				return allUsers;
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<PortalUser> getAllTellersByBankCode(String bankCode) {
		try {
			Criteria criteria = getSession().createCriteria(PortalUser.class);
			criteria.add(Restrictions.eq("bankCode", bankCode));
			criteria.addOrder(Order.desc("createDate"));
			List<PortalUser> allUsers = criteria.list();
			if (allUsers.isEmpty()) {
				return null;
			} else {
				return allUsers;
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<PortalUser> getAllYesBankUsers() {
		try {
			String hql = "from PortalUser as user where bankCode = :bankCode order by createDate desc";
			Query query = getSession().createQuery(hql);
			query.setParameter("bankCode", "YESBANK");
			List results = query.list();
			return results;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public List getAllBankHQUsers() {
		try {
			String hql = "from PortalUser as user where 'BANKHQADMIN' in elements(user.roles) order by createDate desc";
			Query query = getSession().createQuery(hql);
			List results = query.list();
			return results;
		} catch (RuntimeException e) {
			e.printStackTrace();logger.error(e.getMessage(), e);
			throw e;
		}
	}

	public void setPasswordForLDAPUser(String password) {
		this.password = password;
	}

}
