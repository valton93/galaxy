package com.kran.project.user.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.kran.project.farmer.entities.FarmerDetails;
import com.kran.project.user.dto.FilterVO;
import com.kran.project.user.dto.UserBean;

public interface CustomUserDetailsService extends UserDetailsService {

	UserBean loadUserDetailsForUser(String username);

	UserBean loadUserDetailsForDistrict(String username);

	List<FarmerDetails> getMigrantApplicantDetails(FilterVO filterVO);
	
	UserBean loadUserDetailsForCullingCenter(String username);
	
	UserBean loadUserDetailsForFinanceUser(String username);
	

}