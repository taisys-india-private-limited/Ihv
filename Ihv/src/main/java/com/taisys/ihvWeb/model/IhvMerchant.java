package com.taisys.ihvWeb.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.taisys.ihvWeb.util.EncryptAndDecrypt;

@Entity
@Table(name = "IHV_MERCHANT")
public class IhvMerchant {

	@Id
	@Column(name = "MerchID", nullable = false, unique = true)
	private String MerchID;

	@Column(name = "mobileNo")
	private String mobileNo;

	@Column(name = "email")
	private String email;

	@Column(name = "Status")
	private String status;

	@Column(name = "registration_date")
	private Date registrationDate;

	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "firstName")
	private String firstName;


	@Column(name = "password")
	private String password;

	@Column(name = "type")
	private String type;

	@Column(name = "dob")
	private Date dob;

	@Column(name = "accountNo")
	private String accountNo;

	@Column(name = "ifsc")
	private String ifsc;

	@Column(name = "accountType")
	private String accountType;
	
	@Column(name = "intitutionName")
	private String intitutionName;
	
	@Column(name = "gstNo")
	private String gstNo;
	
	
	public String getIntitutionName() {
		return intitutionName;
	}

	public void setIntitutionName(String intitutionName) {
		this.intitutionName = intitutionName;
	}

	public String getGstNo() {
		return gstNo;
	}

	public void setGstNo(String gstNo) {
		this.gstNo = gstNo;
	}

	public String getMerchID() {
		return MerchID;
	}

	public void setMerchID(String merchID) {
		MerchID = merchID;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getFirstName() {
		return firstName; 
	}
	
	public void setFirstName(String firstName){
		this.firstName = firstName;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
	
	public String getPassword() throws NumberFormatException, Exception {
		return EncryptAndDecrypt.decrypt(password);
	} 

	public void setPassword(String password) throws NumberFormatException, Exception {
		this.password = EncryptAndDecrypt.encrypt(password);
	
	}
	
	public String getType(){
		return type;
	}
	
	public void setType(String type){
		 this.type = type;
	}
	
	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}
	
	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getAccountType() {
		return accountType;
	}
	
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	
	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}
}