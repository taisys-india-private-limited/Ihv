package com.taisys.ihvWeb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taisys.ihvWeb.dao.IhvTransactionDAO;
import com.taisys.ihvWeb.model.IhvTransaction;

@Service("transactionService")
@Transactional
public class IhvTransactionServiceImpl implements IhvTransactionService {

	@Autowired
	private IhvTransactionDAO dao;

	public boolean createTransaction(IhvTransaction transaction) {
		return dao.createTransaction(transaction);
	}

	public List<IhvTransaction> getTransactions(String mobileNo) {
		return dao.getTransactions(mobileNo);
	}

	public List<IhvTransaction> getMerchantTransactions(String merchId) {
		return dao.getMerchantTransactions(merchId);
	}
	
	
	public List<IhvTransaction> getMerchantTransactionsForMobileNumber(String mobileNumber, String merchId){
		return dao.getMerchantTransactionsForMobileNumber(mobileNumber, merchId);
	}
	
	
	public List<IhvTransaction> getAllTransactions(Date startDate, Date endDate) {
		return dao.getAllTransactions(startDate, endDate);
	}

	public IhvTransaction getTransactionByTransactionId(String transId){
		return dao.getTransactionByTransactionId(transId);
	}

	public boolean updateTransaction(IhvTransaction transaction) {
		return dao.updateTransaction(transaction);
	}


}