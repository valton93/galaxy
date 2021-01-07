package com.kran.project.report.dao;

import java.util.List;

import com.kran.project.farmer.entities.FarmerDetails;
import com.kran.project.user.dto.FilterVO;

public interface CustomReportDetailsDAO {

	List<FarmerDetails> getFarmerDetails(FilterVO filterVO);

}
