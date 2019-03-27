package com.taisys.ihvWeb.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class GenerateKeys {

	private KeyPairGenerator keyGen;
	private KeyPair pair;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	//private static String RSA_CONFIGURATION = "RSA/None/PKCS1Padding";
	//private static String RSA_PROVIDER = "BC";

	public String createStringFromPublicKey(Key publicKey) throws Exception {
	    X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
	    return new String(Base64.encodeBase64(x509EncodedKeySpec.getEncoded()), "UTF-8");
	}
	public String createStringFromPrivateKey(Key privateKey) throws Exception {
		PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
	    return new String(Base64.encodeBase64(encodedKeySpec.getEncoded()), "UTF-8");
	}
	
	public void createKeys() throws NoSuchAlgorithmException, NoSuchProviderException{
		this.keyGen = KeyPairGenerator.getInstance("RSA");
		this.keyGen.initialize(2048);
		this.pair = this.keyGen.generateKeyPair();
		this.privateKey = pair.getPrivate();
		this.publicKey = pair.getPublic();
	}

	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}

	public PublicKey getPublicKey() {
		return this.publicKey;
	}

	public static PrivateKey createPrivateKeyFromString(String privateKeyString) throws Exception {
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyString.getBytes("UTF-8"))));
	}

//	public String decryptRsa(Key key, String base64cypherText) throws Exception {
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//	    Cipher c = Cipher.getInstance(RSA_CONFIGURATION, RSA_PROVIDER);
//	    c.init(Cipher.DECRYPT_MODE, key, new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256,
//	            PSource.PSpecified.DEFAULT));
//	    byte[] decodedBytes = c.doFinal(Base64.decodeBase64(base64cypherText.getBytes("UTF-8")));
//	    String clearText = new String(decodedBytes, "UTF-8");
//	    return clearText;
//	}
	
	public String decryptRsa(Key key, String base64cypherText)
			throws InvalidKeyException, UnsupportedEncodingException,
			IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(Base64.decodeBase64(base64cypherText)), "UTF-8");
	}

}