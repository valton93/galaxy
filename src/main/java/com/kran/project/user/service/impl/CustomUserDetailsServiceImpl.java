package com.kran.project.user.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kran.project.user.dao.CustomUserDetailsDAO;
import com.kran.project.user.dto.FilterVO;
import com.kran.project.user.dto.UserBean;
import com.kran.project.user.entities.DistrictUsers;
import com.kran.project.user.entities.Users;
import com.kran.project.user.entities.repo.DistrictUsersRepository;
import com.kran.project.user.entities.repo.UsersRepository;
import com.kran.project.user.service.CustomUserDetailsService;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
	@Autowired
    UsersRepository usersLoginRepository;
	@Autowired
	DistrictUsersRepository districtUsersRepository;
	
	@Autowired
	CustomUserDetailsDAO customUserDetailsDAO;

    @Override
    public UserDetails loadUserByUsername(String username)
    	   throws UsernameNotFoundException {
		String[] usernameAndDomain = StringUtils.split(username, "$$");

		if (usernameAndDomain.length == 2) {
			if (usernameAndDomain[1].equals("A")) {
				Optional<Users> optionalUser = usersLoginRepository.findByUserName(usernameAndDomain[0]);
				if (optionalUser.isPresent()) {
					Users user = optionalUser.get();
					return new User(username, user.getPassword(), new HashSet<GrantedAuthority>());
				}
			} else if (usernameAndDomain[1].equals("D")) {
				Optional<DistrictUsers> optionalUser
					= districtUsersRepository.findByUserName(usernameAndDomain[0]);
				if (optionalUser.isPresent()) {
					DistrictUsers user = optionalUser.get();
					return new User(username, user.getPassword(), new HashSet<GrantedAuthority>());
				}
			}
			 
		}
		
		throw new UsernameNotFoundException("Invalid Username or Password.");
	}

	public UserBean loadUserDetailsForUser(String username) {
		UserBean userBean = null;
    	Optional<Users> optionalUser 
    		= usersLoginRepository.findByUserName(username);
    	
		if (!optionalUser.isEmpty()) {
			userBean = new UserBean();

			Users user = optionalUser.get();
			userBean.setUserId(user.getId());
			userBean.setUserDomain("A");
			userBean.setUserOffice(user.getName());

			userBean.setAdministrator(user.getId());
			userBean.setAdministrationUser(user.getAdministrator());
			userBean.setFinanceManager(user.getFinanceManager());
			userBean.setName(user.getName());
			userBean.setPasswordReset("Y");
			userBean.setPasswordResetOn(new Date());
		}
    	
    	return userBean;
    }

	
	public UserBean loadUserDetailsForDistrict(String username) {
		UserBean userBean = null;
    	Optional<DistrictUsers> optionalUser 
    		= districtUsersRepository.findByUserName(username);
    	
		if (!optionalUser.isEmpty()) {
			userBean = new UserBean();

			DistrictUsers user = optionalUser.get();
			userBean.setUserId(user.getId());
			userBean.setUserDomain("D");
			userBean.setUserOffice(user.getDistrict().getDistrictName());
			userBean.setDistrict(user.getDistrict().getId());

			userBean.setName(user.getDistrict().getDistrictName());
			userBean.setPasswordReset("Y");
			userBean.setPasswordResetOn(new Date());
			userBean.setDVOUser(user.getDvoStatus());
			userBean.setDVODEOUser(user.getDvdeoStatus());
			userBean.setDCUser(user.getDcStatus());
			userBean.setDCDEOUser(user.getDcdeoStatus());

			
		}
    	
    	return userBean;
    }



}
