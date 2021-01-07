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

import lombok.Data;

@Entity
@Table(schema = "veterinaryassist", catalog = "veterinaryassist", name = "culling_center_users")
@Data
public class CullingCenterUsers {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@ManyToOne
	@JoinColumn(name = "center")
	private CullingCenters center;
	@Column(name = "name")
	private String name;
	@Column(name = "mobile")
	private Long mobile;
	@Column(name = "email")
	private String email;
	@Column(name = "username")
	private String userName;
	@Column(name = "password")
	private String password;
	@Column(name = "active_status")
	private String activeStatus = "N";
	@Column(name = "created_on")
	private Date createdOn;
	
	@Column(name = "data_entry_operator")
	private String dataEntryOperator = "N";
	@Column(name = "culling_officer")
	private String cullingOfficer = "N";
	
	@Column(name = "password_reset")
	private String passwordReset = "N";
	@Column(name = "password_reset_on")
	private Date passwordResetOn;
	@Column(name = "otp")
	private String otp;
	
}
