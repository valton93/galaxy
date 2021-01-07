package com.kran.project.report.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kran.project.farmer.entities.FarmerDetails;
import com.kran.project.report.dao.CustomReportDetailsDAO;
import com.kran.project.user.dao.CustomUserDetailsDAO;
import com.kran.project.user.dto.FilterVO;
import com.kran.project.user.entities.CullingCenters;

@Repository
public class CustomReportDetailsDAOImpl implements CustomReportDetailsDAO {
	@Autowired
	EntityManager entityManager;

	@Override
	public List<FarmerDetails> getFarmerDetails(FilterVO filterVO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<FarmerDetails> criteriaQuery = criteriaBuilder.createQuery(FarmerDetails.class);
		Predicate districtPredicate = null;
		Predicate epiCenterPredicate = null;
		Predicate applicationStatusPredicate = null;
		Predicate transactionStatusPredicate = null;
		Predicate collectionStatusPredicate = null;
		Predicate cullingStatusPredicate = null;
		Root<FarmerDetails> farmerRoot = criteriaQuery.from(FarmerDetails.class);
		Predicate finalPredicate=criteriaBuilder.isNotNull(farmerRoot.get("nativeDistrict"));
		
		if(filterVO.getDistrictId()!=null&&filterVO.getDistrictId()!=0) {
			districtPredicate=criteriaBuilder.equal(farmerRoot.get("nativeDistrict"),filterVO.getDistrictId());
			finalPredicate=criteriaBuilder.and(finalPredicate,districtPredicate);
		}
		if(filterVO.getEpiCenter()!=null&&filterVO.getEpiCenter()!=0) {
			epiCenterPredicate=criteriaBuilder.equal(farmerRoot.get("epiCenter"),filterVO.getEpiCenter());
			finalPredicate=criteriaBuilder.and(finalPredicate,epiCenterPredicate);
		}
		if(filterVO.getApplicationStatus()!=null&&!filterVO.getApplicationStatus().equals("0")) {
			if(filterVO.getApplicationStatus().equals("S")) {
				applicationStatusPredicate=criteriaBuilder.isNotNull(farmerRoot.get("submitOn"));
			}else if(filterVO.getApplicationStatus().equals("V")) {
				applicationStatusPredicate=criteriaBuilder.isNotNull(farmerRoot.get("verifiedOn"));
			}else if(filterVO.getApplicationStatus().equals("A")) {
				applicationStatusPredicate=criteriaBuilder.isNotNull(farmerRoot.get("acceptedOn"));
			}
			finalPredicate=criteriaBuilder.and(finalPredicate,applicationStatusPredicate);
		}
		if(filterVO.getTransactionStatus()!=null&&!filterVO.getTransactionStatus().equals("0")) {
			if(filterVO.getTransactionStatus().equals("N")) {
				transactionStatusPredicate=criteriaBuilder.equal(farmerRoot.get("fundTransferStatus"),"N");
			}else if(filterVO.getTransactionStatus().equals("I")) {
				transactionStatusPredicate=criteriaBuilder.equal(farmerRoot.get("fundTransferStatus"),"I");
			}else if(filterVO.getTransactionStatus().equals("S")) {
				transactionStatusPredicate=criteriaBuilder.equal(farmerRoot.get("fundTransferStatus"),"S");
			}
			else if(filterVO.getTransactionStatus().equals("F")) {
				transactionStatusPredicate=criteriaBuilder.equal(farmerRoot.get("fundTransferStatus"),"F");
			}
			finalPredicate=criteriaBuilder.and(finalPredicate,transactionStatusPredicate);
		}
		if(filterVO.getCollectionStatus()!=null&&!filterVO.getCollectionStatus().equals("0")) {
			if(filterVO.getCollectionStatus().equals("NS")) {
				Predicate collPredicate1 =criteriaBuilder.isNotNull(farmerRoot.get("acceptedOn"));
				Predicate collPredicate2=criteriaBuilder.isNull(farmerRoot.get("pickupScheduledOn"));
				collectionStatusPredicate=criteriaBuilder.and(collPredicate1,collPredicate2);
			}else if(filterVO.getCollectionStatus().equals("PP")) {
				Predicate collPredicate1 =criteriaBuilder.isNotNull(farmerRoot.get("acceptedOn"));
				Predicate collPredicate2=criteriaBuilder.isNotNull(farmerRoot.get("pickupScheduledOn"));
				Predicate collPredicate3=criteriaBuilder.isNull(farmerRoot.get("collectedOn"));
				collectionStatusPredicate=criteriaBuilder.and(collPredicate1,collPredicate2,collPredicate3);
			}else if(filterVO.getCollectionStatus().equals("C")) {
				Predicate collPredicate1 =criteriaBuilder.isNotNull(farmerRoot.get("acceptedOn"));
				Predicate collPredicate2=criteriaBuilder.isNotNull(farmerRoot.get("pickupScheduledOn"));
				Predicate collPredicate3=criteriaBuilder.isNotNull(farmerRoot.get("collectedOn"));
				collectionStatusPredicate=criteriaBuilder.and(collPredicate1,collPredicate2,collPredicate3);
			}
			finalPredicate=criteriaBuilder.and(finalPredicate,collectionStatusPredicate);
		}
		if(filterVO.getCullingStatus()!=null&&!filterVO.getCullingStatus().equals("0")) {
			if(filterVO.getCullingStatus().equals("P")) {
				Predicate collPredicate1 =criteriaBuilder.isNotNull(farmerRoot.get("acceptedOn"));
				Predicate collPredicate2=criteriaBuilder.isNotNull(farmerRoot.get("pickupScheduledOn"));
				Predicate collPredicate3=criteriaBuilder.isNotNull(farmerRoot.get("collectedOn"));
				Predicate collPredicate4=criteriaBuilder.isNull(farmerRoot.get("culledOn"));
				cullingStatusPredicate=criteriaBuilder.and(collPredicate1,collPredicate2,collPredicate3,collPredicate4);
			}else if(filterVO.getCullingStatus().equals("C")) {
				Predicate collPredicate1 =criteriaBuilder.isNotNull(farmerRoot.get("acceptedOn"));
				Predicate collPredicate2=criteriaBuilder.isNotNull(farmerRoot.get("pickupScheduledOn"));
				Predicate collPredicate3=criteriaBuilder.isNotNull(farmerRoot.get("collectedOn"));
				Predicate collPredicate4=criteriaBuilder.isNotNull(farmerRoot.get("culledOn"));
				cullingStatusPredicate=criteriaBuilder.and(collPredicate1,collPredicate2,collPredicate3,collPredicate4);
			}
			finalPredicate=criteriaBuilder.and(finalPredicate,cullingStatusPredicate);
		}
		criteriaQuery.where(finalPredicate);

		return entityManager.createQuery(criteriaQuery).getResultList();
	}

}
