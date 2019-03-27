package com.taisys.ihvWeb.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutoLogoutService {
	
	public static final Logger logger = Logger.getLogger(AutoLogoutService.class);

	@Autowired
	SessionService sessionService;

	@Scheduled(cron = "0 0/1 * * * *", zone = "Asia/Calcutta")
	public void checkStatus() {
		try {
			logger.info("Scheduler running to delete expired sessions.");
			sessionService.deleteExpiredSessions();
		} catch (Exception e) {
			e.printStackTrace();logger.error(e.getMessage(), e);
		}
	}
}