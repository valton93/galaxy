package com.kran.project.farmer.entities.repo;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.kran.project.farmer.entities.AnimalDetails;
@EnableTransactionManagement
public interface AnimalDetailsRepository extends JpaRepository<AnimalDetails, Long> {


	@Query("FROM AnimalDetails WHERE farmerId=?1")
	ArrayList<AnimalDetails> findByfarmerId(Long id);

	@Query("FROM AnimalDetails WHERE farmerId=?1 and deleteStatus='N'")
	ArrayList<AnimalDetails> getActiveAnimalDetailsByFarmer(Long id);

}
