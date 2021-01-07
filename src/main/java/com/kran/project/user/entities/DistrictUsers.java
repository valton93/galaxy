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

import com.kran.project.farmer.entities.DistrictSetup;

import lombok.Data;

@Entity
@Table(schema = "veterinaryassist", catalog = "veterinaryassist", name = "district_user_login")
@Data
public class DistrictUsers {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@ManyToOne
	@JoinColumn(name = "district")
	public DistrictSetup district;
	@Column(name = "username")
	private String userName;
	@Column(name = "password")
	private String password;
	@Column(name = "active_status")
	private String activeStatus = "Y";
	@Column(name = "otp")
	private String otp ;
	@Column(name = "password_reset")
	private String passwordReset = "N";
	@Column(name = "password_reset_on")
	private Date passwordResetOn;
	@Column(name = "name")
	private String name ;
	@Column(name = "mobile")
	private String mobile;
	@Column(name = "email")
	private String email;
	@Column(name = "dvdeo_Status")
	private String dvdeoStatus;
	@Column(name = "dvo_Status")
	private String dvoStatus;
	@Column(name = "dc_Status")
	private String dcStatus;
	@Column(name = "dcdeo_Status")
	private String dcdeoStatus;
	@Transient
	private String userType;
	
}
