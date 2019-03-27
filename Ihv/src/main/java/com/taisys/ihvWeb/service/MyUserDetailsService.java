package com.taisys.ihvWeb.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taisys.ihvWeb.dao.PortalUserDAOImpl;
import com.taisys.ihvWeb.jwt.JwtUser;

@Service("myUserDetailsService")
@Transactional
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private PortalUserDAOImpl userDao;

	public static final Logger logger = Logger.getLogger(MyUserDetailsService.class);

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		com.taisys.ihvWeb.model.PortalUser user = userDao.getUserByUserName(username);
		logger.info("Found User: " + user.getUserName());
		Set<String> roles = new HashSet<>();
		for (String role : user.getRoles()) {
			roles.add(role);
		}
		List<GrantedAuthority> authorities = buildUserAuthority(roles);
		return buildUserForAuthentication(user, authorities);
	}

	private JwtUser buildUserForAuthentication(com.taisys.ihvWeb.model.PortalUser user,
			List<GrantedAuthority> authorities) {
			return new JwtUser(user.getUserName(), user.getUserName(), user.getPassword(), authorities, true,
					user.getUpdateDate());
	}

	private List<GrantedAuthority> buildUserAuthority(Set<String> roles) {
		Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();
		// Build user's authorities
		for (String role : roles) {
			setAuths.add(new SimpleGrantedAuthority(role));
		}
		List<GrantedAuthority> Result = new ArrayList<GrantedAuthority>(setAuths);
		return Result;
	}

	public PortalUserDAOImpl getUserDAO() {
		return userDao;
	}

	public void setUserDAO(PortalUserDAOImpl userDao) {
		this.userDao = userDao;
	}

}