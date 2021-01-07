package com.kran.project.report.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.kran.project.farmer.entities.FarmerDetails;
import com.kran.project.user.dto.FilterVO;
import com.kran.project.user.dto.UserBean;

public interface CustomReportDetailsService  {

	public List<FarmerDetails> getFarmerDetails(FilterVO filterVO);
	

}