package com.kran.project.farmer.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kran.project.farmer.entities.FarmerDetails;
import com.kran.project.farmer.entities.FinanceReport;
import com.kran.project.farmer.entities.FinanceReportDetails;
import com.kran.project.farmer.entities.FinanceUploadReport;

import lombok.Data;

@Data
public class FinanceReportForm {
	private FinanceReport financeReport;
	private List<FinanceReportDetails> financeReportDetails;
	private List<FarmerDetails> details;
	private MultipartFile [] files;
	private FinanceUploadReport financeUploadReport;
	
	public FinanceReportForm() {
		super();
	}

	public FinanceReportForm(FinanceReport fnanceReport) {
		super();
		this.financeReport = fnanceReport;
	}

	
}