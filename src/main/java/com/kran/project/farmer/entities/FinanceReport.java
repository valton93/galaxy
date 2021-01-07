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
@Table(schema = "veterinaryassist", catalog = "veterinaryassist", name = "finance_report")
@Data
public class FinanceReport {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "no_of_beneficiery")
	private Integer noOfBeneficiery;
	
	@Column(name = "name")
	private String name;
	@Column(name = "status")
	private String status="O";//O-open C-closed
	
	@Column(name = "remark")
	private String remark;
	
	@Column(name = "delete_status")
	private String deleteStatus="N";
	
	@Column(name = "report_date")
	private Date reportDate;
	
	@Column(name = "generated_By")
	private Long generatedBy;
	
	@Column(name = "generated_On")
	private Date generatedOn;
	
	@Column(name = "success_count")
	private Long successCount;
	
	@Column(name = "reject_count")
	private Long rejectCount;
	
	@Column(name = "pending_count")
	private Long pendingCount;
	
	@Column(name = "file_name")
	private String fileName;

}