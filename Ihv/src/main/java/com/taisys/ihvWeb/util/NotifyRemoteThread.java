package com.taisys.ihvWeb.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.log4j.Logger;


/**
 * Created with IntelliJ IDEA.
 * User: Benny Lo
 * Date: 2015/3/11
 * Time: ä¸‹å�ˆ 05:53
 */
public class NotifyRemoteThread extends Thread {
	protected String username; // password that is to be used along with username
	protected String password; // Message content that is to be transmitted
	protected String message;
	/**
	 * * What type of the message that is to be sent
	 * <ul>
	 * <li>0:means plain * text</li>
	 * <li>1:means flash</li>
	 * <li>2:means Unicode (Message content * should be in Hex)</li>
	 * <li>6:means Unicode Flash (Message content should * be in Hex)</li>
	 * </ul>
	 */
	protected String type;
	/**
	 * * Require DLR or not
	 * <ul>
	 * <li>0:means DLR is not Required</li>
	 * <li>1:means * DLR is Required</li>
	 * </ul>
	 */
	protected String dlr;
	/**
	 * * Destinations to which message is to be sent For submitting more than
	 * one destination at once destinations should be comma separated Like *
	 * 91999000123,91999000124
	 */
	protected String destination; // Sender Id to be used for submitting the message
	protected String source; // To what server you need to connect to for submission
	protected String server; // Port that is to be used like 8080 or 8000
	protected int port;

    private static final Logger logger = Logger.getLogger(NotifyRemoteThread.class);

    public NotifyRemoteThread(String server,int port, String username, String password,
			String message, String dlr, String type, String destination,
			String source) {
    	this.username = username;
		this.password = password;
		this.message = message;
		this.dlr = dlr;
		this.type = type;
		this.destination = destination;
		this.source = source;
		this.server = server;
		this.port = port;
    }


    @Override
    public void run() {
        logger.info("Start .");
        try {
            Thread.sleep(500);
            submitMessage( server,port,  username,  password,
        			 message,  dlr,  type,  destination,
        			 source);

        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("unused")
	private void submitMessage(String server, int port,String username, String password,
			String message, String dlr, String type, String destination,
			String source) {
		HttpURLConnection httpConnection = null;
		try {
			URL sendUrl = new URL("https://" + this.server + ":" + this.port
					+ "/bulksms/bulksms");
			HostnameVerifier hostVerfier = new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					return true;
				}
			};
			httpConnection = (java.net.HttpURLConnection) sendUrl
					.openConnection();
			httpConnection.setRequestMethod("POST");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			httpConnection.setUseCaches(false);
			DataOutputStream dataStreamToServer = new DataOutputStream(
					httpConnection.getOutputStream());
			dataStreamToServer.writeBytes("username="
					+ URLEncoder.encode(username, "UTF-8") + "&password="
					+ URLEncoder.encode(password, "UTF-8") + "&type="
					+ URLEncoder.encode(type, "UTF-8") + "&dlr="
					+ URLEncoder.encode(dlr, "UTF-8") + "&destination="
					+ URLEncoder.encode(destination, "UTF-8") + "&source="
					+ URLEncoder.encode(source, "UTF-8") + "&message="
					+ URLEncoder.encode(message, "UTF-8"));
			dataStreamToServer.flush();
			dataStreamToServer.close();
			BufferedReader dataStreamFromUrl = new BufferedReader(
					new InputStreamReader(httpConnection.getInputStream()));
			String dataFromUrl = "", dataBuffer = "";
			while ((dataBuffer = dataStreamFromUrl.readLine()) != null) {
				dataFromUrl += dataBuffer;
			}
			dataStreamFromUrl.close();
			logger.info("Response: " + dataFromUrl);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}
	}

	@SuppressWarnings("unused")
	private static StringBuffer convertToUnicode(String regText) {
		char[] chars = regText.toCharArray();
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			String iniHexString = Integer.toHexString((int) chars[i]);
			if (iniHexString.length() == 1) {
				iniHexString = "000" + iniHexString;
			} else if (iniHexString.length() == 2) {
				iniHexString = "00" + iniHexString;
			} else if (iniHexString.length() == 3) {
				iniHexString = "0" + iniHexString;
			}
			hexString.append(iniHexString);
		}
		logger.info("hexString : "+hexString);
		return hexString;
	}

}
