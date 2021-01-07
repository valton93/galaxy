package com.kran.project.user.entities.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kran.project.user.entities.LoginDetails;

public interface LoginDetailsRepository extends JpaRepository<LoginDetails, Long> {

}
