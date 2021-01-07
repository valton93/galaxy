package com.kran.project.farmer.entities.repo;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.kran.project.farmer.entities.AnimalDetailsHistory;
import com.kran.project.farmer.entities.FarmerDetails;
import com.kran.project.farmer.entities.FinanceReport;
@EnableTransactionManagement
public interface FinanceReportRepository extends JpaRepository<FinanceReport, Long> {
	@Query("SELECT COUNT(id) "
			 + "FROM FinanceReport WHERE status='O' "
			 + "AND generatedBy=?1  "
			 + "AND (LOWER(name) LIKE %?2% ) ORDER BY reportDate DESC")
	Long countByFundTransferInitiatedByFO(Long userId, String pageSearch);
	@Query( "FROM FinanceReport WHERE status='O' "
			 + "AND generatedBy=?1  "
			 + "AND (LOWER(name) LIKE %?2% ) ORDER BY reportDate DESC")
	List<FinanceReport> findByFundTransferInitiatedByFO(Long userId, String pageSearch, PageRequest pageRequest);
	
	@Query("SELECT COUNT(id) "
			 + "FROM FinanceReport WHERE status='C' "
			 + "AND generatedBy=?1  "
			 + "AND (LOWER(name) LIKE %?2% )")
	Long countByFundTransferCompletedByFO(Long userId, String pageSearch);
	@Query( "FROM FinanceReport WHERE status='C' "
			 + "AND generatedBy=?1  "
			 + "AND (LOWER(name) LIKE %?2% )")
	List<FinanceReport> findByFundTransferCompletedByFO(Long userId, String pageSearch, PageRequest pageRequest);

}
