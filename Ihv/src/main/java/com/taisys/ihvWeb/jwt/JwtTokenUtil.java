package com.taisys.ihvWeb.jwt;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.taisys.ihvWeb.model.SessionInfo;
import com.taisys.ihvWeb.service.SessionService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = -3301605591108950415L;

	private static final String CLAIM_KEY_SUBJECT = "sub";
	private static final String CLAIM_KEY_AUDIENCE = "aud";
	private static final String CLAIM_KEY_ISSUEDAT = "iat";
	private static final String CLAIM_KEY_ISSUER = "iss";
	private static final String CLAIM_KEY_EXPIRES = "exp";
	private static final String CLAIM_KEY_JTI = "jti";
	private static final String CLAIM_KEY_ROLES = "sr";

	private static final String AUDIENCE_WEB = "web";

	@Value("My very confidential secret!")
	private String secret;

	@Value("600000")
	private Long expiration;

	@Autowired
	SessionService sessionService;

	public String getUsernameFromToken(String token) {
		String username;
		try {
			final Claims claims = getClaimsFromToken(token);
			username = claims.getSubject();
		} catch (Exception e) {
			username = null;
		}
		return username;
	}

	public Date getCreatedDateFromToken(String token) {
		Date created;
		try {
			final Claims claims = getClaimsFromToken(token);
			long millis = (long) claims.get(CLAIM_KEY_ISSUEDAT);
			created = new Date(millis);
		} catch (Exception e) {
			created = null;
		}
		return created;
	}

	public Date getExpirationDateFromToken(String token) {
		Date expiration;
		try {
			final Claims claims = getClaimsFromToken(token);
			long millis = (long) claims.get(CLAIM_KEY_EXPIRES);
			expiration = new Date(millis);
		} catch (Exception e) {
			expiration = null;
		}
		return expiration;
	}

	public String getAudienceFromToken(String token) {
		String audience;
		try {
			final Claims claims = getClaimsFromToken(token);
			audience = (String) claims.getAudience();
		} catch (Exception e) {
			audience = null;
		}
		return audience;
	}

	public String getIssuerFromToken(String token) {
		String audience;
		try {
			final Claims claims = getClaimsFromToken(token);
			audience = (String) claims.getIssuer();
		} catch (Exception e) {
			audience = null;
		}
		return audience;
	}

	public String getJtiFromToken(String token) {
		String audience;
		try {
			final Claims claims = getClaimsFromToken(token);
			audience = String.valueOf(claims.get(CLAIM_KEY_JTI));
		} catch (Exception e) {
			audience = null;
		}
		return audience;
	}

	private Claims getClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey("My very confidential secret!".getBytes("UTF-8")).parseClaimsJws(token)
					.getBody();
		} catch (Exception e) {
			claims = null;
		}
		return claims;
	}

	private Date generateExpirationDate() {
		return new Date(System.currentTimeMillis() + expiration);
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
		return (lastPasswordReset != null && created.before(lastPasswordReset));
	}

	private Boolean isTokenUnused(String token) {
		final String subject = getUsernameFromToken(token);
		final String tokenId = getJtiFromToken(token);
		SessionInfo session = sessionService.getSessionByUser(subject);
		boolean unused = session.getTokenId().equals(tokenId);
		Date current = new Date();
		if (session.getIssuedAt() != null && session.getIssuedAt().getMonth() == current.getMonth()
				&& session.getIssuedAt().getDate() == current.getDate()
				&& session.getIssuedAt().getHours() == current.getHours()
				&& session.getIssuedAt().getMinutes() == current.getMinutes()) {
			session.setRequestsThisMinute(session.getRequestsThisMinute() + 1);
		} else {
			session.setRequestsThisMinute(0);
		}
		if (unused && session.getRequestsThisMinute() < 150) {
			session.setIssuedAt(null);
			sessionService.updateSession(session);
		} else {
			sessionService.deleteSession(session.getUserId());
		}
		return unused;
	}

	public String generateToken(UserDetails userDetails) {
		String jti = generateJti();
		Date created = new Date();
		Date expires = generateExpirationDate();
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_KEY_SUBJECT, userDetails.getUsername());
		claims.put(CLAIM_KEY_AUDIENCE, AUDIENCE_WEB);
		claims.put(CLAIM_KEY_ISSUEDAT, created);
		claims.put(CLAIM_KEY_EXPIRES, expires);
		claims.put(CLAIM_KEY_ISSUER, "yesbank");
		claims.put(CLAIM_KEY_JTI, jti);
		Iterator<? extends GrantedAuthority> itr = userDetails.getAuthorities().iterator();
		List<String> roles = new ArrayList<String>();
		while (itr.hasNext()) {
			GrantedAuthority ath = itr.next();
			if (ath.getAuthority().equals("YESBANKADMIN")) {
				roles.add("ABY");
			} else if (ath.getAuthority().equals("MANAGEBANKSMAKER")) {
				roles.add("MBM");
			} else if (ath.getAuthority().equals("MANAGEBANKSCHECKER")) {
				roles.add("CBM");
			} else if (ath.getAuthority().equals("KITMANAGER")) {
				roles.add("MKI");
			} else if (ath.getAuthority().equals("MISREPORTMANAGER")) {
				roles.add("MPM");
			} else if (ath.getAuthority().equals("QUERYMANAGER")) {
				roles.add("MQ");
			} else if (ath.getAuthority().equals("RECONSYSTEMMANAGER")) {
				roles.add("MR");
			} else if (ath.getAuthority().equals("MANAGEUSERMAKER")) {
				roles.add("MUM");
			} else if (ath.getAuthority().equals("MANAGEUSERCHECKER")) {
				roles.add("CUM");
			} else if (ath.getAuthority().equals("KYCMANAGER")) {
				roles.add("MK");
			} else {
				roles.add(ath.getAuthority());
			}
		}
		claims.put(CLAIM_KEY_ROLES, roles.toArray());

		SessionInfo session = sessionService.getSessionByUser(userDetails.getUsername());
		session.setTokenId(jti);
		session.setIssuedAt(created);
		session.setExpires(expires);
		sessionService.updateSession(session);

		return generateToken(claims);
	}

	private String generateToken(Map<String, Object> claims) {
		try {
			String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, secret.getBytes("UTF-8"))
					.compact();
			String[] tokenParts = token.split("\\.");
			String localCustomSign = encode("Secret Key!", tokenParts[1]);
			return localCustomSign.substring(0, localCustomSign.length() - 1) + "." + token;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
		final Date created = getCreatedDateFromToken(token);
		return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset) && (!isTokenExpired(token));
	}

	public String refreshToken(String token) {
		String refreshedToken;
		try {
			final Claims claims = getClaimsFromToken(token);
			claims.put(CLAIM_KEY_ISSUEDAT, new Date());
			refreshedToken = generateToken(claims);
		} catch (Exception e) {
			refreshedToken = null;
		}
		return refreshedToken;
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		String[] tokenParts = token.split("\\.");
		String customSign = tokenParts[0];
		token = tokenParts[1] + "." + tokenParts[2] + "." + tokenParts[3];
		boolean valid = false;
		String localCustomSign = encode("Secret Key!", tokenParts[2]);
		if (localCustomSign.substring(0, localCustomSign.length() - 1).equals(customSign)) {
			JwtUser user = (JwtUser) userDetails;
			final String username = getUsernameFromToken(token);
			final Date created = getCreatedDateFromToken(token);
			valid = (username.equalsIgnoreCase(user.getUsername()) && !isTokenExpired(token)
					&& !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate())
					&& isTokenUnused(token));
		}
		return valid;
	}

	public String encode(String key, String data) {
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
			sha256_HMAC.init(secret_key);

			return Base64.getUrlEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String generateJti() {
		Random r = new Random();
		int Low = 1000000;
		int High = 100000000;
		int Result = r.nextInt(High - Low) + Low;
		return "id" + Result;
	}
}