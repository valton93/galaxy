package com.kran.project.user.entities.repo;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.kran.project.user.entities.CarcassRateSetup;

public interface CarcassRateSetupRepository extends JpaRepository<CarcassRateSetup, Long> {


	@Query("SELECT COUNT(id) FROM CarcassRateSetup ")
	Long countOfCarcassRateSetup(String pageSearch);

	@Query("FROM CarcassRateSetup")
	List<CarcassRateSetup> findByCarcassRateSetupAll(String pageSearch, PageRequest pageRequest);

	@Transactional
	@Modifying
	@Query("UPDATE CarcassRateSetup SET deleteStatus='Y' WHERE id=?1 ")
	void updateCarcassRateSetupDeleteStatus(Long id);
	
	@Query("FROM CarcassRateSetup where fromKg<=?1 and toKg>?1 and deleteStatus='N'")
	CarcassRateSetup getCarcasRateByWeight(Double carcasWeight);
	
}
