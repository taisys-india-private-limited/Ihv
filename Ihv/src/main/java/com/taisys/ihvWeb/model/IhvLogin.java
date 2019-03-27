package com.taisys.ihvWeb.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.taisys.ihvWeb.util.EncryptAndDecrypt;

import javax.persistence.Id;


@Entity
@Table(name = "IHV_Login")
public class IhvLogin {

	@Id
	@Column(name = "id", nullable = false)
	private String id;

	@Column(name = "email")
	private String email;

	@Column(name = "mobileNo")
	private String mobileNo;

	
	@Column(name = "password")
	private String password;

	@Column(name = "login_Time")
	private Date login_Time;
	
	@Column(name = "status")
	private String status;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getEmailId() {
		return email;
	}

	public void setEmailId(String email) {
		this.email = email;
	}
	
	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getPassword() throws NumberFormatException, Exception {
		return EncryptAndDecrypt.decrypt(password);
	} 

	public void setPassword(String password) throws NumberFormatException, Exception {
		this.password = EncryptAndDecrypt.encrypt(password);
	
	}
	
	public Date getloginTime() {
		return login_Time;
	}

	public void setloginTime(Date login_Time) {
		this.login_Time = login_Time;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}