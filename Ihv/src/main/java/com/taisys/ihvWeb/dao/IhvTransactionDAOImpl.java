package com.taisys.ihvWeb.dao;

import java.util.ArrayList;
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

import com.taisys.ihvWeb.model.IhvTransaction;

@Repository("transactionDao")
public class IhvTransactionDAOImpl implements IhvTransactionDAO {

	public static final Logger logger = Logger.getLogger(IhvTransactionDAOImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public boolean createTransaction(IhvTransaction transaction) {
		try {
			getSession().save(transaction);
			return true;
		} catch (RuntimeException e) {
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<IhvTransaction> getMerchantTransactions(String merchId) {
		try {
			Criteria criteria = getSession().createCriteria(IhvTransaction.class);
			criteria.add(Restrictions.eq("merchId", merchId));
			criteria.add(Restrictions.eq("transactionStatus", "SUCCESS"));
			criteria.addOrder(Order.desc("completedOn"));
			//criteria.createAlias("completedOn", "comPon");
	//		criteria.addOrder(Order.desc("transactionStatus"));
			List<IhvTransaction> transactions = criteria.list();
			if (transactions.isEmpty()) {
				logger.info("Transactions not found.");
				return null;
			} else {
				logger.info("Transactions found successfully.");
				//return transactions.get(0);
				return transactions;
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<IhvTransaction> getTransactions(String mobileNo) {
		try {
			Criteria criteria = getSession().createCriteria(IhvTransaction.class);
			criteria.add(Restrictions.eq("mobileNo", mobileNo));
			criteria.addOrder(Order.desc("completedOn"));
		//	criteria.add(Restrictions.ne("transactionStatus", "IN-PROGRESS"));
			//criteria.createAlias("completedOn", "comPon");
	//		criteria.addOrder(Order.desc("transactionStatus"));
			List<IhvTransaction> transactions = criteria.list();
			if (transactions.isEmpty()) {
				logger.info("Transactions not found.");
				return null;
			} else {
				logger.info("Transactions found successfully.");
				//return transactions.get(0);
				return transactions;
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}

	
	@SuppressWarnings("unchecked")
	public List<IhvTransaction> getMerchantTransactionsForMobileNumber(String mobileNo, String merchID) {
		try {
			Criteria criteria = getSession().createCriteria(IhvTransaction.class);
			criteria.add(Restrictions.like("mobileNo", mobileNo + "%"));
			criteria.add(Restrictions.eq("merchId", merchID));
			criteria.add(Restrictions.eq("transactionStatus", "SUCCESS"));
			criteria.addOrder(Order.desc("completedOn"));
		//	criteria.add(Restrictions.ne("transactionStatus", "IN-PROGRESS"));
			//criteria.createAlias("completedOn", "comPon");
	//		criteria.addOrder(Order.desc("transactionStatus"));
			List<IhvTransaction> transactions = criteria.list();
			if (transactions.isEmpty()) {
				logger.info("Transactions not found.");
				return new ArrayList<>();
			} else {
				logger.info("Transactions found successfully.");
				//return transactions.get(0);
				return transactions;
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getAllTransactions(Date startDate, Date endDate) {
		try {
			String hql = "SELECT i.mobileNo, i.amount, i.convFee, i.gstFee, i.totalAmount, i.paymentRefId, i.transactionStatus, i.completedOn, i.transId,i.merchId,i.remark"
					+ " FROM IhvTransaction i WHERE  i.completedOn >= :startDate AND i.completedOn <= :endDate order by i.completedOn desc";
			Query query = getSession().createQuery(hql).setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
			List transactions = query.list();
			if (transactions.isEmpty()) {
				logger.info("Transactions not found.");
				return null;
			} else {
				logger.info("Transactions found successfully from: " + startDate + " to " + endDate);
				//return transactions.get(0);
				return transactions;
			}
		} catch (RuntimeException e) {
			logger.info("Error executing getAllTransactions request!");
			e.printStackTrace();logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public IhvTransaction getTransactionByTransactionId(String transId) {
		try {
			Criteria criteria = getSession().createCriteria(IhvTransaction.class);
			criteria.add(Restrictions.eq("transId", transId));
			List<IhvTransaction> transactions = criteria.list();
			if (transactions.isEmpty()) {
				logger.info("Transaction not found.");
				return null;
			} else {
				logger.info("Transaction found successfully.");
				return transactions.get(0);
			}
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public boolean updateTransaction(IhvTransaction transaction) {
		try {
			getSession().update(transaction);
			return true;
		} catch (RuntimeException e) {
			throw e;
		}
	}

}