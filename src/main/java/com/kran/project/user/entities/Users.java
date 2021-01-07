package com.kran.project.user.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(schema = "veterinaryassist", catalog = "veterinaryassist", name = "users")
@Data
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "name")
	private String name;
	@Column(name = "designation")
	private String designation;
	@Column(name = "user_type")
	private String type;
	@Column(name = "username")
	private String userName;
	@Column(name = "password")
	private String password;
	
	@Column(name = "administrator")
	private String administrator = "N";
	@Column(name = "finance_manager")
	private String financeManager = "N";
	
	@Column(name = "password_reset")
	private String passwordReset = "N";
	@Column(name = "password_reset_on")
	private Date passwordResetOn;

	@Column(name = "mobile")
	private String mobile;
	@Column(name = "email")
	private String email;
	@Column(name = "otp")
	private String otp;
	
}
