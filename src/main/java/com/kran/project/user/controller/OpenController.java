package com.kran.project.user.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kran.project.farmer.dto.BankDetails;
import com.kran.project.farmer.entities.repo.AnimalDetailsHistoryRepository;
import com.kran.project.farmer.entities.repo.DistrictSetupRepository;
import com.kran.project.farmer.entities.repo.StateSetupRepository;
import com.kran.project.user.entities.EpiCenterSetup;
import com.kran.project.user.entities.repo.CarcassRateSetupRepository;
import com.kran.project.user.entities.repo.CullingCentersRepository;
import com.kran.project.user.entities.repo.EpiCenterSetupRepository;
import com.kran.project.utilities.BankIFSCService;

@Controller
@RequestMapping("open")
@Scope("prototype")
public class OpenController {
	@Autowired
	StateSetupRepository stateSetupRepository;
	@Autowired
	DistrictSetupRepository districtSetupRepository;
	@Autowired
	AnimalDetailsHistoryRepository  animalDetailsHistoryRepository;
	@Autowired
	CarcassRateSetupRepository carcasRateRepository;
	@Autowired
	CullingCentersRepository cullingCentersRepository;
	@Autowired
	EpiCenterSetupRepository epiCenterSetupRepository;
	
	
	@SuppressWarnings("unused")
	private Map<String, String> fieldErrors;
	
	
	@RequestMapping(value = "getEpicCenters", method = { RequestMethod.GET })
	public ModelAndView getApplicationDetailsForAdministration(Long district) {
		ModelAndView modelAndView=new ModelAndView();
		List<EpiCenterSetup>epiCenterList=epiCenterSetupRepository.findDistrictWiseEpiCenters(district);
		modelAndView.addObject("epicenters", epiCenterList);
		modelAndView.setViewName("open/epicenterlist");
		return modelAndView;
	}
	@GetMapping("verifyIFSC")
	@ResponseBody
	public boolean verifyIFSC(String bankIFSC) {
		if (bankIFSC != null && bankIFSC.length() > 10) {
			if(BankIFSCService.verifyIFSCUsingRazorPay(bankIFSC)) {
				return true;
			}else{
				return BankIFSCService.verifyIFSCUsingDatayuge(bankIFSC);
			}
		}

		return false;
	}
	@GetMapping(path="getIFSCDetails", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public BankDetails getIFSCDetails(String bankIFSC) {
		BankDetails bankDetails=new BankDetails();
		bankDetails.setStatus("F");
		if (bankIFSC != null && bankIFSC.length() > 10) {
			bankDetails=BankIFSCService.getIFSCUsingDatayuge(bankIFSC);
			if(bankDetails.getStatus().equals("S")) {
				bankDetails=BankIFSCService.getIFSCUsingRazorPay(bankIFSC);
			}
		}
		return bankDetails;
	}		
				
}
