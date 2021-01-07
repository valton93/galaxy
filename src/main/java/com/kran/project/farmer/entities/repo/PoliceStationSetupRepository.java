package com.kran.project.farmer.entities.repo;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.kran.project.farmer.entities.PoliceStationSetup;
@EnableTransactionManagement
public interface PoliceStationSetupRepository extends JpaRepository<PoliceStationSetup, Long> {

	List<PoliceStationSetup> findByDistrict(Long district, Sort sort);

}
