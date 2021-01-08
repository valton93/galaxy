package com.kran.project.user.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kran.project.farmer.entities.repo.DistrictSetupRepository;
import com.kran.project.user.dto.FilterVO;
import com.kran.project.user.dto.UserBean;

@Controller
@RequestMapping("dashboard")
@Scope("prototype")
public class DashboardController {
	@Autowired
	DistrictSetupRepository districtSetupRepository;

	public static List<LocalDate> getDatesBetweenUsingJava9(LocalDate startDate, LocalDate endDate) {
	    return startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
    }
	
	@RequestMapping(value = "gotoDashboardDVO", method = { RequestMethod.GET })
	public ModelAndView gotoDashboardDVO(HttpSession session) {
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		
		session.setAttribute("tab", "dashboardDVO");

		ModelAndView modelAndView = new ModelAndView();
		FilterVO filterVO = new FilterVO();
		filterVO.setDistrictId(userBean.getDistrict());
		modelAndView.addObject("filterVO", filterVO);
		modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
		modelAndView.setViewName("dashboard/DashboardDVO");
		return modelAndView;
	}
	
	@RequestMapping(value = "getCountForDashboardAjax", method = { RequestMethod.GET })
	public ResponseEntity<Object> getCountForDashboardAjax(HttpSession session,Long id) {
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		Collection<Object> list=new ArrayList<Object>();
		FilterVO filterVO = new FilterVO();
		filterVO.setDistrictId(id);
		if(id==0) {
			/*
			 * list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(1l));
			 * list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(2l));
			 * list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(3l));
			 * list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(4l));
			 * list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(5l));
			 */
		}else {
			
			/*
			 * list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(1l,id));
			 * list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(2l,id));
			 * list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(3l,id));
			 * list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(4l,id));
			 * list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(5l,id));
			 */
			
		}
		
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(list);
	}
	@RequestMapping(value = "getCountForApplicationAjax", method = { RequestMethod.GET })
	public ResponseEntity<Object> getCountForApplicationAjax(HttpSession session,Long id) {
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		Collection<Object> list=new ArrayList<Object>();
		if(id==0) {
			/*
			 * list.add(dashboardRepository.getApplication());
			 * list.add(dashboardRepository.getSubmittedApplication());
			 * list.add(dashboardRepository.getVerifiedApplication());
			 * list.add(dashboardRepository.getApprovedApplication());
			 * list.add(dashboardRepository.getSubmittedNotVerifiedApplication());
			 * list.add(dashboardRepository.getVerifiedNotApprovedApplication());
			 */
		}else {
			
			/*
			 * list.add(dashboardRepository.getApplication(id));
			 * list.add(dashboardRepository.getSubmittedApplication(id));
			 * list.add(dashboardRepository.getVerifiedApplication(id));
			 * list.add(dashboardRepository.getApprovedApplication(id));
			 * list.add(dashboardRepository.getSubmittedNotVerifiedApplication(id));
			 * list.add(dashboardRepository.getVerifiedNotApprovedApplication(id));
			 */
			
		}
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(list);
	}
	
			
}
