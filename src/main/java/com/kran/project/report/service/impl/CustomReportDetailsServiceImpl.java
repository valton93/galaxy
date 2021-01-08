package com.kran.project.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kran.project.report.dao.CustomReportDetailsDAO;
import com.kran.project.report.service.CustomReportDetailsService;
import com.kran.project.user.entities.repo.DistrictUsersRepository;

@Service
public class CustomReportDetailsServiceImpl implements CustomReportDetailsService {
	@Autowired
	DistrictUsersRepository districtUsersRepository;
	
	@Autowired
	CustomReportDetailsDAO customReportDetailsDAO;

//	public List<FarmerDetails> getFarmerDetails(FilterVO filterVO) {
//		return customReportDetailsDAO.getFarmerDetails(filterVO);
//	}

}
