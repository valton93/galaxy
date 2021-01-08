package com.kran.project.user.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kran.project.farmer.entities.repo.DistrictSetupRepository;
import com.kran.project.farmer.entities.repo.StateSetupRepository;
import com.kran.project.user.dto.Pagination;

@Controller
@RequestMapping("admin")
@Scope("prototype")
public class AdminController {
	@Autowired
	StateSetupRepository stateSetupRepository;
	@Autowired
	DistrictSetupRepository districtSetupRepository;
	
	private Map<String, String> fieldErrors;
	
	
	@RequestMapping(value = "getApplicationDetailsForAdministration", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getApplicationDetailsForAdministration(HttpSession session, Pagination pagination) {
		session.setAttribute("tab", "applications");
		ModelAndView modelAndView = new ModelAndView();

		/*
		 * if ((pagination == null || pagination.getPageTotal() == 0) &&
		 * !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
		 * pagination = new Pagination(); } if (pagination.getPageTotal() == 0 ||
		 * pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
		 * pagination.setPageTotal(farmerDetailsRepository.countAllApplication(
		 * pagination.getPageSearch())); }
		 * 
		 * Pagination.updatePaginationAttributes(pagination);
		 * 
		 * List<FarmerDetails> details = null; if (pagination.getPageTotal() > 0) {
		 * PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1,
		 * pagination.getPageSize(), Sort.by("id").ascending()); details =
		 * farmerDetailsRepository.findAllApplication( pagination.getPageSearch(),
		 * pageRequest); if (details != null) {
		 * pagination.setCurrentPageSize(details.size()); } }
		 */
		modelAndView.addObject("pagination", pagination);
//		modelAndView.addObject("details", details);
		modelAndView.setViewName("admin/applicationdetailsadmin");

		return modelAndView;
	}
				
				
}
