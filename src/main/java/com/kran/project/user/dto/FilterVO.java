package com.kran.project.user.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class FilterVO {
	private String type;
	private Long user;
	
	private String district;
	private Long districtId;
	private String fromDateStr;
	private String applicationStatus;
	private String transactionStatus;
	private String collectionStatus;
	private String cullingStatus;
	private String toDateStr;
	
	private Date fromDate;
	private Date toDate;

	private Long epiCenter;

	List<String> statusList ;
	private String status;
	
	private Double liveWeight;
	
	private Double heartGirth;
	
	private Double length;
	
	private String weighStatus;
	
	private String seriesData;
	private String dates;
	private Date date;
	
	
	
}
