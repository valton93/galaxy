package com.kran.project.farmer.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(schema = "localgovernmentdirectory", catalog = "localgovernmentdirectory", name = "lgd_district_setup")
@Data
public class DistrictSetup {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "district_Code")
	private Long id;
	@ManyToOne
	@JoinColumn(name = "state_code")
	private StateSetup stateCode;
	@Column(name = "district_name_english")
	private String districtName;
	@Column(name = "district_name_local")
	private String districtNameLocal;
	@Column(name = "ss_code")
	private String ssCode;
	@Column(name = "census_2011_code")
	private String census2011Code;
	@Column(name = "census_2001_code")
	private String census2001Code;

}
