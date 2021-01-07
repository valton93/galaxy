package com.kran.project.user.dto;

import java.util.Date;

import lombok.Data;

@Data
public class UserBean {
	private Long userId;
	private String userDomain;
	private String userOffice;
	private Long district;
	
	private String name;
	
	private Long administrator;
	
	private Long screeningCenterUser;
	private Long screeningCenter;
	
	private Long cullingCenterUser;
	private Long cullingCenter;

	private String passwordReset = "N";
	private Date passwordResetOn;
	
	private String currentpassword;
	private String newpassword;
	private String otp;
	
	/*** PRIVILEGES ***/
	private String administrationUser = "N";
	private String DVOUser = "N";
	private String DVODEOUser = "N";
	private String DCUser = "N";
	private String DCDEOUser = "N";
	private String CullingOfficer = "N";
	private String CullingOfficerDEO = "N";
	
	private String financeManager = "N";
	private String dataEntryOperator = "N";
}