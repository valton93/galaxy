package com.kran.project.farmer.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.persistence.Transient;

import com.kran.project.user.entities.CullingCenters;
import com.kran.project.user.entities.EpiCenterSetup;

import lombok.Data;

@Entity
@Table(schema = "veterinaryassist", catalog = "veterinaryassist", name = "farmer_details")
@Data
public class FarmerDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "tracking_id")
	private String trackingId;
	@Column(name = "unique_id")
	private String uniqueId;

	@Column(name = "name")
	private String name;
	@Column(name = "gender")
	private String gender = "0";
	@Column(name = "age")
	private Integer age;
	@Column(name = "mobile")
	private String mobile;

	@ManyToOne
	@JoinColumn(name = "native_district")
	private DistrictSetup nativeDistrict;
	@ManyToOne
	@JoinColumn(name = "epi_center")
	private EpiCenterSetup epiCenter;
	@Column(name = "native_pincode")
	private Long nativePincode;
//	@ManyToOne
//	@JoinColumn(name = "native_police")
//	private PoliceStationSetup nativePolice;
	@Column(name = "native_circle")
	private String nativeCircle; 
	@Column(name = "native_block")
	private String nativeBlock; 
	@Column(name = "native_area")
	private String nativeArea = "R";
	@Column(name = "native_city")
	private String nativeCity;
	@Column(name = "native_ward")
	private String nativeWard;
	@Column(name = "native_village")
	private String nativeVillage;
	@Column(name = "native_panchayat")
	private String nativePanchayat;
	@Column(name = "native_address")
	private String nativeAddress;
	
	@Column(name = "account_number")
	private String accountNumber;
	@Column(name = "ifsc_code")
	private String ifscCode;
	
	@Column(name = "no_of_animal")
	private Integer noOfAnimal;
	
	@Column(name = "amount")
	private Long amount;
	
	
	@Column(name = "submit_by")
	private Long submitBy;
	@Column(name = "submit_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date submitOn;
	
	@Column(name = "verified_on")
	private Date verifiedOn;
	@Column(name = "verified_by")
	private Long verifiedBy;
	
	@Column(name = "accepted_by")
	private Long acceptedBy;
	@Column(name = "accepted_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date acceptedOn;
	
	@Column(name = "remark")
	private String remark;
	
	@Column(name = "verification_remark")
	private String verificationRemark;
	
	@Column(name = "accept_remark")
	private String acceptRemark;
	
	@Column(name = "modified_By")
	private Long modifiedBy;
	@Column(name = "modified_On")
	private Date modifiedOn;
	
	@Column(name = "pickup_scheduled_by")
	private Long pickupScheduledBy;
	
	@Column(name = "pickup_scheduled_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date pickupScheduledOn;
	
	@Column(name = "fund_transfer_init_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fundTransferInitOn;
	
	@Column(name = "fund_transfer_init_by")
	private Long fundTransferInitBy;
	
	@Column(name = "fund_transfer_status")
	private String fundTransferStatus="N";
	
	@Column(name = "fund_transfer_reject_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fundTransferRejectOn;
	
	@Column(name = "fund_transfer_reject_by")
	private Long fundTransferRejectBy;
	
	@Column(name = "fund_transfer_reject_remark")
	private String fundTransferRejectRemark;
	
	
	@Column(name = "fund_transfer_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fundTransferOn;
	
	@Column(name = "fund_transfer_by")
	private Long fundTransferBy;
	
	@Column(name = "collected_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date collectedOn;
	
	@Column(name = "collected_by")
	private Long collectedBy;
	
	@Column(name = "culled_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date culledOn;
	
	@Column(name = "culled_by")
	private Long culledBy;
	
	@Column(name = "lat")
	private String lat;
	
	@Column(name = "lng")
	private String lng;
	
	
	@Column(name = "schedule_remark")
	private String scheduleRemark;
	
	@Column(name = "collected_remark")
	private String collectedRemark;
	
	@Column(name = "culled_remark")
	private String culledRemark;
	
	@Column(name = "farm_category")
	private String farmCategory="B";

	@Column(name = "farmer_category")
	private String farmerCategory="G";
	
	
	
	@ManyToOne
	@JoinColumn(name = "culling_center_id")
	private CullingCenters cullingCenter;

	@Column(name = "bank_name")
	private String bankName;
	
	@Column(name = "bank_address")
	private String bankAddress;
	
	@Column(name = "bank_branch")
	private String bankBranch;
	
	@Column(name = "bank_city")
	private String bankCity;
	
	@Column(name = "bank_district")
	private String bankDistrict;
	
	@Column(name = "bank_state")
	private String bankState;
	
	@Transient
	private String pickupScheduledOnStr;
	
	@Transient
	private String fundTransferInitOnStr;
	
	@Transient
	private String fundTransferOnStr;
	
	@Transient
	private String collectedOnStr;
	
	@Transient
	private String culledOnStr;
	
	
}