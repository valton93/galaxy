package com.kran.project.farmer.entities.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kran.project.farmer.entities.FarmerDetails;

public interface DashboardRepository extends JpaRepository<FarmerDetails, Long> {
	

	@Query("SELECT COUNT(id) FROM FarmerDetails")
	Long getApplication();
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getApplication(Long district);
	@Query("SELECT COUNT(id) FROM FarmerDetails where date(created_on)<=?1")
	Long getApplication(Date date);
	@Query("SELECT COUNT(id) FROM FarmerDetails where date(created_on)<=?1 AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?2")
	Long getApplication(Date date,Long district);
	
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL ")
	Long getSubmittedApplication();
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getSubmittedApplication(Long district);
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND date(submitOn)<=?1")
	Long getSubmittedApplication(Date date);
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND date(submitOn)<=?1 AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?2")
	Long getSubmittedApplication(Date date,Long district);
	
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL ")
	Long getVerifiedApplication();
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getVerifiedApplication(Long district);
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND date(verifiedOn)<=?1 ")
	Long getVerifiedApplication(Date date);
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND date(verifiedOn)<=?1 AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?2")
	Long getVerifiedApplication(Date date,Long district);
	
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL")
	Long getApprovedApplication();
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getApprovedApplication(Long district);
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND date(acceptedOn)<=?1 ")
	Long getApprovedApplication(Date date);
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND date(acceptedOn)<=?1 AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?2")
	Long getApprovedApplication(Date date,Long district);
	
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NULL")
	Long getSubmittedNotVerifiedApplication();
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getSubmittedNotVerifiedApplication(Long district);
	
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NULL")
	Long getVerifiedNotApprovedApplication();
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getVerifiedNotApprovedApplication(Long district);
	
