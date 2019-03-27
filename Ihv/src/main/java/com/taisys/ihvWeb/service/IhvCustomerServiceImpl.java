package com.taisys.ihvWeb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taisys.ihvWeb.dao.IhvCustomerDAO;
import com.taisys.ihvWeb.model.IhvCustomer;

@Service("customerService")
@Transactional
public class IhvCustomerServiceImpl implements IhvCustomerService {

	@Autowired
	private IhvCustomerDAO dao;

	public boolean createCustomer(IhvCustomer customer) {
		return dao.createCustomer(customer);
	}

	public IhvCustomer getCustomer(String mobileNo) {
		return dao.getCustomerByMobile(mobileNo);
	}
	
	public IhvCustomer getCustomerByCustomerId(String custId){
		return dao.getCustomerByCustomerId(custId);
	}

	public boolean updateCustomer(IhvCustomer customer) {
		return dao.updateCustomer(customer);
	}

	@SuppressWarnings("rawtypes")
	public List registrationsReport(Date startDate, Date endDate, String bankCode, String branchId, String tellerId) {
		return dao.registrationsReport(startDate, endDate, bankCode, branchId, tellerId);
	}

}