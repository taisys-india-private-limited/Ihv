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

import com.taisys.ihvWeb.model.IhvLogin;

@Repository("loginDao")
public class IhvLoginDAOImpl implements IhvLoginDAO {

	public static final Logger logger = Logger.getLogger(IhvCustomerDAOImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public boolean createUser(IhvLogin User) {
		try {
			getSession().save(User);
			return true;
		} catch (RuntimeException e) {
			throw e;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List registrationsReport(Date startDate, Date endDate, String bankCode, String branchId, String tellerId) {
		logger.info("Retreiving Registration Report from: " + startDate + " to " + endDate + " for branch: " + branchId + " and teller: " + tellerId);
		try {
			String hql = "SELECT c.CustID, c.kitNumber , c.firstName , c.lastName , c.address1 , c.email , c.motherName , c.mobileNo , c.bankAccountNo , c.bankAccountIfsc , c.state , c.city , c.pinCode , c.DOB , c.status , c.registrationDate , c.registeredBy , c.kycRequestStatus , b.bankBranchName , b.bankCode, n.bankName FROM Customer c, BranchIfscDetails b, NewPartnerBank n WHERE  c.branchId = b.branchId AND b.bankCode = n.BankCode AND c.registrationDate >= :start AND c.registrationDate < :end";
			if (!bankCode.equals("")) {
				hql += " AND b.bankCode = :bank";
			}
			if (!branchId.equals("")) {
				hql += " AND b.branchId = :branch";
			}
			if (!tellerId.equals("")) {
				hql += " AND c.registeredBy = :teller";
			}
			
			hql += " order by c.registrationDate desc";
			
			Query query = getSession().createQuery(hql).setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			query.setParameter("start", startDate);
			query.setParameter("end", endDate);
			if (!bankCode.equals("")) {
				query.setParameter("bank", bankCode);
			}
			if (!branchId.equals("")) {
				query.setParameter("branch", branchId);
			}
			if (!tellerId.equals("")) {
				query.setParameter("teller", tellerId);
			}
			List results = query.list();
			logger.info("Transaction Detailed Report from: " + startDate + " to " + endDate + "retreived successfully");
			return results;
		} catch (RuntimeException e) {
			logger.info("Error Getting Transaction Detailed Report");
			e.printStackTrace();logger.error(e.getMessage(), e);
			throw e;
		}
	}

}