package com.kran.project.farmer.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(schema = "veterinaryassist", catalog = "veterinaryassist", name = "finance_report_details")
@Data
public class FinanceReportDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "farmer_id")
	private Long farmerId;
	
	@Column(name = "report_id")
	private Long reportId;
	
	@Column(name = "tracking_id")
	private String trackingId;
	
	@Column(name = "mobile")
	private String mobile;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "ifsc_code")
	private String ifscCode;
	
	@Column(name = "account_number")
	private String accountNumber;
	
	@Column(name = "amount")
	private Long amount;
	
	@Column(name = "status")
	private String status;//N transactin not done F-transaction rejected S-transaction Completed
	
	@Column(name = "remark")
	private String remark;
	
	@Column(name = "entry_On")
	private Date entryOn;
	
	@Column(name = "delete_status")
	private String deleteStatus="N";

}