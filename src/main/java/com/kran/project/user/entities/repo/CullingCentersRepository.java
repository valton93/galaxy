package com.kran.project.user.entities.repo;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.kran.project.user.entities.CullingCenters;

public interface CullingCentersRepository extends JpaRepository<CullingCenters, Long> {

	@Transactional
	@Modifying
	@Query("UPDATE CullingCenters SET activeStatus='N' WHERE id=?1 ")
	void updateActiveStatus(Long id);

	Long findIdByName(String name);

	@Query("SELECT COUNT(id) FROM CullingCenters ")
	Long countOfCullingCenters(String pageSearch);

	@Query("FROM CullingCenters")
	List<CullingCenters> findByCullingCentersAll(String pageSearch, PageRequest pageRequest);

	@Query("FROM CullingCenters WHERE district.id=?1 ")
	List<CullingCenters> findDistrictWiseCullingCenters(Long id);
}
