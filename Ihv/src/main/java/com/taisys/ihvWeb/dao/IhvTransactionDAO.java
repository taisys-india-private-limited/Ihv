package com.taisys.ihvWeb.dao;

import java.util.Date;
import java.util.List;

import com.taisys.ihvWeb.model.IhvTransaction;

public interface IhvTransactionDAO {

	public boolean createTransaction(IhvTransaction transaction);

	public List<IhvTransaction> getTransactions(String mobileNo);

	public List<IhvTransaction> getAllTransactions(Date startDate, Date endDate);

	public IhvTransaction getTransactionByTransactionId(String transId);
	
	public List<IhvTransaction> getMerchantTransactions(String merchId);
	
	public List<IhvTransaction> getMerchantTransactionsForMobileNumber(String mobileNumber, String merchId);

	public boolean updateTransaction(IhvTransaction transaction);

}