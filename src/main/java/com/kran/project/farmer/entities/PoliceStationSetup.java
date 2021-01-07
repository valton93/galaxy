package com.kran.project.farmer.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(schema = "localgovernmentdirectory", catalog = "localgovernmentdirectory", name = "police_station_setup")
@Data
public class PoliceStationSetup {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ps_id")
	private Long id;
	@Column(name = "district_code")
	private Long district;
	@Column(name = "police_station_name")
	private String policeStationName;
	@Column(name = "type")
	private String type;
}
