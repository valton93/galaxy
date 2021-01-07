package com.kran.project.user.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kran.project.farmer.dto.FarmerDetailsForm;
import com.kran.project.farmer.entities.AnimalDetails;
import com.kran.project.farmer.entities.AnimalDetailsHistory;
import com.kran.project.farmer.entities.DistrictSetup;
import com.kran.project.farmer.entities.FarmerDetails;
import com.kran.project.farmer.entities.FarmerDetailsHistory;
import com.kran.project.farmer.entities.repo.AnimalDetailsHistoryRepository;
import com.kran.project.farmer.entities.repo.AnimalDetailsRepository;
import com.kran.project.farmer.entities.repo.DistrictSetupRepository;
import com.kran.project.farmer.entities.repo.FarmerDetailsHistoryRepository;
import com.kran.project.farmer.entities.repo.FarmerDetailsRepository;
import com.kran.project.farmer.entities.repo.PoliceStationSetupRepository;
import com.kran.project.farmer.entities.repo.StateSetupRepository;
import com.kran.project.user.dto.Pagination;
import com.kran.project.user.dto.Pagination.PageStatusConstants;
import com.kran.project.user.dto.UserBean;
import com.kran.project.user.entities.CarcassRateSetup;
import com.kran.project.user.entities.CullingCenters;
import com.kran.project.user.entities.repo.CarcassRateSetupRepository;
import com.kran.project.user.entities.repo.CullingCentersRepository;
import com.kran.project.utilities.CustomUtils;

@Controller
@RequestMapping("admin")
@Scope("prototype")
public class AdminController {
	@Autowired
	StateSetupRepository stateSetupRepository;
	@Autowired
	DistrictSetupRepository districtSetupRepository;
	@Autowired
	FarmerDetailsRepository farmerDetailsRepository;
	@Autowired
	FarmerDetailsHistoryRepository farmerDetailsHistoryRepository;
	@Autowired
	AnimalDetailsRepository  animalDetailsRepository;
	@Autowired
	AnimalDetailsHistoryRepository  animalDetailsHistoryRepository;
	@Autowired
	CarcassRateSetupRepository carcasRateRepository;
	@Autowired
	CullingCentersRepository cullingCentersRepository;
	
	private Map<String, String> fieldErrors;
	
	
	@RequestMapping(value = "getApplicationDetailsForAdministration", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getApplicationDetailsForAdministration(HttpSession session, Pagination pagination) {
		session.setAttribute("tab", "applications");
		ModelAndView modelAndView = new ModelAndView();

		if ((pagination == null || pagination.getPageTotal() == 0)
			&& !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
			pagination = new Pagination();
		}
		if (pagination.getPageTotal() == 0
			|| pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
			pagination.setPageTotal(farmerDetailsRepository.countAllApplication(pagination.getPageSearch()));
		}
		
		Pagination.updatePaginationAttributes(pagination);

		List<FarmerDetails> details = null;
		if (pagination.getPageTotal() > 0) {
			PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
			details = farmerDetailsRepository.findAllApplication( pagination.getPageSearch(), pageRequest);
			if (details != null) {
				pagination.setCurrentPageSize(details.size());
			}
		}
		modelAndView.addObject("pagination", pagination);
		modelAndView.addObject("details", details);
		modelAndView.setViewName("admin/applicationdetailsadmin");

		return modelAndView;
	}
				
				
}
