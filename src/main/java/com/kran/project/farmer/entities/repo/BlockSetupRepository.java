package com.kran.project.farmer.entities.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.kran.project.farmer.entities.BlockSetup;
@EnableTransactionManagement
public interface BlockSetupRepository extends JpaRepository<BlockSetup, Long> {

	List<BlockSetup> findByDistrict(Long district);

}
