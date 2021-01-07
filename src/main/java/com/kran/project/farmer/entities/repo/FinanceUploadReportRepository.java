package com.kran.project.farmer.entities.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.kran.project.farmer.entities.FinanceUploadReport;
@EnableTransactionManagement
public interface FinanceUploadReportRepository extends JpaRepository<FinanceUploadReport, Long> {
	@Query("SELECT COUNT(id) "
			 + "FROM FinanceUploadReport WHERE reportId=?1")
	Long getCountOfUploadsForReportId(Long financeReportId);

	@Query("FROM FinanceUploadReport WHERE reportId=?1  and updateStatus='N' and deleteStatus='N' " )
	FinanceUploadReport getNonUpdatedFinanceUploadReportForReportId(Long id);

	

}
