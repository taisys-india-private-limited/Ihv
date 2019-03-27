package com.taisys.ihvWeb.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PortalUser")
public class PortalUser {
	
	@Id
	 @Column(name = "User_ID", nullable = false, unique = true)
	 private String userID;

	 @Column(name = "User_Name")
	 private String userName;

	 @Column(name = "Bank_Name")
	 private String bankName;

	 @Column(name = "Email_Token")
	 private String emailToken;
	 
	 @Column(name = "User_Status")
	 private String userStatus;

	 @Column(name = "Suspend_Reason")
	 private String suspendReason;

	 @Column(name = "Password")
	 private String password;

	 @Column(name = "Last_Login")
	 private Date lastLogin;

	 @Column(name = "Password_Fail_Count")
	 private Integer passwordFailCount;

	 @Column(name = "Password_Reset_Count")
	 private Integer passwordResetCount;

	 @Column(name = "Password_Last_Update_Date")
	 private Date passwordLastUpdateDate;

	 @Column(name = "Create_User")
	 private String createUser;

	 @Column(name = "Create_Date")
	 private Date createDate;

	 @Column(name = "Update_User")
	 private String updateUser;

	 @Column(name = "Update_Date")
	 private Date updateDate;

	 @Column
	 @ElementCollection(targetClass=String.class, fetch = FetchType.EAGER)
	 private List<String> roles = new ArrayList<String>();

	 @Column(name = "Bank_ID")
	 private String bankCode;

	 @Column(name = "Bank_Branch_Id")
	 private String bankBranchId;
	 
	 @Column(name = "Bank_Branch_Name")
	 private String bankBranchName;

	 @Column(name = "Bank_Branch_Address")
	 private String bankBranchAddress;

	 @Column(name = "Bank_Branch_City")
	 private String bankBranchCity;

	 @Column(name = "Bank_Branch_District")
	 private String bankBranchDistrict;

	 @Column(name = "Bank_Branch_State")
	 private String bankBranchState;

	 @Column(name = "EMPLOYEE_ID", nullable = false) 
	 private String employeeID;

	 @Column(name = "Employee_First_Name")
	 private String employeeFirstName;

	 @Column(name = "Employee_Last_Name")
	 private String employeeLastName;

	 @Column(name = "Employee_Mobile_Number")
	 private String employeeMobileNumber;

	 public String getUserID() {
	  return userID;
	 }

	 public void setUserID(String userID) {
	  this.userID = userID;
	 }

	 public String getUserName() {
	  return userName;
	 }

	 public void setUserName(String userName) {
	  this.userName = userName;
	 }

	 public String getBankName() {
	  return bankName;
	 }

	 public void setBankName(String bankName) {
	  this.bankName = bankName;
	 }

	 public String getUserStatus() {
	  return userStatus;
	 }

	 public void setUserStatus(String userStatus) {
	  this.userStatus = userStatus;
	 }

	 public String getSuspendReason() {
	  return suspendReason;
	 }

	 public void setSuspendReason(String suspendReason) {
	  this.suspendReason = suspendReason;
	 }

	 public String getPassword() {
	  return password;
	 }

	 public void setPassword(String password) {
	  this.password = password;
	 }

	 public Date getLastLogin() {
	  return lastLogin;
	 }

	 public void setLastLogin(Date lastLogin) {
	  this.lastLogin = lastLogin;
	 }

	 public Integer getPasswordFailCount() {
	  return passwordFailCount;
	 }

	 public void setPasswordFailCount(Integer passwordFailCount) {
	  this.passwordFailCount = passwordFailCount;
	 }

	 public Integer getPasswordResetCount() {
	  return passwordResetCount;
	 }

	 public void setPasswordResetCount(Integer passwordResetCount) {
	  this.passwordResetCount = passwordResetCount;
	 }

	 public Date getPasswordLastUpdateDate() {
	  return passwordLastUpdateDate;
	 }

	 public void setPasswordLastUpdateDate(Date passwordLastUpdateDate) {
	  this.passwordLastUpdateDate = passwordLastUpdateDate;
	 }

	 public String getCreateUser() {
	  return createUser;
	 }

	 public void setCreateUser(String createUser) {
	  this.createUser = createUser;
	 }

	 public Date getCreateDate() {
	  return createDate;
	 }

	 public void setCreateDate(Date createDate) {
	  this.createDate = createDate;
	 }

	 public String getUpdateUser() {
	  return updateUser;
	 }

	 public void setUpdateUser(String updateUser) {
	  this.updateUser = updateUser;
	 }

	 public Date getUpdateDate() {
	  return updateDate;
	 }

	 public void setUpdateDate(Date updateDate) {
	  this.updateDate = updateDate;
	 }

	 public List<String> getRoles() {
	  return roles;
	 }

	 public void setRoles(List<String> roles) {
	  this.roles = roles;
	 }

	 public String getBankCode() {
	  return bankCode;
	 }

	 public void setBankCode(String bankID) {
	  this.bankCode = bankID;
	 }

	 public String getBankBranchName() {
	  return bankBranchName;
	 }

	 public void setBankBranchName(String bankBranchName) {
	  this.bankBranchName = bankBranchName;
	 }

	 public String getBankBranchAddress() {
	  return bankBranchAddress;
	 }

	 public void setBankBranchAddress(String bankBranchAddress) {
	  this.bankBranchAddress = bankBranchAddress;
	 }
	 
	 public String getBankBranchId() {
	  return bankBranchId;
	 }

	 public void setBankBranchId(String bankBranchId) {
	  this.bankBranchId = bankBranchId;
	 }

	 public String getBankBranchCity() {
	  return bankBranchCity;
	 }

	 public void setBankBranchCity(String bankBranchCity) {
	  this.bankBranchCity = bankBranchCity;
	 }

	 public String getBankBranchDistrict() {
	  return bankBranchDistrict;
	 }

	 public void setBankBranchDistrict(String bankBranchDistrict) {
	  this.bankBranchDistrict = bankBranchDistrict;
	 }

	 public String getBankBranchState() {
	  return bankBranchState;
	 }

	 public void setBankBranchState(String bankBranchState) {
	  this.bankBranchState = bankBranchState;
	 }

	 public String getEmployeeID() {
	  return employeeID;
	 }

	 public void setEmployeeID(String employeeID) {
	  this.employeeID = employeeID;
	 }

	 public String getEmployeeFirstName() {
	  return employeeFirstName;
	 }

	 public void setEmployeeFirstName(String employeeFirstName) {
	  this.employeeFirstName = employeeFirstName;
	 }

	 public String getEmployeeLastName() {
	  return employeeLastName;
	 }

	 public void setEmployeeLastName(String employeeLastName) {
	  this.employeeLastName = employeeLastName;
	 }

	 public String getEmployeeMobileNumber() {
	  return employeeMobileNumber;
	 }

	 public void setEmployeeMobileNumber(String employeeMobileNumber) {
	  this.employeeMobileNumber = employeeMobileNumber;
	 }
	 
	 public String getEmailToken() {
	  return emailToken;
	 }

	 public void setEmailToken(String emailToken) {
	  this.emailToken = emailToken;
	 }

}
