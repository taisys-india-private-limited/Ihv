package com.taisys.ihvWeb.service;

import com.taisys.ihvWeb.model.SessionInfo;

public interface SessionService {
	public boolean createSession(SessionInfo session);

	public boolean updateSession(SessionInfo token);

	public boolean deleteSession(String userId);

	public boolean deleteExpiredSessions();

	public SessionInfo getSessionByUser(String userId);
}
