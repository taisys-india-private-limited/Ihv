package com.taisys.ihvWeb.util;

import java.util.HashMap;
//import java.util.Set;
import java.util.List;

public class Response {

	private boolean isSuccess;

	private String status;

	private String message;

	@SuppressWarnings("rawtypes")
	private HashMap resultObj;
	
	@SuppressWarnings("rawtypes")
	private List resultObjList;

//	@SuppressWarnings("rawtypes")
//	private Set result;
	
	public Response() {
	}

	public Response(boolean isSuccess, String status, String message){
		this.isSuccess = isSuccess;
		this.status = status;
		this.message = message;
	}

	public boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@SuppressWarnings("rawtypes")
	public HashMap getResultObj() {
		return resultObj;
	}

	@SuppressWarnings("rawtypes")
	public void setResultObj(HashMap resultObj) {
		this.resultObj = resultObj;
	}

	public List getResultObjList() {
		return resultObjList;
	}

	public void setResultObjList(List resultObjList) {
		this.resultObjList = resultObjList;
	}

	
//	public Set getResult() {
//		return result;
//	}
//
//	public void setResult(Set result) {
//		this.result = result;
//	}
	
}
