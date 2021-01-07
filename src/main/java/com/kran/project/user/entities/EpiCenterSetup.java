package com.kran.project.user.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

import com.kran.project.farmer.entities.DistrictSetup;

import lombok.Data;

@Entity
@Table(schema = "veterinaryassist", catalog = "veterinaryassist", name = "epicenter_setup")
@Data
public class EpiCenterSetup {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "name")
	private String name;
	@ManyToOne
	@JoinColumn(name = "district_id")
	private DistrictSetup district;
	@Column(name = "delete_status")
	private String deleteStatus = "N";
	
}
