package com.kran.project.farmer.entities.repo;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.kran.project.farmer.entities.DistrictSetup;
@EnableTransactionManagement
public interface DistrictSetupRepository extends JpaRepository<DistrictSetup, Long> {

	List<DistrictSetup> findByStateCodeId(Long stateCodeId, Sort sort);

	@Query("SELECT id, districtName, stateCode.stateName FROM DistrictSetup WHERE districtName LIKE %:districtName%")
	List<Object> findByDistrictNameContainingIgnoreCase(String districtName);

	Long findIdByDistrictName(String districtName);

}
