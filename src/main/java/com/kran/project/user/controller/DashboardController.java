package com.kran.project.user.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

import com.kran.project.farmer.entities.repo.DashboardRepository;
import com.kran.project.farmer.entities.repo.DistrictSetupRepository;
import com.kran.project.user.dto.FilterVO;
import com.kran.project.user.dto.UserBean;

@Controller
@RequestMapping("dashboard")
@Scope("prototype")
public class DashboardController {
	@Autowired
	DistrictSetupRepository districtSetupRepository;
	@Autowired
	DashboardRepository dashboardRepository;

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
			list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(1l));
			list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(2l));
			list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(3l));
			list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(4l));
			list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(5l));
		}else {
			
			list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(1l,id));
			list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(2l,id));
			list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(3l,id));
			list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(4l,id));
			list.add(dashboardRepository.getAnimalsByCarcassRateSetupId(5l,id));
			
		}
		
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(list);
	}
	@RequestMapping(value = "getCountForApplicationAjax", method = { RequestMethod.GET })
	public ResponseEntity<Object> getCountForApplicationAjax(HttpSession session,Long id) {
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		Collection<Object> list=new ArrayList<Object>();
		if(id==0) {
			list.add(dashboardRepository.getApplication());
			list.add(dashboardRepository.getSubmittedApplication());
			list.add(dashboardRepository.getVerifiedApplication());
			list.add(dashboardRepository.getApprovedApplication());
			list.add(dashboardRepository.getSubmittedNotVerifiedApplication());
			list.add(dashboardRepository.getVerifiedNotApprovedApplication());
		}else {
			
			list.add(dashboardRepository.getApplication(id));
			list.add(dashboardRepository.getSubmittedApplication(id));
			list.add(dashboardRepository.getVerifiedApplication(id));
			list.add(dashboardRepository.getApprovedApplication(id));
			list.add(dashboardRepository.getSubmittedNotVerifiedApplication(id));
			list.add(dashboardRepository.getVerifiedNotApprovedApplication(id));
			
		}
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(list);
	}
	
	@RequestMapping(value = "getDateWiseApplicationChart", method = { RequestMethod.GET })
	public ResponseEntity<Object> getDateWiseApplicationChart(HttpSession session,Long id) {
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		FilterVO filterVO = new FilterVO();
		String dataHeading="'";
		String dataT="[ ";
		String dataS="[ ";
		String dataV="[ ";
		String dataA="[ ";
		int i=0;
					
		// setting UTC as the timezone
		ZoneId defaultZoneId = ZoneId.of("Asia/Kolkata");
		List<LocalDate> dates=getDatesBetweenUsingJava9(LocalDate.of(2020, 6, 01), LocalDate.now(ZoneId.of("Asia/Kolkata")));
		
		for (LocalDate localDate : dates) {
			Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
			if(i==0){
				
				dataHeading=dataHeading+ new SimpleDateFormat("dd-MM-yyyy").format(date)+"'";
				if(id==0) {
					dataT=dataT+dashboardRepository.getApplication(date)+"";
					dataS=dataS+dashboardRepository.getSubmittedApplication(date)+"";
					dataV=dataV+dashboardRepository.getVerifiedApplication(date)+"";
					dataA=dataA+dashboardRepository.getApprovedApplication(date)+"";
				}else {
					dataT=dataT+dashboardRepository.getApplication(date,id)+"";
					dataS=dataS+dashboardRepository.getSubmittedApplication(date,id)+"";
					dataV=dataV+dashboardRepository.getVerifiedApplication(date,id)+"";
					dataA=dataA+dashboardRepository.getApprovedApplication(date,id)+"";
				}

	        }else{
	        	
	        	dataHeading=dataHeading+",'"+new SimpleDateFormat("dd-MM-yyyy").format(date)+"'";
	        	if(id==0) {
	        		dataT=dataT+", "+dashboardRepository.getApplication(date)+"";
	        		dataS=dataS+", "+dashboardRepository.getSubmittedApplication(date)+"";
	        		dataV=dataV+", "+dashboardRepository.getVerifiedApplication(date)+"";
	        		dataA=dataA+", "+dashboardRepository.getApprovedApplication(date)+"";
	        	}else {
	        		dataT=dataT+", "+dashboardRepository.getApplication(date,id)+"";
	        		dataS=dataS+", "+dashboardRepository.getSubmittedApplication(date,id)+"";
	        		dataV=dataV+", "+dashboardRepository.getVerifiedApplication(date,id)+"";
	        		dataA=dataA+", "+dashboardRepository.getApprovedApplication(date,id)+"";
	        	}
	        }
			
			i++;
		}

		//dataT=dataT+" ]";
		dataS=dataS+" ]";
		dataV=dataV+" ]";
		dataA=dataA+" ]";
		
		//dataT="{ name: 'Total Applications', data: "+dataT+" }, ";
		dataS="{ name: 'Submitted Applications', data: "+dataS+" }, ";
		dataS=dataS+"{ name: 'Verified Applications', data: "+dataV+" }, ";
		dataS=dataS+"{ name: 'Approved Applications', data: "+dataA+" }, ";


		filterVO.setDates(dataHeading);
	    filterVO.setSeriesData(dataS);
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(filterVO);
	}
	@RequestMapping(value = "getCountForBeneficiariesAjax", method = { RequestMethod.GET })
	public ResponseEntity<Object> getCountForBeneficiariesAjax(HttpSession session,Long id) {
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		Collection<Object> list=new ArrayList<Object>();
		if(id==0) {
			list.add(dashboardRepository.getApprovedBeneficiaries());
			list.add(dashboardRepository.getFundransferNotInitiatedBeneficiaries());
			list.add(dashboardRepository.getFundransferedBeneficiaries());
			list.add(dashboardRepository.getFundransferInitiatedBeneficiaries());
			list.add(dashboardRepository.getFundTransferFailedBeneficiaries());
		}else {
			list.add(dashboardRepository.getApprovedBeneficiaries(id));
			list.add(dashboardRepository.getFundransferNotInitiatedBeneficiaries(id));
			list.add(dashboardRepository.getFundransferedBeneficiaries(id));
			list.add(dashboardRepository.getFundransferInitiatedBeneficiaries(id));
			list.add(dashboardRepository.getFundTransferFailedBeneficiaries(id));
			
		}
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(list);
	}
	@RequestMapping(value = "getCountForFundDistributionAjax", method = { RequestMethod.GET })
	public ResponseEntity<Object> getCountForFundDistributionAjax(HttpSession session,Long id) {
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		Collection<Object> list=new ArrayList<Object>();
		Long approvedAmt;
		Long notInitAmt;
		Long transteredAmt;
		Long ftiAmt;
		Long ftfAmt;
		if(id==0) {
			approvedAmt =dashboardRepository.getApprovedAmount();
			notInitAmt =dashboardRepository.getFundTransferNotInitiatedAmount();
			transteredAmt =dashboardRepository.getFundTransferedAmount();
			ftiAmt =dashboardRepository.getFundTransferInitiatedAmount();
			ftfAmt=dashboardRepository.getFundTransferFailedAmount();
		}else {
			approvedAmt =dashboardRepository.getApprovedAmount(id);
			notInitAmt =dashboardRepository.getFundTransferNotInitiatedAmount(id);
			transteredAmt =dashboardRepository.getFundTransferedAmount(id);
			ftiAmt =dashboardRepository.getFundTransferInitiatedAmount(id);
			ftfAmt=dashboardRepository.getFundTransferFailedAmount(id);
			
		}
		if(approvedAmt==null) {
			approvedAmt=0l;
		}
		if(notInitAmt==null) {
			notInitAmt=0l;
		}
		if(transteredAmt==null) {
			transteredAmt=0l;
		}
		if(ftiAmt==null) {
			ftiAmt=0l;
		}
		if(ftfAmt==null) {
			ftfAmt=0l;
		}
		list.add(approvedAmt);
		list.add(notInitAmt);
		list.add(transteredAmt);
		list.add(ftiAmt);
		list.add(ftfAmt);
		
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(list);
	}
	@RequestMapping(value = "getCountForCollectionAndCullingApplicationAjax", method = { RequestMethod.GET })
	public ResponseEntity<Object> getCountForCollectionAndCullingApplicationAjax(HttpSession session,Long id) {
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		Collection<Object> list=new ArrayList<Object>();
		Long pickupSch;
		Long schPending;
		Long collectionCompleted;
		Long collectionPending;
		Long cullingCompleted;
		Long cullingPending;
		if(id==0) {
			pickupSch= dashboardRepository.getPickupScheduledApplication();
			schPending =dashboardRepository.getSchedulePendingApplication();
			collectionCompleted =dashboardRepository.getCollectionCompletedApplication();
			collectionPending =dashboardRepository.getCollectionPendingApplication();
			cullingCompleted=dashboardRepository.getCullingCompletedApplication();
			cullingPending =dashboardRepository.getCullingPendingApplication();
		}else {
			pickupSch= dashboardRepository.getPickupScheduledApplication(id);
			schPending =dashboardRepository.getSchedulePendingApplication(id);
			collectionCompleted =dashboardRepository.getCollectionCompletedApplication(id);
			collectionPending =dashboardRepository.getCollectionPendingApplication(id);
			cullingCompleted=dashboardRepository.getCullingCompletedApplication(id);
			cullingPending =dashboardRepository.getCullingPendingApplication(id);
		}
		
		if(pickupSch==null) {
			pickupSch=0l;
		}
		if(schPending==null) {
			schPending=0l;
		}
		if(collectionCompleted==null) {
			collectionCompleted=0l;
		}
		if(collectionPending==null) {
			collectionPending=0l;
		}
		if(cullingCompleted==null) {
			cullingCompleted=0l;
		}
		if(cullingPending==null) {
			cullingPending=0l;
		}
		list.add(pickupSch);
		list.add(schPending);
		list.add(collectionCompleted);
		list.add(collectionPending);
		list.add(cullingCompleted);
		list.add(cullingPending);
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(list);
	}
	@RequestMapping(value = "getCountForCollectionAndCullingAnimalAjax", method = { RequestMethod.GET })
	public ResponseEntity<Object> getCountForCollectionAndCullingAnimalAjax(HttpSession session,Long id) {
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		Collection<Object> list=new ArrayList<Object>();
		Long pickupSch;
		Long schPending;
		Long collectionCompleted;
		Long collectionPending;
		Long cullingCompleted;
		Long cullingPending;
		if(id==0) {
			pickupSch= dashboardRepository.getPickupScheduledAnimal();
			schPending =dashboardRepository.getSchedulePendingAnimal();
			collectionCompleted =dashboardRepository.getCollectionCompletedAnimal();
			collectionPending =dashboardRepository.getCollectionPendingAnimal();
			cullingCompleted=dashboardRepository.getCullingCompletedAnimal();
			cullingPending =dashboardRepository.getCullingPendingAnimal();
		}else {
			pickupSch= dashboardRepository.getPickupScheduledAnimal(id);
			schPending =dashboardRepository.getSchedulePendingAnimal(id);
			collectionCompleted =dashboardRepository.getCollectionCompletedAnimal(id);
			collectionPending =dashboardRepository.getCollectionPendingAnimal(id);
			cullingCompleted=dashboardRepository.getCullingCompletedAnimal(id);
			cullingPending =dashboardRepository.getCullingPendingAnimal(id);
		}
		if(pickupSch==null) {
			pickupSch=0l;
		}
		if(schPending==null) {
			schPending=0l;
		}
		if(collectionCompleted==null) {
			collectionCompleted=0l;
		}
		if(collectionPending==null) {
			collectionPending=0l;
		}
		if(cullingCompleted==null) {
			cullingCompleted=0l;
		}
		if(cullingPending==null) {
			cullingPending=0l;
		}
		list.add(pickupSch);
		list.add(schPending);
		list.add(collectionCompleted);
		list.add(collectionPending);
		list.add(cullingCompleted);
		list.add(cullingPending);
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(list);
	}
			
}
