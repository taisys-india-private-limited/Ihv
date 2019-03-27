package com.taisys.ihvWeb.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "IHV_TRANSACTION")
public class IhvTransaction {

	@Id
	@Column(name = "transaction_id", nullable = false, unique = true)
	private String transId;

	@Column(name = "mobile_number")
	private String mobileNo;

	@Column(name = "amount")
	private String amount;

	@Column(name = "conv_fee")
	private String convFee;

	@Column(name = "gst_fee")
	private String gstFee;

	@Column(name = "total_amount")
	private String totalAmount;

	@Column(name = "payment_reference_id")
	private String paymentRefId;

	@Column(name = "transaction_status")
	private String transactionStatus;

	@Column(name = "initiated_on")
	private Date initiatedOn;

	@Column(name = "completed_on")
	private Date completedOn;

	@Column(name = "merchId")
	private String merchId;

	@Column(name = "remark")
	private String remark;
	
	public String getTransID() {
		return transId;
	}

	public void setTransID(String transID) {
		this.transId = transID;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getAmount() {
		return amount;
	}
 
	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getConvFee() {
		return convFee;
	}

	public void setConvFee(String convFee) {
		this.convFee = convFee;
	}

	public String getGstFee() {
		return gstFee;
	}

	public void setGstFee(String gstFee) {
		this.gstFee = gstFee;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getPaymentRefId() {
		return paymentRefId;
	}

	public void setPaymentRefId(String paymentRefId) {
		this.paymentRefId = paymentRefId;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public Date getInitiatedOn() {
		return initiatedOn;
	}

	public void setInitiatedOn(Date initiatedOn) {
		this.initiatedOn = initiatedOn;
	}

	public Date getCompletedOn() {
		return completedOn;
	}

	public void setCompletedOn(Date completedOn) {
		this.completedOn = completedOn;
	}

	public String getMerchantId() {
		return merchId;
	}

	public void setMerchantId(String merchId) {
		this.merchId = merchId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}