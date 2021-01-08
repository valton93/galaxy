package com.kran.project.setup.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kran.project.farmer.entities.repo.DistrictSetupRepository;
import com.kran.project.farmer.entities.repo.StateSetupRepository;
import com.kran.project.user.dto.Pagination;
import com.kran.project.user.dto.Pagination.PageStatusConstants;
import com.kran.project.user.entities.DistrictUsers;
import com.kran.project.user.entities.repo.DistrictUsersRepository;

@Controller
@RequestMapping("setup")
@Scope("prototype")
public class SetupController {

	@Autowired
	StateSetupRepository stateSetupRepository;
	@Autowired
	DistrictSetupRepository districtSetupRepository;
	@Autowired
	DistrictUsersRepository  districtUsersRepository;

	private Map<String, String> fieldErrors;

	

	
	@RequestMapping(value = "getDistrictUserSetup", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getDistrictUserSetup(HttpSession session, Pagination pagination)	throws IOException {
		session.setAttribute("tab", "settings");
		session.setAttribute("subtab", "districtusersetup");
		ModelAndView modelAndView = new ModelAndView();
		if ((pagination == null || pagination.getPageTotal() == 0) && !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
			pagination = new Pagination();
		}

		List<DistrictUsers> districtUsersList = null;
		if (pagination.getPageTotal() > 0) {
			PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
			districtUsersList = districtUsersRepository.findAllDistrictUsers(pagination.getPageSearch(),pageRequest);
			if (districtUsersList != null) {
				pagination.setCurrentPageSize(districtUsersList.size());
			}
		}
		modelAndView.addObject("pagination", pagination);
		modelAndView.addObject("districtUsersList", districtUsersList);
		modelAndView.setViewName("setup/DistrictUserSetup");
		return modelAndView;
	}

	@RequestMapping(value = "addNewDistrictUserSetup", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView addNewDistrictUserSetup() {
		ModelAndView modelAndView = new ModelAndView();
		DistrictUsers districtUsers = new DistrictUsers();
		modelAndView.addObject("districtList", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
		modelAndView.addObject("districtUser", districtUsers);
		modelAndView.setViewName("setup/AddNewDistrictUserSetup");
		return modelAndView;
	}
	@RequestMapping(value = "editDistrictUserSetup", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView editDistrictUserSetup(Long id) {
		ModelAndView modelAndView = new ModelAndView();
		DistrictUsers districtUser = districtUsersRepository.findById(id).get();
		if(districtUser.getDcdeoStatus().equals("Y")) {
			districtUser.setUserType("DCE");
		}else if(districtUser.getDcStatus().equals("Y")) {
			districtUser.setUserType("DC");
		}else if(districtUser.getDvdeoStatus().equals("Y")) {
			districtUser.setUserType("DVE");
		}else if(districtUser.getDvoStatus().equals("Y")) {
			districtUser.setUserType("DV");
		}
		modelAndView.addObject("districtList", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
		modelAndView.addObject("districtUser", districtUser);
		modelAndView.setViewName("setup/AddNewDistrictUserSetup");
		return modelAndView;
	}

	@GetMapping("deactivateDistrictUser")
	public ModelAndView deactivateDistrictUser(Long id) {
		districtUsersRepository.deactivateDistrictUser(id);
		return new ModelAndView("redirect:/setup/getDistrictUserSetup");
	}
	@RequestMapping(value = "saveDistrictUserSetup", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveDistrictUserSetup(HttpSession session, DistrictUsers districtUser)	throws IOException {
		if(districtUser.getUserType().equals("DCE")) {
			districtUser.setDcdeoStatus("Y");
			districtUser.setDcStatus("N");
			districtUser.setDvdeoStatus("N");
			districtUser.setDvoStatus("N");
		}else if(districtUser.getUserType().equals("DC")) {
			districtUser.setDcdeoStatus("N");
			districtUser.setDcStatus("Y");
			districtUser.setDvdeoStatus("N");
			districtUser.setDvoStatus("N");
		}
		else if(districtUser.getUserType().equals("DV")) {
			districtUser.setDcdeoStatus("N");
			districtUser.setDcStatus("N");
			districtUser.setDvdeoStatus("N");
			districtUser.setDvoStatus("Y");
		}
		else if(districtUser.getUserType().equals("DVE")) {
			districtUser.setDcdeoStatus("N");
			districtUser.setDcStatus("N");
			districtUser.setDvdeoStatus("Y");
			districtUser.setDvoStatus("N");
		}
		districtUsersRepository.save(districtUser);
		return new ModelAndView("redirect:/setup/getDistrictUserSetup");
	}

}
