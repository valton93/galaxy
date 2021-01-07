package com.kran.project.farmer.entities.repo;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.kran.project.farmer.entities.FinanceReportDetails;

import antlr.collections.List;
@EnableTransactionManagement
public interface FinanceReportDetailsRepository extends JpaRepository<FinanceReportDetails, Long> {

	@Query("FROM FinanceReportDetails WHERE deleteStatus='N' AND reportId=?1 " )
	ArrayList<FinanceReportDetails>  getAllActiveBeneficiaryListByReportId(Long id);
	
	@Query("FROM FinanceReportDetails WHERE trackingId=?1 AND reportId=?2 " )
	FinanceReportDetails getBeneficiaryByTrackingIdAndReportId(String trackingId,Long id);

	@Query("SELECT COUNT(id) "
			 + "FROM FinanceReportDetails WHERE reportId=?1  "
			 + "AND status=?2")
	Long getStatusCountForReportId(Long financeReportId, String status);

}
