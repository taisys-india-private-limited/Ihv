package com.taisys.ihvWeb.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailSenderTaisys {

	public static final Logger logger = Logger.getLogger(EmailSenderTaisys.class);

	@Value("${email.from}")
	private String from;

	@Value("${email.host}")
	private String host;

	public void sendmail(String emailID, String userName, String verificationToken, String text, String subject) {

		String to = emailID;

		Properties props = new Properties();
		// props.put("mail.smtp.auth", "true");
		// props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		// props.put("mail.smtp.port", "587");

		// Get the Session object.
		Session session = Session.getDefaultInstance(props);

		try {
			// Create a default MimeMessage object.
			Message message = new MimeMessage(session);
			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));
			// Set To: header field of the header.
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			// Set Subject: header field
			message.setSubject(subject);
			// Now set the actual message
			// message.setText(text);
			logger.info("Email text: " + text);
			message.setContent(text, "text/html; charset=utf-8");
			// Send message
			Transport.send(message);
			logger.info("Sent message successfully....");
		} catch (Exception e) {
			e.printStackTrace();logger.error(e.getMessage(), e);
		}
	}
}