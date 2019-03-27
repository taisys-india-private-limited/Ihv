package com.taisys.ihvWeb.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

public class CORSFilter extends OncePerRequestFilter {

	@SuppressWarnings("unused")
	private static final Log LOG = LogFactory.getLog(CORSFilter.class);

	public static final Logger logger = Logger.getLogger(CORSFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			String[] allowDomain = { "http://122.176.40.215:4000", "122.176.40.215:4000", "http://192.168.1.100:4000,http://192.168.1.5:9000", "http://192.168.1.100:9001", "192.168.1.100:4000","http://192.168.1.100:4000" };
			String originHeader = request.getHeader("Origin");
			
			if (originHeader != null) {
				for (String domian : allowDomain) {
					if (originHeader.endsWith(domian)) {
						response.setHeader("Access-Control-Allow-Origin", originHeader);
						break;
					}
				}
			} else {
				response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
			}
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
			response.setHeader("Allow", "POST, GET, OPTIONS");
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setHeader("Access-Control-Expose-Headers", "NAT");
			response.setHeader("Access-Control-Allow-Headers",
					"Content-Type, Accept, X-Requested-With, remember-me, Authorization, X-Auth-Token");
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}