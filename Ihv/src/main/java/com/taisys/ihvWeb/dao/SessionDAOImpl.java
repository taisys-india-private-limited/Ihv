package com.taisys.ihvWeb.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.taisys.ihvWeb.model.SessionInfo;

@Repository("TokensDAO")
public class SessionDAOImpl implements SessionDAO {

	@Autowired
	private SessionFactory sessionFactory;

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public boolean createSession(SessionInfo session) {
		try {
			getSession().save(session);
			return true;
		} catch (RuntimeException e) {
			throw e;
		}
	}
	
	public boolean updateSession(SessionInfo session) {
		try {
			getSession().update(session);
			return true;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public boolean deleteSession(String userId) {
		try {
			String hql = "DELETE FROM SessionInfo s WHERE s.userId = :user";

			Query query = getSession().createQuery(hql);
			//query.setParameter("token", userId);
			query.setParameter("user", userId.toLowerCase());
			query.executeUpdate();
			return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public boolean deleteExpiredSessions() {
		try {
			String hql = "DELETE FROM SessionInfo s WHERE s.expires < :now";
			Query query = getSession().createQuery(hql);
			query.setParameter("now", new Date());
			query.executeUpdate();
			return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public SessionInfo getSessionByUser(String userId) {
		try {
			Criteria criteria = getSession().createCriteria(SessionInfo.class);
			criteria.add(Restrictions.eq("userId", userId));
			List<SessionInfo> sessions = criteria.list();
			if (sessions == null || sessions.isEmpty()) {
				return null;
			} else {
				return sessions.get(0);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

}
