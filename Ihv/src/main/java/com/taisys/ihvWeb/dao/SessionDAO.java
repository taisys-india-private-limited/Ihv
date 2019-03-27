package com.taisys.ihvWeb.dao;

import com.taisys.ihvWeb.model.SessionInfo;

public interface SessionDAO {
	public boolean createSession(SessionInfo token);
	public boolean updateSession(SessionInfo token);
	public boolean deleteSession(String userId);
	public boolean deleteExpiredSessions();
	public SessionInfo getSessionByUser(String userId);
}
