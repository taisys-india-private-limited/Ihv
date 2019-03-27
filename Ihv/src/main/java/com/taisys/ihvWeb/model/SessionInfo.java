package com.taisys.ihvWeb.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Session_Info")
public class SessionInfo {
	
	 @Id
	 @Column(name = "Id", nullable = false)
	 private String Id;
	 
	 @Column(name = "tokenId", nullable = true)
	 private String tokenId;

	 @Column(name = "userId", nullable = false)
	 private String userId;
	 
	 @Column(name = "ip", nullable = true)
	 private String ip;

	 @Column(name = "issuedAt", nullable = true)
	 private Date issuedAt;
	 
	 @Column(name = "expires", nullable = true)
	 private Date expires;
	 
	 @Column(name = "requestsThisMinute", nullable = true)
	 private int requestsThisMinute;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(Date issuedAt) {
		this.issuedAt = issuedAt;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public int getRequestsThisMinute() {
		return requestsThisMinute;
	}

	public void setRequestsThisMinute(int requestsThisMinute) {
		this.requestsThisMinute = requestsThisMinute;
	}
	 
}
