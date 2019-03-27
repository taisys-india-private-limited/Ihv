package com.taisys.ihvWeb.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.taisys.ihvWeb.util.EncryptAndDecrypt;

@Entity
@Table(name = "IHV_OTP")
public class IhvOTP {

	@Id
	@Column(name = "id", nullable = false)
	private String id;

	@Column(name = "mobileNo", nullable = false)
	private String mobileNo;

	@Column(name = "otp", nullable = false)
	private String otp;

	@Column(name = "createTime", nullable = false)
	private Date createTime;
	
	@Column(name = "status", nullable = false)
	private String status;

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOtp() throws Exception {
		return EncryptAndDecrypt.decrypt(otp);
	}

	public void setOtp(String otp) throws Exception {
		this.otp = EncryptAndDecrypt.encrypt(otp);
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}