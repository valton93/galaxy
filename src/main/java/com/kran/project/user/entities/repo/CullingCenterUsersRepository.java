package com.kran.project.user.entities.repo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.kran.project.user.entities.CullingCenterUsers;

public interface CullingCenterUsersRepository extends JpaRepository<CullingCenterUsers, Long> {

	Optional<CullingCenterUsers> findByUserName(String userName);

	@Transactional
	@Modifying
	@Query("UPDATE CullingCenterUsers SET activeStatus='N' WHERE id=?1 ")
	void updateActiveStatus(Long id);
	
	@Query("SELECT COUNT(id) "
			 + "FROM CullingCenterUsers ")
		long countOfCullingCentersUsers(String pageSearch);
	
	@Query("FROM CullingCenterUsers")
	List<CullingCenterUsers> findByCullingCentersUsersAll(String pageSearch, PageRequest pageRequest);

}
