package com.taisys.ihvWeb.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SendSMS {

	private static final Logger logger = Logger.getLogger(SendSMS.class);

	@Value( "${notify.sms.server.name}") 
	private String NOTIFY_SMS_SERVER_NAME;

	@Value( "${notify.sms.server.port}") 
	private String NOTIFY_SMS_SERVER_PORT;

	private String NOTIFY_SMS_USER_NAME = "Taisystr";
 
	private String NOTIFY_SMS_USER_PASSWORD = "l0m9v8z7";

	public void sendSMSResponse(String content, String receiverMsisdn, String isSmpp, String drUrl, String messageId, String moUrl) {
		/*try {
			logger.info("Send SMS request parameter [content]: " + new String(content.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}*/
		logger.info("Send SMS request parameter [receiverMsisdn]: " + receiverMsisdn);
		logger.info("Send SMS request parameter [isSmpp]: " + isSmpp);
		logger.info("Send SMS request parameter [drUrl]: " + drUrl);
		logger.info("Send SMS request parameter [messageId]: " + messageId);
		System.out.println(content);
		try {
			String type = "0";
			if (isSmpp.equalsIgnoreCase("true")) {
				type = "9";
			} 
			logger.info("Try notify SMSC Gateway[" + moUrl + "].");
			new NotifyRemoteThread(NOTIFY_SMS_SERVER_NAME, Integer.parseInt(NOTIFY_SMS_SERVER_PORT), NOTIFY_SMS_USER_NAME, 
					NOTIFY_SMS_USER_PASSWORD, content, "1", type, receiverMsisdn, "TAISRO").start();
			logger.info("Notified SMSC Gateway[" + moUrl + "].");
		} catch (Exception e) {
			e.printStackTrace();logger.error(e.getMessage(), e);
		}
	}

	public static String unicodeEscaped(String str) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) < 0x10) {
				result.append("000" + Integer.toHexString(str.charAt(i)));
			} else if (str.charAt(i) < 0x100) {
				result.append("00" + Integer.toHexString(str.charAt(i)));
			} else if (str.charAt(i) < 0x1000) {
				result.append("0" + Integer.toHexString(str.charAt(i)));
			} else {
				result.append(Integer.toHexString(str.charAt(i)));
			}
		}
		return result.toString();
	}

}
