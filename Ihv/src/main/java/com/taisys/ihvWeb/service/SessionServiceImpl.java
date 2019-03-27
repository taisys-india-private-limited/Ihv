package com.taisys.ihvWeb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taisys.ihvWeb.dao.SessionDAO;
import com.taisys.ihvWeb.model.SessionInfo;

@Service("TokenService")
@Transactional
public class SessionServiceImpl implements SessionService {

	@Autowired
	private SessionDAO dao;

	@Override
	public boolean createSession(SessionInfo session){
		return dao.createSession(session);
	}
	
	@Override
	public boolean updateSession(SessionInfo session){
		return dao.updateSession(session);
	}
	
	public boolean deleteSession(String userId){
		return dao.deleteSession(userId);
	}
	
	public boolean deleteExpiredSessions(){
		return dao.deleteExpiredSessions();
	}
	
	public SessionInfo getSessionByUser(String userId){
		return dao.getSessionByUser(userId);
	}

}
