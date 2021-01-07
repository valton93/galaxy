package com.kran.project.farmer.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(schema = "localgovernmentdirectory", catalog = "localgovernmentdirectory", name = "lgd_state_setup")
@Data
public class StateSetup {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "state_code")
	private Long id;
	@Column(name = "state_name_english")
	private String stateName;
	@Column(name = "state_name_local")
	private String stateNameLocal;
	@Column(name = "census_2011_code")
	private String censusCode;

	public StateSetup() {
		super();
	}

	public StateSetup(Long id) {
		super();
		this.id = id;
	}

}
