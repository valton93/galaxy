package com.kran.project.user.dao.impl;

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
import com.kran.project.user.dao.CustomUserDetailsDAO;
import com.kran.project.user.dto.FilterVO;
import com.kran.project.user.entities.CullingCenters;

@Repository
public class CustomUserDetailsDAOImpl implements CustomUserDetailsDAO {
	@Autowired
	EntityManager entityManager;

	@Override
	public List<FarmerDetails> getMigrantApplicantDetails(FilterVO filterVO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<FarmerDetails> criteriaQuery = criteriaBuilder.createQuery(FarmerDetails.class);

		Root<FarmerDetails> root = criteriaQuery.from(FarmerDetails.class);
		Join<FarmerDetails,CullingCenters > join1 = root.join("cullingCenters", JoinType.LEFT);
		
		List<Predicate> predicates = new ArrayList<>();
//		if (filterVO.getScreeningCenter() != null
//			&& filterVO.getScreeningCenter() > 0) {
//			predicates.add(criteriaBuilder.equal(join1.get("id"), filterVO.getScreeningCenter()));
//		}
		if (filterVO.getUser() != null) {
			predicates.add(criteriaBuilder.equal(root.get("culledBy"), filterVO.getUser()));
		}
		if (filterVO.getFromDate() != null
			&& filterVO.getToDate() != null) {
			predicates.add(criteriaBuilder.between(root.get("culledOn"), filterVO.getFromDate(), filterVO.getToDate()));
		}
		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(criteriaQuery).getResultList();
	}

}
