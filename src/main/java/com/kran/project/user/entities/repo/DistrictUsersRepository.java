package com.kran.project.user.entities.repo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.kran.project.user.entities.DistrictUsers;

public interface DistrictUsersRepository extends JpaRepository<DistrictUsers, Long> {
	
	Optional<DistrictUsers> findByUserName(String userName);
	
	@Query("FROM DistrictUsers WHERE activeStatus='Y' "
			 + "AND (CONCAT(userName,'') LIKE %?1% OR LOWER(name) LIKE %?1% OR LOWER(mobile) LIKE %?1%)")
	List<DistrictUsers> findAllDistrictUsers(String pageSearch, PageRequest pageRequest);

	@Transactional
	@Modifying
	@Query("UPDATE DistrictUsers SET activeStatus='N' WHERE id=?1 ")
	void deactivateDistrictUser(Long id);
	
}
