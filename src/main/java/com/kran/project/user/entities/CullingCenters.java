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
@Table(schema = "veterinaryassist", catalog = "veterinaryassist", name = "culling_centers")
@Data
public class CullingCenters {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "name")
	private String name;
	@Column(name = "address")
	private String address;
	@ManyToOne
	@JoinColumn(name = "district")
	private DistrictSetup district;
	@Column(name = "active_status")
	private String activeStatus = "N";
	@Column(name = "created_on")
	private Date createdOn;
	
}
