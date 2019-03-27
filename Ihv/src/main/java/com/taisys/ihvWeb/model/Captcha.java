package com.taisys.ihvWeb.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "captcha")
public class Captcha {
	
	@Id
	 @Column(name = "userId", nullable = false)
	 private String userId;

	 @Column(name = "captchaText", nullable = false)
	 private String captchaText;
	 
	 @Column(name = "salt")
	 private String salt;

	 @Lob @Basic(fetch = FetchType.LAZY)
	 @Column(name = "captchaImg", nullable = false)
	 private byte[] captchaImg;
	 
	 @Column(name = "createdOn", nullable = false)
	 private Date createdOn;
	 
	 @Column(name = "updatedOn", nullable = true)
	 private Date updatedOn;

	 public String getUserId() {
	  return userId;
	 }

	 public void setUserId(String userId) {
	  this.userId = userId;
	 }

	 public String getCaptchaText() {
	  return captchaText;
	 }

	 public void setCaptchaText(String captchaText) {
	  this.captchaText = captchaText;
	 }

	 public String getSalt() {
	  return salt;
	 }

	 public void setSalt(String salt) {
	  this.salt = salt;
	 }

	 public byte[] getCaptchaImg() {
	  return captchaImg;
	 }

	 public void setCaptchaImg(byte[] captchaImg) {
	  this.captchaImg = captchaImg;
	 }

	 public Date getCreatedOn() {
	  return createdOn;
	 }

	 public void setCreatedOn(Date createdOn) {
	  this.createdOn = createdOn;
	 }

	 public Date getUpdatedOn() {
	  return updatedOn;
	 }

	 public void setUpdatedOn(Date updatedOn) {
	  this.updatedOn = updatedOn;
	 }

}
