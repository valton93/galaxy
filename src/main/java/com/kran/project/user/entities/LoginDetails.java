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
@Table(schema = "user_activity_log", catalog = "user_activity_log", name = "login_session_details")
@Data
public class LoginDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "user_domain")
	private String userDomain;
	@Column(name = "user_allot_id")
	private Long userAllotId;
	@Column(name = "session")
	private String session;
	@Column(name = "ip")
	private String ip;
	@Column(name = "os")
	private String os;
	@Column(name = "browser")
	private String browser;
	@Column(name = "login_time")
	private Date loginTime;
	@Column(name = "logout_time")
	private Date logoutTime;
	@Column(name = "logout_type")
	private String logoutType;

}