package com.kran.project.user.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(schema = "veterinaryassist", catalog = "veterinaryassist", name = "carcass_rate_setup")
@Data
public class CarcassRateSetup {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	  
	@Column(name = "destruction_Category")
	private String destructionCategory;
	
	@Column(name = "from_Kg")
	private double fromKg;
	
	@Column(name = "to_Kg")
	private double toKg;
	
	@Column(name = "rate")
	private Long rate;

	
	@Column(name = "delete_Status")
	private String deleteStatus = "N";
	
	
}
