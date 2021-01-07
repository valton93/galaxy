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
@Table(schema = "veterinaryassist", catalog = "veterinaryassist", name = "animal_details")
@Data
public class AnimalDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "farmer_id")
	private Long farmerId;
	
	
	@Column(name = "reg_id")
	private String regId;
	@Column(name = "heart_girth")
	private Double heartGirth;
	
	@Column(name = "carcas_id")
	private Long carcasId;
	
	@Column(name = "length")
	private Double length;
	
	@Column(name = "live_weight")
	private Double liveWeight;
	
	@Column(name = "carcas_weight")
	private Double carcasWeight=0.0;
	
	@Column(name = "amount")
	private Long amount=0l;

	@Column(name = "remark")
	private String remark;
	
	@Column(name = "weigh_status")
	private String weighStatus;
	
	@Column(name = "modified_By")
	private Long modifiedBy;
	
	@Column(name = "modified_On")
	private Date modifiedOn;
	
	@Column(name = "breed")
	private String breed;
	
	@Column(name = "delete_status")
	private String deleteStatus="N";
	
	
	

}