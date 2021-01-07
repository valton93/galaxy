package com.kran.project.farmer.entities.repo;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.kran.project.farmer.entities.StateSetup;
@EnableTransactionManagement
public interface StateSetupRepository extends JpaRepository<StateSetup, Long> {

	List<StateSetup> findByIdNot(Long id, Sort sort);

}
