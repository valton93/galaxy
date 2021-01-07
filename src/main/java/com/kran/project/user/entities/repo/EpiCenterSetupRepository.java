package com.kran.project.user.entities.repo;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.kran.project.user.entities.CullingCenters;
import com.kran.project.user.entities.EpiCenterSetup;

public interface EpiCenterSetupRepository extends JpaRepository<EpiCenterSetup, Long> {

	@Transactional
	@Modifying
	@Query("UPDATE EpiCenterSetup SET deleteStatus='Y' WHERE id=?1 ")
	void updateActiveStatus(Long id);

	Long findIdByName(String name);

	@Query("SELECT COUNT(id) FROM EpiCenterSetup ")
	Long countOfEpiCenters(String pageSearch);

	@Query("FROM EpiCenterSetup")
	List<EpiCenterSetup> findByEpicCentersAll(String pageSearch, PageRequest pageRequest);

	@Query("FROM EpiCenterSetup WHERE district.id=?1 ")
	List<EpiCenterSetup> findDistrictWiseEpiCenters(Long id);
}
