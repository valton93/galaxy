package com.kran.project.report.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kran.project.farmer.entities.FarmerDetails;
import com.kran.project.report.dao.CustomReportDetailsDAO;
import com.kran.project.report.service.CustomReportDetailsService;
import com.kran.project.user.dao.CustomUserDetailsDAO;
import com.kran.project.user.dto.FilterVO;
import com.kran.project.user.dto.UserBean;
import com.kran.project.user.entities.CullingCenterUsers;
import com.kran.project.user.entities.DistrictUsers;
import com.kran.project.user.entities.Users;
import com.kran.project.user.entities.repo.CullingCenterUsersRepository;
import com.kran.project.user.entities.repo.DistrictUsersRepository;
import com.kran.project.user.entities.repo.UsersRepository;
import com.kran.project.user.service.CustomUserDetailsService;

@Service
public class CustomReportDetailsServiceImpl implements CustomReportDetailsService {
	@Autowired
    CullingCenterUsersRepository cullingCenterUsersRepository;
	@Autowired
	DistrictUsersRepository districtUsersRepository;
	
	@Autowired
	CustomReportDetailsDAO customReportDetailsDAO;

	public List<FarmerDetails> getFarmerDetails(FilterVO filterVO) {
		return customReportDetailsDAO.getFarmerDetails(filterVO);
	}

}
