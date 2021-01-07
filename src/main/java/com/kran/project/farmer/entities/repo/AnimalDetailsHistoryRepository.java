package com.kran.project.farmer.entities.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.kran.project.farmer.entities.AnimalDetailsHistory;
@EnableTransactionManagement
public interface AnimalDetailsHistoryRepository extends JpaRepository<AnimalDetailsHistory, Long> {

}
