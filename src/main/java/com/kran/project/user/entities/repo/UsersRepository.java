package com.kran.project.user.entities.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kran.project.user.entities.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {

	Optional<Users> findByUserName(String userName);

}