	@Query("SELECT COUNT(id) FROM AnimalDetails WHERE carcasId=?1")
	Long getAnimalsByCarcassRateSetupId(Long carcassRateSetupId);
	@Query("SELECT COUNT(ad.id) FROM AnimalDetails ad Left Join FarmerDetails fd on ad.farmerId=fd.id WHERE ad.carcasId=?1 and fd.nativeDistrict.id=?2")
	Long getAnimalsByCarcassRateSetupId(Long carcassRateSetupId,Long district);
	
	
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL")
	Long getApprovedBeneficiaries();	
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getApprovedBeneficiaries(Long district);
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND fundTransferInitOn Is NULL AND fundTransferStatus='N'")
	Long getFundransferNotInitiatedBeneficiaries();
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND fundTransferInitOn Is NULL AND fundTransferStatus='N' AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getFundransferNotInitiatedBeneficiaries(Long district);
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND fundTransferInitOn Is NOT NULL AND fundTransferStatus='I' ")
	Long getFundransferInitiatedBeneficiaries();
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND fundTransferInitOn Is NOT NULL AND fundTransferStatus='I' AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getFundransferInitiatedBeneficiaries(Long district);
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND fundTransferInitOn Is NOT NULL AND fundTransferStatus='S' AND fundTransferOn IS NOT NULL ")
	Long getFundransferedBeneficiaries();
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND fundTransferInitOn Is NOT NULL AND fundTransferStatus='S' AND fundTransferOn IS NOT NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getFundransferedBeneficiaries(Long district);
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND fundTransferInitOn Is NOT NULL AND fundTransferStatus='F' AND fundTransferOn IS NOT NULL ")
	Long getFundTransferFailedBeneficiaries();
	@Query("SELECT COUNT(id) FROM FarmerDetails WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND fundTransferInitOn Is NOT NULL AND fundTransferStatus='F' AND fundTransferOn IS NOT NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getFundTransferFailedBeneficiaries(Long district);
	
	
	@Query("SELECT SUM(fd.amount) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL")
	Long getApprovedAmount();	
	@Query("SELECT SUM(fd.amount) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getApprovedAmount(Long district);
	@Query("SELECT SUM(fd.amount) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND fundTransferInitOn Is NULL AND fundTransferStatus='N'")
	Long getFundTransferNotInitiatedAmount();
	@Query("SELECT SUM(fd.amount) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND fundTransferInitOn Is NULL AND fundTransferStatus='N' AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getFundTransferNotInitiatedAmount(Long district);
	@Query("SELECT SUM(fd.amount) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND fundTransferInitOn Is NOT NULL AND fundTransferStatus='I' ")
	Long getFundTransferInitiatedAmount();
	@Query("SELECT SUM(fd.amount) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND fundTransferInitOn Is NOT NULL AND fundTransferStatus='I' AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getFundTransferInitiatedAmount(Long district);
	@Query("SELECT SUM(fd.amount) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND fundTransferInitOn Is NOT NULL AND fundTransferStatus='S' AND fundTransferOn IS NOT NULL ")
	Long getFundTransferedAmount();
	@Query("SELECT SUM(fd.amount) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND fundTransferInitOn Is NOT NULL AND fundTransferStatus='S' AND fundTransferOn IS NOT NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getFundTransferedAmount(Long district);
	@Query("SELECT SUM(fd.amount) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND fundTransferInitOn Is NOT NULL AND fundTransferStatus='F' AND fundTransferOn IS NOT NULL ")
	Long getFundTransferFailedAmount();
	@Query("SELECT SUM(amount) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND fundTransferInitOn Is NOT NULL AND fundTransferStatus='F' AND fundTransferOn IS NOT NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getFundTransferFailedAmount(Long district);
	
	
	@Query("SELECT COUNT(id) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL")
	Long getPickupScheduledApplication();	
	@Query("SELECT COUNT(id) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getPickupScheduledApplication(Long district);
	@Query("SELECT COUNT(id) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS  NULL")
	Long getSchedulePendingApplication();
	@Query("SELECT COUNT(id) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS  NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getSchedulePendingApplication(Long district);
	@Query("SELECT COUNT(id) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND collectedOn IS NOT NULL ")
	Long getCollectionCompletedApplication();
	@Query("SELECT COUNT(id) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND collectedOn IS NOT NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getCollectionCompletedApplication(Long district);
	@Query("SELECT COUNT(id) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND collectedOn IS  NULL ")
	Long getCollectionPendingApplication();
	@Query("SELECT COUNT(id) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND collectedOn IS  NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getCollectionPendingApplication(Long district);
	@Query("SELECT COUNT(id) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND collectedOn IS  NOT NULL AND culledOn Is NOT NULL ")
	Long getCullingCompletedApplication();
	@Query("SELECT COUNT(id) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND collectedOn IS  NOT NULL AND culledOn Is NOT NULL  AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getCullingCompletedApplication(Long district);
	@Query("SELECT COUNT(id) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND collectedOn IS  NOT NULL AND culledOn Is NULL ")
	Long getCullingPendingApplication();
	@Query("SELECT COUNT(id) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND collectedOn IS  NOT NULL AND culledOn Is NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getCullingPendingApplication(Long district);
	
	@Query("SELECT COUNT(fd.noOfAnimal) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL")
	Long getPickupScheduledAnimal();	
	@Query("SELECT COUNT(fd.noOfAnimal) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getPickupScheduledAnimal(Long district);
	@Query("SELECT COUNT(fd.noOfAnimal) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS  NULL")
	Long getSchedulePendingAnimal();
	@Query("SELECT COUNT(fd.noOfAnimal) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS  NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getSchedulePendingAnimal(Long district);
	@Query("SELECT COUNT(fd.noOfAnimal) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND collectedOn IS NOT NULL ")
	Long getCollectionCompletedAnimal();
	@Query("SELECT COUNT(fd.noOfAnimal) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND collectedOn IS NOT NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getCollectionCompletedAnimal(Long district);
	@Query("SELECT COUNT(fd.noOfAnimal) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND collectedOn IS  NULL ")
	Long getCollectionPendingAnimal();
	@Query("SELECT COUNT(fd.noOfAnimal) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND collectedOn IS  NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getCollectionPendingAnimal(Long district);
	@Query("SELECT COUNT(fd.noOfAnimal) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND collectedOn IS  NOT NULL AND culledOn Is NOT NULL ")
	Long getCullingCompletedAnimal();
	@Query("SELECT COUNT(fd.noOfAnimal) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND collectedOn IS  NOT NULL AND culledOn Is NOT NULL  AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getCullingCompletedAnimal(Long district);
	@Query("SELECT COUNT(fd.noOfAnimal) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND collectedOn IS  NOT NULL AND culledOn Is NULL ")
	Long getCullingPendingAnimal();
	@Query("SELECT COUNT(fd.noOfAnimal) FROM FarmerDetails as fd WHERE submitBy IS NOT NULL AND verifiedOn IS NOT NULL AND acceptedOn IS NOT NULL AND pickupScheduledOn IS NOT NULL AND collectedOn IS  NOT NULL AND culledOn Is NULL AND nativeDistrict IS NOT NULL AND nativeDistrict.id=?1")
	Long getCullingPendingAnimal(Long district);
		
}