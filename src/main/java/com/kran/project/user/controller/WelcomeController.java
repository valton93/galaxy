package com.kran.project.user.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kran.project.farmer.entities.repo.DistrictSetupRepository;
import com.kran.project.farmer.entities.repo.PoliceStationSetupRepository;
import com.kran.project.user.dto.UserBean;
import com.kran.project.user.service.CustomUserDetailsService;

@Controller
@Scope("prototype")
public class WelcomeController {
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	DistrictSetupRepository districtSetupRepository;
	@Autowired
	PoliceStationSetupRepository policeStationSetupRepository;


	@GetMapping(value = "/")
	public ModelAndView welcome(HttpSession session, Authentication authentication) {
		String[] usernameAndDomain = StringUtils.split(authentication.getName(), "$$");
		
		if (usernameAndDomain[1].equals("A")) {
			UserBean userBean = customUserDetailsService.loadUserDetailsForUser(usernameAndDomain[0]);
			session.setAttribute("userBean", userBean);
		} else if (usernameAndDomain[1].equals("D")) {
			UserBean userBean= customUserDetailsService.loadUserDetailsForDistrict(usernameAndDomain[0]);
			session.setAttribute("userBean", userBean);
		}
		else if (usernameAndDomain[1].equals("C")) {
			UserBean userBean
				= customUserDetailsService.loadUserDetailsForCullingCenter(usernameAndDomain[0]);
			session.setAttribute("userBean", userBean);
		}
		else if (usernameAndDomain[1].equals("F")) {
			UserBean userBean = customUserDetailsService.loadUserDetailsForFinanceUser(usernameAndDomain[0]);
			session.setAttribute("userBean", userBean);
		}

		return new ModelAndView("redirect:/home");
	}

	@GetMapping(value = "/home")
	public ModelAndView home(HttpSession session) {
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		
		if (userBean.getUserDomain().equals("A")) {
				return new ModelAndView("redirect:/admin/getApplicationDetailsForAdministration");
		} else if (userBean.getUserDomain().equals("D")) {
			if(userBean.getDVOUser().equals("Y")) {
				return new ModelAndView("redirect:/district/getDistrictApplicationForVerificationList");
			}
			else if(userBean.getDVODEOUser().equals("Y")) {
				return new ModelAndView("redirect:/district/getDistrictApplicationSubmittedDEOList");
			}
			else if(userBean.getDCUser().equals("Y")) {
				return new ModelAndView("redirect:/district/getDistrictApplicationDCList");
			}
			else if(userBean.getDCDEOUser().equals("Y")) {
				return new ModelAndView("redirect:/district/getDistrictApplicationDCDEOList");
			}
				
		}
		else if (userBean.getUserDomain().equals("C")) {
			if(userBean.getCullingOfficer().equals("Y")) {
				return new ModelAndView("redirect:/district/getPickupScheduledCullingOfficeList");
			}
		}
		else if (userBean.getUserDomain().equals("F")) {
			if(userBean.getFinanceManager().equals("Y")) {
				return new ModelAndView("redirect:/finance/getNonInitiatedTransactionList");
			}
		}
		return new ModelAndView("welcome");
	}
	
	@GetMapping(value = "/public/getDistricts")
	public ModelAndView getDistricts(Long state) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("presentDistricts", districtSetupRepository.findByStateCodeId(state, Sort.by("districtName")));
		modelAndView.setViewName("/public/districts");
		return modelAndView;
	}
	
	@GetMapping(value = "/public/getOtherDistricts")
	@ResponseBody
	public List<Object> getOtherDistricts(String temp) {
		return districtSetupRepository.findByDistrictNameContainingIgnoreCase(temp);
	}
	
	@GetMapping(value = "/public/getPoliceStations")
	public ModelAndView getPoliceStations(Long district) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("nativestations", policeStationSetupRepository.findByDistrict(district, Sort.by("policeStationName")));
		modelAndView.setViewName("/public/policestations");
		return modelAndView;
	}

	@GetMapping(value = "/analytics")
	public ModelAndView analytics(HttpSession session) {
		session.setAttribute("tab", "analytics");
		return new ModelAndView("analytics");
	}
	@GetMapping(value = "/public/getPoliceStationList")
	public ModelAndView getPoliceStationList(Long district) {
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.addObject("nativestations", policeStationSetupRepository.findByDistrict(district, Sort.by("policeStationName")));
		modelAndView.setViewName("/public/policestationlist");
		return modelAndView;
	}

	
}
