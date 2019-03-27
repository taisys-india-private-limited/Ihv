package com.taisys.ihvWeb.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;

public class IdGenerator {

	public static final Logger logger = Logger.getLogger(IdGenerator.class);

	public String generateCustomerID(String prefix) throws Exception {

		int maxPrefixLen = 4;
		int maxTimeKeyLen = 14;
		String idKeyDateFormat = "ddMMyyHHmm";
		return createId(prefix, maxPrefixLen, maxTimeKeyLen, idKeyDateFormat);
	}

	public String generateOtpId(String prefix) throws Exception {
		int maxPrefixLen = 4;
		int maxTimeKeyLen = 20;
		String idKeyDateFormat = "ddMMyyyyHHmmssSSS";
		return createId(prefix, maxPrefixLen, maxTimeKeyLen, idKeyDateFormat);
	}

	public String generateId(String prefix) throws Exception {
		int maxPrefixLen = 3;
		int maxTimeKeyLen = 22;
		String idKeyDateFormat = "ddMMyyyyHHmmssSSS";
		return createId(prefix, maxPrefixLen, maxTimeKeyLen, idKeyDateFormat);
	}

	private String createId(String prefix, int maxPrefixLen, int maxTimeKeyLen, String idKeyDateFormat)
			throws Exception {
		SimpleDateFormat keyDateFormat = new SimpleDateFormat(idKeyDateFormat);
		if (prefix != null && prefix.length() > maxPrefixLen) {
			throw new Exception("Prefix length can not be longer than " + maxPrefixLen + ". Prefix : " + prefix);
		}
		String s = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
		Random random = new Random();
		StringBuilder sb = new StringBuilder(keyDateFormat.format(new Date()));
		logger.info("sb.toString() : " + sb.toString());
		int otherLen = maxTimeKeyLen - sb.length();
		for (int i = 0; i < otherLen; i++) {
			sb.append(s.charAt(random.nextInt(s.length())));
		}
		sb.insert(0, (prefix == null ? "" : prefix));
		return sb.toString();
	}

}
