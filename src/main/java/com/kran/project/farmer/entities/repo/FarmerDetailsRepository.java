package com.kran.project.farmer.entities.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.kran.project.farmer.entities.FarmerDetails;
@EnableTransactionManagement
public interface FarmerDetailsRepository extends JpaRepository<FarmerDetails, Long> {
	
	Optional<FarmerDetails> findTop1ByGenderAndAgeAndMobile(String gender, Integer age, String mobile);

	Optional<FarmerDetails> findTop1ByIdNotAndGenderAndAgeAndMobile(Long id, String gender, Integer age, String mobile);
	
	Optional<FarmerDetails> findTop1ByNameAndMobile(String name, String mobile);
	
	Optional<FarmerDetails> findTop1ByIdNotAndNameAndMobile(Long id,String name, String mobile);
	
	
	/*** DC Login Starts***/
	
	@Query("FROM FarmerDetails WHERE submitBy IS NOT NULL "
			+ "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1 "
			 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
	List<FarmerDetails> findByNativeDistrict(Long district, String pageSearch,
				PageRequest pageRequest);
		

	@Query("SELECT COUNT(id) "
			 + "FROM FarmerDetails WHERE submitBy IS NOT NULL "
			 + "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1  "
			 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
		Long countByNativeDistrict(Long district, String pageSearch);
	
	
	//Pending Application
	@Query("SELECT COUNT(id) "
			 + "FROM FarmerDetails WHERE submitBy IS NULL "
			 + "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1  "
			 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
		Long countByNativeDistrictPendingApplication(Long district, String pageSearch);
	
	@Query("FROM FarmerDetails WHERE submitBy IS NULL "
			+ "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1 "
			 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
	List<FarmerDetails> findByNativeDistrictPendingApplication(Long district, String pageSearch,
				PageRequest pageRequest);
	//Pending Application
	
	//Submitted Application
		@Query("SELECT COUNT(id) "
				 + "FROM FarmerDetails WHERE submitBy IS NOT NULL "
				 + "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1  "
				 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
			Long countByNativeDistrictSubmittedApplication(Long district, String pageSearch);
		
		@Query("FROM FarmerDetails WHERE submitBy IS NOT NULL "
				+ "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1 "
				 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
		List<FarmerDetails> findByNativeDistrictSubmittedApplication(Long district, String pageSearch,
					PageRequest pageRequest);
	//Submitted Application

	//Submitted and Not Verified Application
		@Query("SELECT COUNT(id) "
				 + "FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NULL "
				 + "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1  "
				 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
			Long countByNativeDistrictSubmittedNotVerified(Long district, String pageSearch);
		
		@Query("FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NULL "
				+ "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1 "
				 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
		List<FarmerDetails> findByNativeDistrictSubmittedNotVerified(Long district, String pageSearch,
					PageRequest pageRequest);
	//Submitted and Not Verified Application
	
	//Submitted and Verified Application
			@Query("SELECT COUNT(id) "
					 + "FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL "
					 + "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1  "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
				Long countByNativeDistrictSubmittedVerified(Long district, String pageSearch);
			
			@Query("FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL "
					+ "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1 "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
			List<FarmerDetails> findByNativeDistrictSubmittedVerified(Long district, String pageSearch,
						PageRequest pageRequest);
	//Submitted and Verified Application
			
	//Submitted and Verified and Not Approved Application
			@Query("SELECT COUNT(id) "
					 + "FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NULL "
					 + "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1  "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
				Long countByNativeDistrictSubmittedVerifiedNotApproved(Long district, String pageSearch);
			
			@Query("FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NULL "
					+ "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1 "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
			List<FarmerDetails> findByNativeDistrictSubmittedVerifiedNotApproved(Long district, String pageSearch,
						PageRequest pageRequest);
	//Submitted and Verified and Not Approved Application
		
		
	//Submitted and Verified and Approved Application
			@Query("SELECT COUNT(id) "
					 + "FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL "
					 + "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1  "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
				Long countByNativeDistrictSubmittedVerifiedApproved(Long district, String pageSearch);
			
			@Query("FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL "
					+ "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1 "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
			List<FarmerDetails> findByNativeDistrictSubmittedVerifiedApproved(Long district, String pageSearch,
						PageRequest pageRequest);
	//Submitted and Verified and Approved Application	
			@Query("SELECT COUNT(id) "
					 + "FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL  AND pickupScheduledOn IS NULL "
					 + "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1  "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
			Long countByNativeDistrictSubmittedVerifiedApprovedAndNotScheduled(Long district, String pageSearch);
			
			@Query("FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL  AND  pickupScheduledOn IS NULL "
					+ "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1 "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
			List<FarmerDetails> findByNativeDistrictSubmittedVerifiedApprovedAndNotScheduled(Long district,
					String pageSearch, PageRequest pageRequest);
			
			@Query("SELECT COUNT(id) "
					 + "FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL  AND pickupScheduledOn IS NOT NULL "
					 + "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1  "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
			Long countByNativeDistrictSubmittedVerifiedAndScheduled(Long district, String pageSearch);
			
			@Query("FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL "
					+ "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1 "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%) ORDER BY pickupScheduledOn ASC ")
			List<FarmerDetails> findByNativeDistrictSubmittedVerifiedAndScheduled(Long district,
					String pageSearch, PageRequest pageRequest);
			
			@Query("SELECT COUNT(id) "
					 + "FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL "
					 + " AND collectedOn IS NULL "
					 + "AND cullingCenter IS NOT NULL AND cullingCenter.id=?1  "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
			Long countBySubmittedVerifiedAndScheduledForCullingCenter(Long cullingCenterId, String pageSearch);
			
			@Query("FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL  AND pickupScheduledOn IS NOT NULL"
					+ " AND collectedOn IS NULL "
					+ "AND cullingCenter IS NOT NULL AND cullingCenter.id=?1 "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%) ORDER BY pickupScheduledOn ASC ")
			List<FarmerDetails> findBySubmittedVerifiedAndScheduledForCullingCenter(Long cullingCenterId,
					String pageSearch, PageRequest pageRequest);

			
			@Query("SELECT COUNT(id) "
					 + "FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL  AND pickupScheduledOn IS NOT NULL AND collectedOn IS NOT NULL "
					 + " AND culledOn IS NULL "
					 + "AND cullingCenter IS NOT NULL AND cullingCenter.id=?1  "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
			Long countByScheduledCollectedForCullingCenter(Long cullingCenter, String pageSearch);
			
			@Query("FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND  pickupScheduledOn IS NOT NULL AND collectedOn IS NOT NULL "
					+ " AND culledOn IS NULL "
					+ "AND cullingCenter IS NOT NULL AND cullingCenter.id=?1  "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
			List<FarmerDetails> findByScheduledCollectedForCullingCenter(Long cullingCenterId,
					String pageSearch, PageRequest pageRequest);
			
			@Query("SELECT COUNT(id) "
					 + "FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL "
					 + " AND pickupScheduledOn IS NOT NULL AND collectedOn IS NOT NULL AND culledOn IS NOT NULL "
					 + "AND cullingCenter IS NOT NULL AND cullingCenter.id=?1  "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
			Long countByScheduledCollectedCulledForCullingCenter(Long cullingCenter, String pageSearch);
			
			@Query("FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL "
					+ " AND pickupScheduledOn IS NOT NULL AND collectedOn IS NOT NULL AND culledOn IS NOT NULL "
					 + "AND cullingCenter IS NOT NULL AND cullingCenter.id=?1  "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
			List<FarmerDetails> findByScheduledCollectedCulledForCullingCenter(Long cullingCenterId,
					String pageSearch, PageRequest pageRequest);

			@Query("FROM FarmerDetails WHERE submitBy IS NOT NULL "
					+ "AND nativeDistrict IS NOT NULL "
					 + "AND (CONCAT(trackingId,'') LIKE %?1% OR LOWER(name) LIKE %?1% OR LOWER(mobile) LIKE %?1%)")
			List<FarmerDetails> findAllApplication( String pageSearch,
						PageRequest pageRequest);
				

			@Query("SELECT COUNT(id) "
					 + "FROM FarmerDetails WHERE submitBy IS NOT NULL "
					 + "AND nativeDistrict IS NOT NULL  "
					 + "AND (CONCAT(trackingId,'') LIKE %?1% OR LOWER(name) LIKE %?1% OR LOWER(mobile) LIKE %?1%)")
				Long countAllApplication(String pageSearch);

			@Query("SELECT COUNT(id) "
					 + "FROM FarmerDetails WHERE acceptedOn IS NOT NULL"
					 + " AND ( fundTransferInitOn IS NULL AND fundTransferStatus='N' ) OR fundTransferStatus='RS' "
					 + "AND (CONCAT(trackingId,'') LIKE %?1% OR LOWER(name) LIKE %?1% OR LOWER(mobile) LIKE %?1%)")
			Long countByFundTransferNotInitiated(String pageSearch);
			
			@Query("FROM FarmerDetails WHERE acceptedOn IS NOT NULL" 
					 + " AND ( fundTransferInitOn IS NULL AND fundTransferStatus='N' ) OR fundTransferStatus='RS' " 
					 + "AND (CONCAT(trackingId,'') LIKE %?1% OR LOWER(name) LIKE %?1% OR LOWER(mobile) LIKE %?1%)")
			List<FarmerDetails> findByFundTransferNotInitiated( String pageSearch,
					PageRequest pageRequest);
			
			@Query("FROM FarmerDetails WHERE acceptedOn IS NOT NULL" 
					 + " AND ( fundTransferInitOn IS NULL AND fundTransferStatus='N' ) OR fundTransferStatus='RS' " )
			List<FarmerDetails> findByFundTransferNotInitiatedAll();
		
		
			@Query("SELECT COUNT(id) "
					 + "FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND collectedOn is NOT NULL"
					 + " AND acceptedOn IS NULL "
					 + "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1  "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
				Long countByNativeDistrictCollectedNotApproved(Long district, String pageSearch);
			
			@Query("FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL  AND collectedOn is NOT NULL"
					+ " AND acceptedOn IS NULL "
					+ "AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1 "
					 + "AND (CONCAT(trackingId,'') LIKE %?2% OR LOWER(name) LIKE %?2% OR LOWER(mobile) LIKE %?2%)")
			List<FarmerDetails> findByNativeDistrictCollectedNotApproved(Long district, String pageSearch,
						PageRequest pageRequest);
		
		
}