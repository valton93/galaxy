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
@Table(schema = "veterinaryassist", catalog = "veterinaryassist", name = "finance_report_upload")
@Data
public class FinanceUploadReport {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "report_id")
	private Long reportId;
	
	@Column(name = "file_name")
	private String fileName;
	
	@Column(name = "update_status")
	private String updateStatus="N";
	
	@Column(name = "upload_date")
	private Date uploadDate;
	
	@Column(name = "update_date")
	private Date updateDate;
	
	@Column(name = "delete_status")
	private String deleteStatus="N";

}