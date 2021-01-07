package com.kran.project.farmer.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(schema = "localgovernmentdirectory", catalog = "localgovernmentdirectory", name = "lgd_block_setup")
@Data
public class BlockSetup {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "block_code")
	private Long id;
	@Column(name = "district_code")
	private Long district;
	@Column(name = "block_name")
	private String blockName;
}
