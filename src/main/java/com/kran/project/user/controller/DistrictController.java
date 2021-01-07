package com.kran.project.user.controller;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import com.kran.project.user.entities.EpiCenterSetup;
import com.kran.project.user.entities.repo.CarcassRateSetupRepository;
import com.kran.project.user.entities.repo.CullingCentersRepository;
import com.kran.project.user.entities.repo.EpiCenterSetupRepository;
import com.kran.project.utilities.CustomUtils;
import com.kran.project.utilities.MessageService;
import com.kran.project.utilities.MessageService.Provider;

@Controller
@Service
@RequestMapping("district")
@Scope("prototype")
public class DistrictController {
	@Autowired
	StateSetupRepository stateSetupRepository;
	@Autowired
	DistrictSetupRepository districtSetupRepository;
	@Autowired
	PoliceStationSetupRepository policeStationSetupRepository;
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
	@Autowired
	EpiCenterSetupRepository epiCenterSetupRepository;
	private Map<String, String> fieldErrors;
	@Value("${templates.fundapproval}")
    private String templatesFundApproval;
	@Value("${templates.pickupschedule}")
    private String templatesPickupSchedule;
	
	
	@RequestMapping(value = "gotoNewApplication", method = { RequestMethod.GET })
	public ModelAndView gotoNewApplication(HttpSession session) {
		UserBean userBean = (UserBean) session.getAttribute("userBean");
//		session.setAttribute("tab", "applicationDC");
		ModelAndView modelAndView = new ModelAndView();
		FarmerDetails farmer = new FarmerDetails();
		ArrayList<AnimalDetails> animalDetailsList=new ArrayList<AnimalDetails>();
		if(animalDetailsList.size()<1) {
			animalDetailsList.add(new AnimalDetails() );
		}
		farmer.setNativeDistrict(new DistrictSetup());
		farmer.getNativeDistrict().setId(userBean.getDistrict());
		FarmerDetailsForm form=new FarmerDetailsForm();
		form.setFarmer(farmer);
		form.setAnimalDetails(animalDetailsList);
		modelAndView.addObject("farmerDetails", form);
		modelAndView.addObject("states", stateSetupRepository.findByIdNot(18L, Sort.by("stateName")));
		modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
		modelAndView.addObject("epicenters", epiCenterSetupRepository.findDistrictWiseEpiCenters(userBean.getDistrict()));
		modelAndView.setViewName("district/newapplication");
		return modelAndView;
	}
	@Transactional
	@PostMapping("saveNewEntry")
	public ModelAndView saveNewEntry(HttpSession session, FarmerDetailsForm farmerDetailsForm) {
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		FarmerDetails farmerDetails=farmerDetailsForm.getFarmer();
		if(farmerDetails.getEpiCenter()==null||farmerDetails.getEpiCenter().getId()==0){
			farmerDetails.setEpiCenter(null);
		}
		List<AnimalDetails>animalList=farmerDetailsForm.getAnimalDetails();
		animalList = animalList.stream().filter(animal ->(animal.getLiveWeight()!=null)||(animal.getHeartGirth()!=null&&animal.getLength()!=null )).collect(Collectors.toList());
		if (!validateFarmerDetails(farmerDetails,animalList)) {
			ModelAndView modelAndView = new ModelAndView();
			if(animalList.size()==0) {
				animalList.add(new AnimalDetails());
			}
		
			farmerDetailsForm.setAnimalDetails(animalList);
			modelAndView.addObject("fieldErrors", fieldErrors);
			
			modelAndView.addObject("farmerDetails", farmerDetailsForm);
			modelAndView.addObject("states", stateSetupRepository.findByIdNot(18L, Sort.by("stateName")));
			modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
			modelAndView.setViewName("district/newapplication");
			if(farmerDetailsForm.getFarmer().getNativeDistrict()!=null) {
				modelAndView.addObject("epicenters", epiCenterSetupRepository.findDistrictWiseEpiCenters(farmerDetailsForm.getFarmer().getNativeDistrict().getId()));
				}else {
					modelAndView.addObject("epicenters", epiCenterSetupRepository.findDistrictWiseEpiCenters(userBean.getDistrict()));
				}
			modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
			modelAndView.addObject("nativestations", policeStationSetupRepository.findAll(Sort.by("policeStationName")));
			modelAndView.setViewName("district/newapplication");
			
			return modelAndView;
		}
		
		farmerDetails.setSubmitBy(userBean.getUserId());
		farmerDetails.setSubmitOn(new Date());
		
		farmerDetailsRepository.save(farmerDetails);
		
		Integer animalCount=0;
		Long totalAmount=0l;
		for(AnimalDetails animal:animalList) {
			animalCount=animalCount+1;
			
			animal.setFarmerId(farmerDetails.getId());
			animal=getAndPopulateAnimalDetails(animal);
			totalAmount=totalAmount+animal.getAmount();
			animal.setModifiedOn(new Date());
			animal.setModifiedBy(userBean.getUserId());
			animal.setDeleteStatus("N");
			animalDetailsRepository.save(animal);
		}
		farmerDetails.setAmount(totalAmount);
		farmerDetails.setNoOfAnimal(animalCount);
		if (farmerDetails.getTrackingId() == null
				|| farmerDetails.getTrackingId().isBlank()) {
				farmerDetails.setTrackingId(
					"T" +
					CustomUtils.padCharacters(farmerDetails.getId().toString(), 7, '0', "L")
				);
				farmerDetailsRepository.save(farmerDetails);
			}
		if(userBean.getDVODEOUser().equals("Y")) {
		
		return new ModelAndView("redirect:/district/getDistrictApplicationSubmittedDEOList");
		}else if(userBean.getDVOUser().equals("Y")){
			return new ModelAndView("redirect:/district/getDistrictApplicationForVerificationList");	
		}else  {
			return new ModelAndView("redirect:/district/getDistrictApplicationDCList");	
		}
	}
	
	private boolean validateFarmerDetails(FarmerDetails farmerDetails,List<AnimalDetails>animalList) {
		boolean resultStatus = true;
		
		fieldErrors = new LinkedHashMap<>();
		
		Pattern numeric = Pattern.compile("[^0-9]", Pattern.CASE_INSENSITIVE);
		
		if (farmerDetails.getMobile() == null
			|| farmerDetails.getMobile().length() != 10) {
			fieldErrors.put("Applicant Mobile", "Invalid Mobile Number");
			resultStatus = false;
		} else if (numeric.matcher(farmerDetails.getMobile()).find()) {
			fieldErrors.put("Applicant Mobile", "Mobile Number contains invalid characters");
			resultStatus = false;
		}
		
		if (farmerDetails.getName() == null
			|| farmerDetails.getName().isBlank()||(farmerDetails.getName().length()<3)) {
			fieldErrors.put("Name", "Invalid Applicant Name");
			resultStatus = false;
		}
		
		Optional<FarmerDetails> optionalDuplicate = null;
		if (farmerDetails.getId() == null) {
			optionalDuplicate = farmerDetailsRepository
				.findTop1ByNameAndMobile(farmerDetails.getName(), farmerDetails.getMobile());
		} else {
			optionalDuplicate = farmerDetailsRepository
				.findTop1ByIdNotAndNameAndMobile(farmerDetails.getId(), farmerDetails.getName(),farmerDetails.getMobile());
		}
		if (optionalDuplicate.isPresent()) {
			FarmerDetails duplicateEntry = optionalDuplicate.get();
			fieldErrors = new LinkedHashMap<>();
			fieldErrors.put("Duplicate",
							"APPLICANT WITH SAME NAME & MOBILE NUMBER EXISTS WITH <b>TRACKING ID " + duplicateEntry.getTrackingId() + "<b>");
			return false;
		}
		
		
		if (farmerDetails.getNativeDistrict() == null
			|| farmerDetails.getNativeDistrict().getId() == null
			|| farmerDetails.getNativeDistrict().getId() == 0) {
			fieldErrors.put("District", "Invalid Applicant District");
			resultStatus = false;
		}
		if(animalList.size()==0) {
			fieldErrors.put("Animal Details", "Invalid Animal Details");
			resultStatus = false;
		}
		

		return resultStatus;
	}
	
	//DVO - Application Pending For Verification Details
	@RequestMapping(value = "getDistrictApplicationForVerificationList", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getDistrictApplicationForVerificationList(HttpSession session, Pagination pagination) {
		session.setAttribute("tab", "applicationDVO");
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		ModelAndView modelAndView = new ModelAndView();

		if ((pagination == null || pagination.getPageTotal() == 0)
			&& !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
			pagination = new Pagination();
		}
		if (pagination.getPageTotal() == 0
			|| pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
			pagination.setPageTotal(farmerDetailsRepository.countByNativeDistrictSubmittedNotVerified(userBean.getDistrict(), pagination.getPageSearch()));
			
		}
		
		Pagination.updatePaginationAttributes(pagination);

		List<FarmerDetails> details = null;
		if (pagination.getPageTotal() > 0) {
			PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
			details = farmerDetailsRepository.findByNativeDistrictSubmittedNotVerified(userBean.getDistrict(), pagination.getPageSearch(), pageRequest);
			if (details != null) {
				pagination.setCurrentPageSize(details.size());
			}
		}

		modelAndView.addObject("pagination", pagination);
		modelAndView.addObject("details", details);
		modelAndView.setViewName("district/applicationdetailsdvo");

		return modelAndView;
	}
	
	//DVO - Verified Application Details
		@RequestMapping(value = "getDistrictApplicationVerifiedList", method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView getDistrictApplicationVerifiedList(HttpSession session, Pagination pagination) {
			session.setAttribute("tab", "applicationDVOVerified");
			UserBean userBean = (UserBean) session.getAttribute("userBean");
			ModelAndView modelAndView = new ModelAndView();

			if ((pagination == null || pagination.getPageTotal() == 0)
				&& !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
				pagination = new Pagination();
			}
			if (pagination.getPageTotal() == 0
				|| pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
				pagination.setPageTotal(farmerDetailsRepository.countByNativeDistrictSubmittedVerified(userBean.getDistrict(), pagination.getPageSearch()));
			}
			
			Pagination.updatePaginationAttributes(pagination);

			List<FarmerDetails> details = null;
			if (pagination.getPageTotal() > 0) {
				PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
				details = farmerDetailsRepository.findByNativeDistrictSubmittedVerified(userBean.getDistrict(), pagination.getPageSearch(), pageRequest);
				if (details != null) {
					pagination.setCurrentPageSize(details.size());
				}
			}

			modelAndView.addObject("pagination", pagination);
			modelAndView.addObject("details", details);
			modelAndView.setViewName("district/applicationdetailsdvo");

			return modelAndView;
		}
	
	//DVO - DEO - Application Details - Pending Application
		@RequestMapping(value = "getDistrictApplicationDEOList", method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView getDistrictApplicationDEOList(HttpSession session, Pagination pagination) {
			session.setAttribute("tab", "applicationDEO");
			UserBean userBean = (UserBean) session.getAttribute("userBean");
			ModelAndView modelAndView = new ModelAndView();
			if ((pagination == null || pagination.getPageTotal() == 0)
				&& !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
				pagination = new Pagination();
			}
			if (pagination.getPageTotal() == 0
				|| pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
				pagination.setPageTotal(farmerDetailsRepository.countByNativeDistrictPendingApplication(userBean.getDistrict(), pagination.getPageSearch()));
			}
			Pagination.updatePaginationAttributes(pagination);
			List<FarmerDetails> details = null;
			if (pagination.getPageTotal() > 0) {
				PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
				details = farmerDetailsRepository.findByNativeDistrictPendingApplication(userBean.getDistrict(), pagination.getPageSearch(), pageRequest);
				if (details != null) {
					pagination.setCurrentPageSize(details.size());
				}
			}
			modelAndView.addObject("pagination", pagination);
			modelAndView.addObject("details", details);
			modelAndView.setViewName("district/applicationdetailsdvodeo");
			return modelAndView;
		}
		
		//DVO - DEO - Application Details - Submitted Application
		@RequestMapping(value = "getDistrictApplicationSubmittedDEOList", method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView getDistrictApplicationSubmittedDEOList(HttpSession session, Pagination pagination) {
			session.setAttribute("tab", "applicationDEOSubmitted");
			UserBean userBean = (UserBean) session.getAttribute("userBean");
			ModelAndView modelAndView = new ModelAndView();
			if ((pagination == null || pagination.getPageTotal() == 0)
				&& !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
				pagination = new Pagination();
			}
			if (pagination.getPageTotal() == 0
				|| pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
				pagination.setPageTotal(farmerDetailsRepository.countByNativeDistrictSubmittedApplication(userBean.getDistrict(), pagination.getPageSearch()));
			}
			Pagination.updatePaginationAttributes(pagination);
			List<FarmerDetails> details = null;
			if (pagination.getPageTotal() > 0) {
				PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
				details = farmerDetailsRepository.findByNativeDistrictSubmittedApplication(userBean.getDistrict(), pagination.getPageSearch(), pageRequest);
				if (details != null) {
					pagination.setCurrentPageSize(details.size());
				}
			}
			modelAndView.addObject("pagination", pagination);
			modelAndView.addObject("details", details);
			modelAndView.setViewName("district/applicationdetailsdvodeo");
			return modelAndView;
		}
		
		//DC - Approval Pending Application Details
		@RequestMapping(value = "getDistrictApplicationDCList", method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView getDistrictApplicationDCList(HttpSession session, Pagination pagination) {
			session.setAttribute("tab", "applicationDC");

			UserBean userBean = (UserBean) session.getAttribute("userBean");

			ModelAndView modelAndView = new ModelAndView();

			if ((pagination == null || pagination.getPageTotal() == 0)
				&& !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
				pagination = new Pagination();
			}
			if (pagination.getPageTotal() == 0
				|| pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
				pagination.setPageTotal(farmerDetailsRepository.countByNativeDistrictSubmittedVerifiedNotApproved(userBean.getDistrict(), pagination.getPageSearch()));
			}
			
			
			Pagination.updatePaginationAttributes(pagination);

			List<FarmerDetails> details = null;
			if (pagination.getPageTotal() > 0) {
				PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
				details = farmerDetailsRepository.findByNativeDistrictSubmittedVerifiedNotApproved(userBean.getDistrict(), pagination.getPageSearch(), pageRequest);
				if (details != null) {
					pagination.setCurrentPageSize(details.size());
				}
			}

			modelAndView.addObject("pagination", pagination);
			modelAndView.addObject("details", details);
			modelAndView.setViewName("district/applicationdetailsdc");

			return modelAndView;
		}
		
		//DC - Approved Application Details
		@RequestMapping(value = "getDistrictApplicationApprovedDCList", method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView getDistrictApplicationApprovedDCList(HttpSession session, Pagination pagination) {
			session.setAttribute("tab", "applicationApprovedDC");

			UserBean userBean = (UserBean) session.getAttribute("userBean");

			ModelAndView modelAndView = new ModelAndView();

			if ((pagination == null || pagination.getPageTotal() == 0)
				&& !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
				pagination = new Pagination();
			}
			if (pagination.getPageTotal() == 0
				|| pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
				pagination.setPageTotal(farmerDetailsRepository.countByNativeDistrictSubmittedVerifiedApproved(userBean.getDistrict(), pagination.getPageSearch()));
			}


			Pagination.updatePaginationAttributes(pagination);
			List<FarmerDetails> details = null;
			if (pagination.getPageTotal() > 0) {
				PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
				details = farmerDetailsRepository.findByNativeDistrictSubmittedVerifiedApproved(userBean.getDistrict(), pagination.getPageSearch(), pageRequest);
				if (details != null) {
					pagination.setCurrentPageSize(details.size());
				}
			}

			modelAndView.addObject("pagination", pagination);
			modelAndView.addObject("details", details);
			modelAndView.setViewName("district/applicationdetailsdc");

			return modelAndView;
		}
		
		//DC-DEO - Application Details
				@RequestMapping(value = "getDistrictApplicationDCDEOList", method = { RequestMethod.GET, RequestMethod.POST })
				public ModelAndView getDistrictApplicationDCDEOList(HttpSession session, Pagination pagination) {
					session.setAttribute("tab", "applicationDCDEO");

					UserBean userBean = (UserBean) session.getAttribute("userBean");

					ModelAndView modelAndView = new ModelAndView();

					if ((pagination == null || pagination.getPageTotal() == 0)
						&& !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
						pagination = new Pagination();
					}
					if (pagination.getPageTotal() == 0
						|| pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
						pagination.setPageTotal(farmerDetailsRepository.countByNativeDistrictSubmittedVerifiedNotApproved(userBean.getDistrict(), pagination.getPageSearch()));
					}
					
					Pagination.updatePaginationAttributes(pagination);

					List<FarmerDetails> details = null;
					if (pagination.getPageTotal() > 0) {
						PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
						details = farmerDetailsRepository.findByNativeDistrictSubmittedVerifiedNotApproved(userBean.getDistrict(), pagination.getPageSearch(), pageRequest);
						if (details != null) {
							pagination.setCurrentPageSize(details.size());
						}
					}

					modelAndView.addObject("pagination", pagination);
					modelAndView.addObject("details", details);
					modelAndView.setViewName("district/applicationdetailsdcdeo");

					return modelAndView;
				}
				@RequestMapping(value = "getAmount", method = { RequestMethod.GET, RequestMethod.POST })
				public ResponseEntity<Long> getAmount(HttpSession session, String weighStatus,Double liveWeight,Double heartGirth,Double length)
				{
					AnimalDetails animalDetails=new AnimalDetails();
					animalDetails.setWeighStatus(weighStatus);
					animalDetails.setLiveWeight(liveWeight);
					animalDetails.setHeartGirth(heartGirth);
					animalDetails.setLength(length);
					animalDetails=getAndPopulateAnimalDetails(animalDetails);
					return new ResponseEntity < Long > (animalDetails.getAmount(), HttpStatus.OK);
				}
				
				private AnimalDetails getAndPopulateAnimalDetails(AnimalDetails animalDetails) {
					Double lGSquare=0.0;
					Double mFactor=69.3;
					Double liveWeight=0.0;
					Double carcasWeight=0.0;
					
					if(animalDetails.getWeighStatus().equals("Y")&&!animalDetails.getLiveWeight().isNaN()) {
						carcasWeight=(animalDetails.getLiveWeight()*72)/100;
						animalDetails.setCarcasWeight(carcasWeight);
						CarcassRateSetup crs= carcasRateRepository.getCarcasRateByWeight(carcasWeight);
//						animalDetails.setAmount(1000l);
						animalDetails.setAmount(crs.getRate());
						animalDetails.setCarcasId(crs.getId());
					}else {
						lGSquare=animalDetails.getLength()*animalDetails.getHeartGirth()*animalDetails.getHeartGirth();
						liveWeight=mFactor*lGSquare;
						carcasWeight=(liveWeight*72)/100;
						animalDetails.setCarcasWeight(carcasWeight);
						animalDetails.setLiveWeight(liveWeight);
						CarcassRateSetup crs= carcasRateRepository.getCarcasRateByWeight(carcasWeight);
//						animalDetails.setAmount(1000l);
						if(crs!=null) {
						animalDetails.setAmount(crs.getRate());
						animalDetails.setCarcasId(crs.getId());
						}
						else {
							animalDetails.setAmount(0l);
						}
					}
					return animalDetails;
				}
				
				
				@RequestMapping(value = "getDetailsForVerification/{id}", method = { RequestMethod.GET })
				public ModelAndView getDetailsForVerification(HttpSession session,  @PathVariable Long id) {
					UserBean userBean = (UserBean) session.getAttribute("userBean");
//					session.setAttribute("tab", "applicationDC");
					ModelAndView modelAndView = new ModelAndView();
					FarmerDetails farmer = farmerDetailsRepository.getOne(id);
					ArrayList<AnimalDetails> animalDetailsList=animalDetailsRepository.getActiveAnimalDetailsByFarmer(id);
					FarmerDetailsForm form=new FarmerDetailsForm();
					form.setFarmer(farmer);
					form.setAnimalDetails(animalDetailsList);
					modelAndView.addObject("farmerDetails", form);
					modelAndView.addObject("states", stateSetupRepository.findByIdNot(18L, Sort.by("stateName")));
					modelAndView.addObject("epicenters", epiCenterSetupRepository.findDistrictWiseEpiCenters(farmer.getNativeDistrict().getId()));
					modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
					modelAndView.setViewName("district/verifyapplicationdvo");
					return modelAndView;
				}
				
				@Transactional
				@PostMapping("verifyApplicationDVO")
				public ModelAndView verifyApplicationDVO(HttpSession session, FarmerDetailsForm farmerDetailsForm) {
					UserBean userBean = (UserBean) session.getAttribute("userBean");
					FarmerDetails farmerDetails=farmerDetailsForm.getFarmer();
					List<AnimalDetails>animalList=farmerDetailsForm.getAnimalDetails();
					if(farmerDetails.getEpiCenter()==null||farmerDetails.getEpiCenter().getId()==0){
						farmerDetails.setEpiCenter(null);
					}
					animalList = animalList.stream().filter(animal ->(animal.getLiveWeight()!=null)||(animal.getHeartGirth()!=null&&animal.getLength()!=null )).collect(Collectors.toList());
					if (!validateFarmerDetails(farmerDetails,animalList)) {
						ModelAndView modelAndView = new ModelAndView();
						if(animalList.size()==0) {
							animalList.add(new AnimalDetails());
						}
						farmerDetailsForm.setAnimalDetails(animalList);
						modelAndView.addObject("fieldErrors", fieldErrors);
						
						modelAndView.addObject("farmerDetails", farmerDetailsForm);
						modelAndView.addObject("states", stateSetupRepository.findByIdNot(18L, Sort.by("stateName")));
						modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
						modelAndView.setViewName("district/newapplication");
						if(farmerDetailsForm.getFarmer().getNativeDistrict()!=null) {
							modelAndView.addObject("epicenters", epiCenterSetupRepository.findDistrictWiseEpiCenters(farmerDetailsForm.getFarmer().getNativeDistrict().getId()));
							}else {
								modelAndView.addObject("epicenters", epiCenterSetupRepository.findDistrictWiseEpiCenters(userBean.getDistrict()));
							}
						modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
						modelAndView.setViewName("district/verifyapplicationdvo");
						return modelAndView;
					}
					
					farmerDetails.setVerifiedBy(userBean.getUserId());
					farmerDetails.setVerifiedOn(new Date());
					
//					For Saving History Start
					FarmerDetails farmerHistory=farmerDetailsRepository.getOne(farmerDetails.getId());
					FarmerDetailsHistory farmerDetailsHistory=new FarmerDetailsHistory();
					BeanUtils.copyProperties(farmerHistory, farmerDetailsHistory);
					farmerDetails.setSubmitOn(farmerHistory.getSubmitOn());
					farmerDetailsHistory.setDetailsId(farmerHistory.getId());
					farmerDetailsHistory.setId(null);
					farmerDetailsHistoryRepository.save(farmerDetailsHistory);
					List<AnimalDetails>animalHistoryList=animalDetailsRepository.getActiveAnimalDetailsByFarmer(farmerHistory.getId());
					for(AnimalDetails ad:animalHistoryList) {
						AnimalDetailsHistory adh=new AnimalDetailsHistory();
						BeanUtils.copyProperties(ad, adh);
						adh.setDetailsId(ad.getId());
						adh.setId(null);
						ad.setDeleteStatus("Y");
						ad.setModifiedOn(new Date());
						ad.setModifiedBy(userBean.getUserId());
						animalDetailsRepository.save(ad);
						animalDetailsHistoryRepository.save(adh);
					}
//					For Saving History Ends
					
					
					farmerDetailsRepository.save(farmerDetails);
					Integer animalCount=0;
					Long totalAmount=0l;
					for(AnimalDetails animal:animalList) {
						animalCount=animalCount+1;
						animal.setFarmerId(farmerDetails.getId());
						animal=getAndPopulateAnimalDetails(animal);
						totalAmount=totalAmount+animal.getAmount();
						animal.setModifiedOn(new Date());
						animal.setModifiedBy(userBean.getUserId());
						animal.setDeleteStatus("N");
						animalDetailsRepository.save(animal);
					}
					farmerDetails.setAmount(totalAmount);
					farmerDetails.setNoOfAnimal(animalCount);
					if (farmerDetails.getTrackingId() == null
							|| farmerDetails.getTrackingId().isBlank()) {
							farmerDetails.setTrackingId(
								"T" +
								CustomUtils.padCharacters(farmerDetails.getId().toString(), 7, '0', "L")
							);
						}
					farmerDetailsRepository.save(farmerDetails);
					return new ModelAndView("redirect:/district/getDistrictApplicationForVerificationList");
				}
				@RequestMapping(value = "getDetailsForApproval/{id}", method = { RequestMethod.GET })
				public ModelAndView getDetailsForApproval(HttpSession session,  @PathVariable Long id) {
					UserBean userBean = (UserBean) session.getAttribute("userBean");
//					session.setAttribute("tab", "applicationDC");
					ModelAndView modelAndView = new ModelAndView();
					FarmerDetails farmer = farmerDetailsRepository.getOne(id);
					ArrayList<AnimalDetails> animalDetailsList=animalDetailsRepository.getActiveAnimalDetailsByFarmer(id);
					FarmerDetailsForm form=new FarmerDetailsForm();
					form.setFarmer(farmer);
					form.setAnimalDetails(animalDetailsList);
					modelAndView.addObject("farmerDetails", form);
					modelAndView.addObject("epicenters", epiCenterSetupRepository.findDistrictWiseEpiCenters(farmer.getNativeDistrict().getId()));
					modelAndView.addObject("states", stateSetupRepository.findByIdNot(18L, Sort.by("stateName")));
					modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
					modelAndView.setViewName("district/approveapplicationdc");
					return modelAndView;
				}
				@Transactional
				@PostMapping("approveApplicationDC")
				public ModelAndView approveApplicationDC(HttpSession session, FarmerDetailsForm farmerDetailsForm) {
					UserBean userBean = (UserBean) session.getAttribute("userBean");
					FarmerDetails farmerDetails=farmerDetailsForm.getFarmer();
					List<AnimalDetails>animalList=farmerDetailsForm.getAnimalDetails();
					if(farmerDetails.getEpiCenter()==null||farmerDetails.getEpiCenter().getId()==0){
						farmerDetails.setEpiCenter(null);
					}
					animalList = animalList.stream().filter(animal ->(animal.getLiveWeight()!=null)||(animal.getHeartGirth()!=null&&animal.getLength()!=null )).collect(Collectors.toList());
					if (!validateFarmerDetails(farmerDetails,animalList)) {
						ModelAndView modelAndView = new ModelAndView();
						if(animalList.size()==0) {
							animalList.add(new AnimalDetails());
						}
						farmerDetailsForm.setAnimalDetails(animalList);
						modelAndView.addObject("fieldErrors", fieldErrors);
						
						modelAndView.addObject("farmerDetails", farmerDetailsForm);
						modelAndView.addObject("states", stateSetupRepository.findByIdNot(18L, Sort.by("stateName")));
						modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
						modelAndView.setViewName("district/newapplication");
						if(farmerDetailsForm.getFarmer().getNativeDistrict()!=null) {
						modelAndView.addObject("epicenters", epiCenterSetupRepository.findDistrictWiseEpiCenters(farmerDetailsForm.getFarmer().getNativeDistrict().getId()));
						}else {
							modelAndView.addObject("epicenters", epiCenterSetupRepository.findDistrictWiseEpiCenters(userBean.getDistrict()));
						}
						modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
						modelAndView.addObject("nativestations", policeStationSetupRepository.findAll(Sort.by("policeStationName")));
						modelAndView.setViewName("district/verifyapplicationdvo");
						return modelAndView;
					}
					
					farmerDetails.setAcceptedBy(userBean.getUserId());
					farmerDetails.setAcceptedOn(new Date());
					
//					For Saving History Start
					FarmerDetails farmerHistory=farmerDetailsRepository.getOne(farmerDetails.getId());
					FarmerDetailsHistory farmerDetailsHistory=new FarmerDetailsHistory();
					BeanUtils.copyProperties(farmerHistory, farmerDetailsHistory);
					
					farmerDetailsHistory.setDetailsId(farmerHistory.getId());
					farmerDetailsHistory.setId(null);
					farmerDetailsHistoryRepository.save(farmerDetailsHistory);
					List<AnimalDetails>animalHistoryList=animalDetailsRepository.getActiveAnimalDetailsByFarmer(farmerHistory.getId());
					for(AnimalDetails ad:animalHistoryList) {
						AnimalDetailsHistory adh=new AnimalDetailsHistory();
						BeanUtils.copyProperties(ad, adh);
						adh.setDetailsId(ad.getId());
						adh.setId(null);
						ad.setDeleteStatus("Y");
						ad.setModifiedOn(new Date());
						ad.setModifiedBy(userBean.getUserId());
						animalDetailsRepository.save(ad);
						animalDetailsHistoryRepository.save(adh);
					}
//					For Saving History Ends
					farmerHistory.setAcceptedBy(farmerDetails.getAcceptedBy());
					farmerHistory.setAcceptedOn(farmerDetails.getAcceptedOn());
					farmerHistory.setAcceptRemark(farmerDetails.getAcceptRemark());
					
					
					farmerDetailsRepository.save(farmerHistory);
					Integer animalCount=0;
					Long totalAmount=0l;
					for(AnimalDetails animal:animalList) {
						animalCount=animalCount+1;
						animal.setFarmerId(farmerDetails.getId());
						animal=getAndPopulateAnimalDetails(animal);
						totalAmount=totalAmount+animal.getAmount();
						animal.setModifiedOn(new Date());
						animal.setModifiedBy(userBean.getUserId());
						animal.setDeleteStatus("N");
						animalDetailsRepository.save(animal);
					}
					farmerHistory.setAmount(totalAmount);
					farmerHistory.setNoOfAnimal(animalCount);
					if (farmerDetails.getTrackingId() == null
							|| farmerDetails.getTrackingId().isBlank()) {
							farmerDetails.setTrackingId(
								"T" +
								CustomUtils.padCharacters(farmerDetails.getId().toString(), 7, '0', "L")
							);
						}
					farmerDetailsRepository.save(farmerHistory);
//					Messagepart
					try {
					String mobileNo=farmerHistory.getMobile().toString();
					String messageContent = templatesFundApproval.replaceAll("#FARMER1", farmerHistory.getName())
							.replaceAll("#AMT1", farmerHistory.getAmount().toString()).replaceAll("#ANIMAL1", farmerHistory.getNoOfAnimal().toString())
							.replaceAll("#TRACK1", farmerHistory.getTrackingId());
						MessageService.sendMessage(messageContent,
												   new String[] { mobileNo },
												   Provider.SMS_SERVETEL);
					} catch (UnknownHostException e) {
					} catch (UnsupportedEncodingException e) {
					}
//					Messagepart Ends
					return new ModelAndView("redirect:/district/getDistrictApplicationDCList");
				}
				
				
				@RequestMapping(value = "getNoPickupScheduledList", method = { RequestMethod.GET, RequestMethod.POST })
				public ModelAndView getNoPickupScheduledList(HttpSession session, Pagination pagination) {
					session.setAttribute("tab", "pickupSchedule");
					session.setAttribute("subtab", "notScheduled");

					UserBean userBean = (UserBean) session.getAttribute("userBean");

					ModelAndView modelAndView = new ModelAndView();

					if ((pagination == null || pagination.getPageTotal() == 0)
						&& !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
						pagination = new Pagination();
					}
					if (pagination.getPageTotal() == 0
						|| pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
						pagination.setPageTotal(farmerDetailsRepository.countByNativeDistrictSubmittedVerifiedApprovedAndNotScheduled(userBean.getDistrict(), pagination.getPageSearch()));
					}


					Pagination.updatePaginationAttributes(pagination);
					List<FarmerDetails> details = null;
					if (pagination.getPageTotal() > 0) {
						PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
						details = farmerDetailsRepository.findByNativeDistrictSubmittedVerifiedApprovedAndNotScheduled(userBean.getDistrict(), pagination.getPageSearch(), pageRequest);
						if (details != null) {
							pagination.setCurrentPageSize(details.size());
						}
					}

					modelAndView.addObject("pagination", pagination);
					modelAndView.addObject("details", details);
					modelAndView.setViewName("district/pickupscheduledvo");

					return modelAndView;
				}
				@RequestMapping(value = "getPickupScheduledList", method = { RequestMethod.GET, RequestMethod.POST })
				public ModelAndView getPickupScheduledList(HttpSession session, Pagination pagination) {
					session.setAttribute("tab", "pickupSchedule");
					session.setAttribute("subtab", "scheduled");
					UserBean userBean = (UserBean) session.getAttribute("userBean");

					ModelAndView modelAndView = new ModelAndView();

					if ((pagination == null || pagination.getPageTotal() == 0)
						&& !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
						pagination = new Pagination();
					}
					if (pagination.getPageTotal() == 0
						|| pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
						pagination.setPageTotal(farmerDetailsRepository.countByNativeDistrictSubmittedVerifiedAndScheduled(userBean.getDistrict(), pagination.getPageSearch()));
					}

					Pagination.updatePaginationAttributes(pagination);
					List<FarmerDetails> details = null;
					if (pagination.getPageTotal() > 0) {
						PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
						details = farmerDetailsRepository.findByNativeDistrictSubmittedVerifiedAndScheduled(userBean.getDistrict(), pagination.getPageSearch(), pageRequest);
						if (details != null) {
							pagination.setCurrentPageSize(details.size());
						}
					}
					modelAndView.addObject("pagination", pagination);
					modelAndView.addObject("details", details);
					modelAndView.setViewName("district/pickupscheduledvo");
					return modelAndView;
				}
				
				@RequestMapping(value = "gotoNewScheduleSingle/{id}", method = { RequestMethod.GET })
				public ModelAndView gotoNewScheduleSingle(HttpSession session, @PathVariable Long id) {
						UserBean userBean = (UserBean) session.getAttribute("userBean");
						ModelAndView modelAndView = new ModelAndView();
						FarmerDetails farmer = farmerDetailsRepository.getOne(id);
						ArrayList<AnimalDetails> animalDetailsList=animalDetailsRepository.getActiveAnimalDetailsByFarmer(id);
						FarmerDetailsForm form=new FarmerDetailsForm();
						form.setFarmer(farmer);
						form.setAnimalDetails(animalDetailsList);
						modelAndView.addObject("farmerDetails", form);
						modelAndView.addObject("states", stateSetupRepository.findByIdNot(18L, Sort.by("stateName")));
						modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
						modelAndView.addObject("cullingingcenters", cullingCentersRepository.findDistrictWiseCullingCenters(userBean.getDistrict()));
					modelAndView.setViewName("district/newsingleschedule");
					return modelAndView;
				}
				@Transactional
				@PostMapping("scheduleSingleApplicationDVO")
				public ModelAndView scheduleSingleApplicationDVO(HttpSession session, FarmerDetailsForm farmerDetailsForm) {
					UserBean userBean = (UserBean) session.getAttribute("userBean");
					FarmerDetails farmerDetails=farmerDetailsForm.getFarmer();
					if (!validateSchedule(farmerDetails)) {
						ModelAndView modelAndView = new ModelAndView();
						FarmerDetails farmer = farmerDetailsRepository.getOne(farmerDetails.getId());
						ArrayList<AnimalDetails> animalDetailsList=animalDetailsRepository.getActiveAnimalDetailsByFarmer(farmerDetails.getId());
						farmer.setPickupScheduledOn(farmerDetails.getPickupScheduledOn());
						if(farmerDetails.getCullingCenter()!=null) {
							farmer.setCullingCenter(new CullingCenters());
							farmer.getCullingCenter().setId(farmerDetails.getCullingCenter().getId());
						}
						farmer.setScheduleRemark(farmerDetails.getScheduleRemark());
						FarmerDetailsForm form=new FarmerDetailsForm();
						form.setFarmer(farmer);
						form.setAnimalDetails(animalDetailsList);
						modelAndView.addObject("fieldErrors", fieldErrors);
						modelAndView.addObject("farmerDetails", form);
						modelAndView.addObject("states", stateSetupRepository.findByIdNot(18L, Sort.by("stateName")));
						modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
						modelAndView.addObject("cullingingcenters", cullingCentersRepository.findDistrictWiseCullingCenters(userBean.getDistrict()));
						modelAndView.setViewName("district/newsingleschedule");
						return modelAndView;
					}
					
					SimpleDateFormat formatter  = new SimpleDateFormat("dd-MM-yyyy hh:mm a");  
					try {
						farmerDetails.setPickupScheduledOn(formatter.parse(farmerDetails.getPickupScheduledOnStr()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
//					For Saving History Start
					FarmerDetails farmerHistory=farmerDetailsRepository.getOne(farmerDetails.getId());
					FarmerDetailsHistory farmerDetailsHistory=new FarmerDetailsHistory();
					BeanUtils.copyProperties(farmerHistory, farmerDetailsHistory);
					farmerDetailsHistory.setDetailsId(farmerHistory.getId());
					farmerDetailsHistory.setId(null);
					farmerDetailsHistoryRepository.save(farmerDetailsHistory);
					List<AnimalDetails>animalHistoryList=animalDetailsRepository.getActiveAnimalDetailsByFarmer(farmerHistory.getId());
					for(AnimalDetails ad:animalHistoryList) {
						AnimalDetailsHistory adh=new AnimalDetailsHistory();
						BeanUtils.copyProperties(ad, adh);
						adh.setDetailsId(ad.getId());
						adh.setId(null);
						animalDetailsHistoryRepository.save(adh);
					}
//					For Saving History Ends
					
					
					farmerHistory.setPickupScheduledOn(farmerDetails.getPickupScheduledOn());
					farmerHistory.setScheduleRemark(farmerDetails.getScheduleRemark());
					farmerHistory.setPickupScheduledBy(userBean.getUserId());
					farmerHistory.setModifiedOn(new Date());
					farmerHistory.setModifiedBy(userBean.getUserId());
					farmerHistory.setCullingCenter(new CullingCenters());
					farmerHistory.getCullingCenter().setId(farmerDetails.getCullingCenter().getId());
					farmerDetailsRepository.save(farmerHistory);
					try {
						SimpleDateFormat ddMMYYYYFORM=new SimpleDateFormat("dd-MM-yyyy");
						String mobileNo=farmerHistory.getMobile().toString();
						String messageContent = templatesPickupSchedule.replaceAll("#FARMER1", farmerHistory.getName())
								.replaceAll("#SCHDATE1 ",  ddMMYYYYFORM.format(farmerHistory.getPickupScheduledOn()))
								.replaceAll("#TRACK1", farmerHistory.getTrackingId());
							MessageService.sendMessage(messageContent,
													   new String[] { mobileNo },
													   Provider.SMS_SERVETEL);
						} catch (UnknownHostException e) {
						} catch (UnsupportedEncodingException e) {
						}
					
					
					return new ModelAndView("redirect:/district/getNoPickupScheduledList");
				}
				private boolean validateSchedule(FarmerDetails farmerDetails) {
					boolean resultStatus = true;
					fieldErrors = new LinkedHashMap<>();
					if (farmerDetails.getPickupScheduledOnStr()== null
						|| farmerDetails.getPickupScheduledOnStr().toString().isBlank()) {
						fieldErrors.put("Schedule Date", "Invalid Pickup Schedule Date");
						resultStatus = false;
					}else {
						SimpleDateFormat formatter  = new SimpleDateFormat("dd-MM-yyyy hh:mm a");  
						try {
							farmerDetails.setPickupScheduledOn(formatter.parse(farmerDetails.getPickupScheduledOnStr()));
						} catch (ParseException e) {
							fieldErrors.put("Schedule Date", "Invalid Pickup Schedule Date");
							resultStatus = false;
						}  
					}
					if (farmerDetails.getCullingCenter() == null
						|| farmerDetails.getCullingCenter().getId() == null
						|| farmerDetails.getCullingCenter().getId() == 0) {
						fieldErrors.put("Culling Center Details", "Invalid Culling Center Details");
						resultStatus = false;
					}
					return resultStatus;
				}
				
				@RequestMapping(value = "getPickupScheduledCullingOfficeList", method = { RequestMethod.GET, RequestMethod.POST })
				public ModelAndView getPickupScheduledCullingOfficeList(HttpSession session, Pagination pagination) {
					session.setAttribute("tab", "scheduledCO");
//					session.setAttribute("subtab", "scheduled");
					UserBean userBean = (UserBean) session.getAttribute("userBean");

					ModelAndView modelAndView = new ModelAndView();

					if ((pagination == null || pagination.getPageTotal() == 0)
						&& !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
						pagination = new Pagination();
					}
					if (pagination.getPageTotal() == 0
						|| pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
						pagination.setPageTotal(farmerDetailsRepository.countBySubmittedVerifiedAndScheduledForCullingCenter(userBean.getCullingCenter(), pagination.getPageSearch()));
					}

					Pagination.updatePaginationAttributes(pagination);
					List<FarmerDetails> details = null;
					if (pagination.getPageTotal() > 0) {
						PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
						details = farmerDetailsRepository.findBySubmittedVerifiedAndScheduledForCullingCenter(userBean.getCullingCenter(), pagination.getPageSearch(), pageRequest);
						if (details != null) {
							pagination.setCurrentPageSize(details.size());
						}
					}
					modelAndView.addObject("pagination", pagination);
					modelAndView.addObject("details", details);
					modelAndView.setViewName("district/pickupscheduledco");
					return modelAndView;
				}
				
				@RequestMapping(value = "getCullingOfficeCollectedList", method = { RequestMethod.GET, RequestMethod.POST })
				public ModelAndView getCullingOfficeCollectedList(HttpSession session, Pagination pagination) {
					session.setAttribute("tab", "collectedCO");
//					session.setAttribute("subtab", "scheduled");
					UserBean userBean = (UserBean) session.getAttribute("userBean");

					ModelAndView modelAndView = new ModelAndView();

					if ((pagination == null || pagination.getPageTotal() == 0)
						&& !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
						pagination = new Pagination();
					}
					if (pagination.getPageTotal() == 0
						|| pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
						pagination.setPageTotal(farmerDetailsRepository.countByScheduledCollectedForCullingCenter(userBean.getCullingCenter(), pagination.getPageSearch()));
					}

					Pagination.updatePaginationAttributes(pagination);
					List<FarmerDetails> details = null;
					if (pagination.getPageTotal() > 0) {
						PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
						details = farmerDetailsRepository.findByScheduledCollectedForCullingCenter(userBean.getCullingCenter(), pagination.getPageSearch(), pageRequest);
						if (details != null) {
							pagination.setCurrentPageSize(details.size());
						}
					}
					modelAndView.addObject("pagination", pagination);
					modelAndView.addObject("details", details);
					modelAndView.setViewName("district/pickupcollectedco");
					return modelAndView;
				}
				
				@RequestMapping(value = "getCullingOfficeCulledList", method = { RequestMethod.GET, RequestMethod.POST })
				public ModelAndView getCullingOfficeCulledList(HttpSession session, Pagination pagination) {
					session.setAttribute("tab", "culledCO");
//					session.setAttribute("subtab", "scheduled");
					UserBean userBean = (UserBean) session.getAttribute("userBean");

					ModelAndView modelAndView = new ModelAndView();

					if ((pagination == null || pagination.getPageTotal() == 0)
						&& !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
						pagination = new Pagination();
					}
					if (pagination.getPageTotal() == 0
						|| pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
						pagination.setPageTotal(farmerDetailsRepository.countByScheduledCollectedCulledForCullingCenter(userBean.getCullingCenter(), pagination.getPageSearch()));
					}

					Pagination.updatePaginationAttributes(pagination);
					List<FarmerDetails> details = null;
					if (pagination.getPageTotal() > 0) {
						PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
						details = farmerDetailsRepository.findByScheduledCollectedCulledForCullingCenter(userBean.getCullingCenter(), pagination.getPageSearch(), pageRequest);
						if (details != null) {
							pagination.setCurrentPageSize(details.size());
						}
					}
					modelAndView.addObject("pagination", pagination);
					modelAndView.addObject("details", details);
					modelAndView.setViewName("district/pickupculledco");
					return modelAndView;
				}
				
				@RequestMapping(value = "gotoNewCollectedSingle/{id}", method = { RequestMethod.GET })
				public ModelAndView gotoNewCollectedSingle(HttpSession session, @PathVariable Long id) {
						UserBean userBean = (UserBean) session.getAttribute("userBean");
						ModelAndView modelAndView = new ModelAndView();
						FarmerDetails farmer = farmerDetailsRepository.getOne(id);
						ArrayList<AnimalDetails> animalDetailsList=animalDetailsRepository.getActiveAnimalDetailsByFarmer(id);
						FarmerDetailsForm form=new FarmerDetailsForm();
						form.setFarmer(farmer);
						form.setAnimalDetails(animalDetailsList);
						modelAndView.addObject("farmerDetails", form);
						modelAndView.addObject("states", stateSetupRepository.findByIdNot(18L, Sort.by("stateName")));
						modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
						modelAndView.addObject("cullingingcenters", cullingCentersRepository.findDistrictWiseCullingCenters(userBean.getDistrict()));
					modelAndView.setViewName("district/newcollectedsingle");
					return modelAndView;
				}
				@RequestMapping(value = "gotoNewCulledSingle/{id}", method = { RequestMethod.GET })
				public ModelAndView gotoNewCulledSingle(HttpSession session, @PathVariable Long id) {
						UserBean userBean = (UserBean) session.getAttribute("userBean");
						ModelAndView modelAndView = new ModelAndView();
						FarmerDetails farmer = farmerDetailsRepository.getOne(id);
						ArrayList<AnimalDetails> animalDetailsList=animalDetailsRepository.getActiveAnimalDetailsByFarmer(id);
						FarmerDetailsForm form=new FarmerDetailsForm();
						form.setFarmer(farmer);
						form.setAnimalDetails(animalDetailsList);
						modelAndView.addObject("farmerDetails", form);
						modelAndView.addObject("states", stateSetupRepository.findByIdNot(18L, Sort.by("stateName")));
						modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
						modelAndView.addObject("cullingingcenters", cullingCentersRepository.findDistrictWiseCullingCenters(userBean.getDistrict()));
					modelAndView.setViewName("district/newculledsingle");
					return modelAndView;
				}
				@Transactional
				@PostMapping("collectSingleApplicationCO")
				public ModelAndView collectSingleApplicationCO(HttpSession session, FarmerDetailsForm farmerDetailsForm) {
					UserBean userBean = (UserBean) session.getAttribute("userBean");
					FarmerDetails farmerDetails=farmerDetailsForm.getFarmer();
					List<AnimalDetails>animalList=farmerDetailsForm.getAnimalDetails();
					if(farmerDetails.getEpiCenter()==null||farmerDetails.getEpiCenter().getId()==0){
						farmerDetails.setEpiCenter(null);
					}
					animalList = animalList.stream().filter(animal ->(animal.getLiveWeight()!=null)||(animal.getHeartGirth()!=null&&animal.getLength()!=null )).collect(Collectors.toList());
					if (!validateCollection(farmerDetails)) {
						ModelAndView modelAndView = new ModelAndView();
						FarmerDetails farmer = farmerDetailsRepository.getOne(farmerDetails.getId());
						ArrayList<AnimalDetails> animalDetailsList=animalDetailsRepository.getActiveAnimalDetailsByFarmer(farmerDetails.getId());
						farmer.setCollectedOn(farmerDetails.getCollectedOn());
						farmer.setCollectedRemark(farmerDetails.getCollectedRemark());
						FarmerDetailsForm form=new FarmerDetailsForm();
						form.setFarmer(farmer);
						form.setAnimalDetails(animalDetailsList);
						modelAndView.addObject("fieldErrors", fieldErrors);
						modelAndView.addObject("farmerDetails", form);
						modelAndView.addObject("states", stateSetupRepository.findByIdNot(18L, Sort.by("stateName")));
						modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
						modelAndView.addObject("cullingingcenters", cullingCentersRepository.findDistrictWiseCullingCenters(userBean.getDistrict()));
						modelAndView.setViewName("district/newcollectedsingle");
						return modelAndView;
					}
					
					SimpleDateFormat formatter  = new SimpleDateFormat("dd-MM-yyyy hh:mm a");  
					try {
						farmerDetails.setCollectedOn(formatter.parse(farmerDetails.getCollectedOnStr()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
//					For Saving History Start
					FarmerDetails farmerHistory=farmerDetailsRepository.getOne(farmerDetails.getId());
					FarmerDetailsHistory farmerDetailsHistory=new FarmerDetailsHistory();
					BeanUtils.copyProperties(farmerHistory, farmerDetailsHistory);
					
					farmerDetailsHistory.setDetailsId(farmerHistory.getId());
					farmerDetailsHistory.setId(null);
					farmerDetailsHistoryRepository.save(farmerDetailsHistory);
					List<AnimalDetails>animalHistoryList=animalDetailsRepository.getActiveAnimalDetailsByFarmer(farmerHistory.getId());
					for(AnimalDetails ad:animalHistoryList) {
						AnimalDetailsHistory adh=new AnimalDetailsHistory();
						BeanUtils.copyProperties(ad, adh);
						adh.setDetailsId(ad.getId());
						adh.setId(null);
						ad.setDeleteStatus("Y");
						ad.setModifiedOn(new Date());
						ad.setModifiedBy(userBean.getUserId());
						animalDetailsRepository.save(ad);
						animalDetailsHistoryRepository.save(adh);
					}
//					For Saving History Ends
					
					Integer animalCount=0;
					Long totalAmount=0l;
					for(AnimalDetails animal:animalList) {
						animalCount=animalCount+1;
						animal.setFarmerId(farmerDetails.getId());
						animal=getAndPopulateAnimalDetails(animal);
						totalAmount=totalAmount+animal.getAmount();
						animal.setModifiedOn(new Date());
						animal.setModifiedBy(userBean.getUserId());
						animal.setDeleteStatus("N");
						animalDetailsRepository.save(animal);
					}
					farmerHistory.setAmount(totalAmount);
					farmerHistory.setNoOfAnimal(animalCount);
					farmerHistory.setCollectedOn(farmerDetails.getCollectedOn());
					farmerHistory.setCollectedBy(userBean.getUserId());
					farmerHistory.setCollectedRemark(farmerDetails.getCollectedRemark());
					farmerHistory.setModifiedOn(new Date());
					farmerHistory.setModifiedBy(userBean.getUserId());
					farmerDetailsRepository.save(farmerHistory);
					return new ModelAndView("redirect:/district/getPickupScheduledCullingOfficeList");
				}
				@Transactional
				@PostMapping("culledSingleApplicationCO")
				public ModelAndView culledSingleApplicationCO(HttpSession session, FarmerDetailsForm farmerDetailsForm) {
					UserBean userBean = (UserBean) session.getAttribute("userBean");
					FarmerDetails farmerDetails=farmerDetailsForm.getFarmer();
					if (!validateCulling(farmerDetails)) {
						ModelAndView modelAndView = new ModelAndView();
						FarmerDetails farmer = farmerDetailsRepository.getOne(farmerDetails.getId());
						ArrayList<AnimalDetails> animalDetailsList=animalDetailsRepository.getActiveAnimalDetailsByFarmer(farmerDetails.getId());
						farmer.setCulledOn(farmerDetails.getCulledOn());
						farmer.setCulledRemark(farmerDetails.getCulledRemark());
						if(farmerDetails.getCullingCenter()!=null) {
							farmer.setCullingCenter(new CullingCenters());
							farmer.getCullingCenter().setId(farmerDetails.getCullingCenter().getId());
						}
						FarmerDetailsForm form=new FarmerDetailsForm();
						form.setFarmer(farmer);
						form.setAnimalDetails(animalDetailsList);
						modelAndView.addObject("fieldErrors", fieldErrors);
						modelAndView.addObject("farmerDetails", form);
						modelAndView.addObject("states", stateSetupRepository.findByIdNot(18L, Sort.by("stateName")));
						modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
						modelAndView.addObject("cullingingcenters", cullingCentersRepository.findDistrictWiseCullingCenters(userBean.getDistrict()));
						modelAndView.setViewName("district/newculledsingle");
						return modelAndView;
					}
					
					SimpleDateFormat formatter  = new SimpleDateFormat("dd-MM-yyyy hh:mm a");  
					try {
						farmerDetails.setCulledOn(formatter.parse(farmerDetails.getCulledOnStr()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
//					For Saving History Start
					FarmerDetails farmerHistory=farmerDetailsRepository.getOne(farmerDetails.getId());
					FarmerDetailsHistory farmerDetailsHistory=new FarmerDetailsHistory();
					BeanUtils.copyProperties(farmerHistory, farmerDetailsHistory);
					farmerDetailsHistory.setDetailsId(farmerHistory.getId());
					farmerDetailsHistory.setId(null);
					farmerDetailsHistoryRepository.save(farmerDetailsHistory);
					List<AnimalDetails>animalHistoryList=animalDetailsRepository.getActiveAnimalDetailsByFarmer(farmerHistory.getId());
					for(AnimalDetails ad:animalHistoryList) {
						AnimalDetailsHistory adh=new AnimalDetailsHistory();
						BeanUtils.copyProperties(ad, adh);
						adh.setDetailsId(ad.getId());
						adh.setId(null);
						animalDetailsHistoryRepository.save(adh);
					}
//					For Saving History Ends
					
					
					farmerHistory.setCulledOn(farmerDetails.getCulledOn());
					farmerHistory.setCulledBy(userBean.getUserId());
					farmerHistory.setCulledRemark(farmerDetails.getCulledRemark());
					farmerHistory.setModifiedOn(new Date());
					farmerHistory.setModifiedBy(userBean.getUserId());
					farmerDetailsRepository.save(farmerHistory);
					return new ModelAndView("redirect:/district/getCullingOfficeCollectedList");
				}
				private boolean validateCollection(FarmerDetails farmerDetails) {
					boolean resultStatus = true;
					fieldErrors = new LinkedHashMap<>();
					if (farmerDetails.getCollectedOnStr()== null
							|| farmerDetails.getCollectedOnStr().toString().isBlank()) {
							fieldErrors.put("Schedule Date", "Invalid Collected Date");
							resultStatus = false;
						}else {
							SimpleDateFormat formatter  = new SimpleDateFormat("dd-MM-yyyy hh:mm a");  
							try {
								farmerDetails.setCollectedOn(formatter.parse(farmerDetails.getCollectedOnStr()));
							} catch (ParseException e) {
								fieldErrors.put("Culling Details", "Invalid Collected Date");
								resultStatus = false;
							}  
						}
					return resultStatus;
				}
				private boolean validateCulling(FarmerDetails farmerDetails) {
					boolean resultStatus = true;
					fieldErrors = new LinkedHashMap<>();
					if (farmerDetails.getCulledOnStr()== null
						|| farmerDetails.getCulledOnStr().toString().isBlank()) {
						fieldErrors.put("Schedule Date", "Invalid Culling Date");
						resultStatus = false;
					}else {
						SimpleDateFormat formatter  = new SimpleDateFormat("dd-MM-yyyy hh:mm a");  
						try {
							farmerDetails.setCulledOn(formatter.parse(farmerDetails.getCulledOnStr()));
						} catch (ParseException e) {
							fieldErrors.put("Culling Details", "Invalid Culling Date");
							resultStatus = false;
						}  
					}
					return resultStatus;
				}
				
				@RequestMapping(value = "gotoApplicationDetailsView", method = { RequestMethod.GET })
				public ModelAndView gotoApplicationDetailsView(HttpSession session,  Long detailsId) {
						UserBean userBean = (UserBean) session.getAttribute("userBean");
						ModelAndView modelAndView = new ModelAndView();
						FarmerDetails farmer = farmerDetailsRepository.getOne(detailsId);
						ArrayList<AnimalDetails> animalDetailsList=animalDetailsRepository.getActiveAnimalDetailsByFarmer(detailsId);
						FarmerDetailsForm form=new FarmerDetailsForm();
						form.setFarmer(farmer);
						form.setAnimalDetails(animalDetailsList);
						modelAndView.addObject("farmerDetails", form);
						modelAndView.addObject("states", stateSetupRepository.findByIdNot(18L, Sort.by("stateName")));
						modelAndView.addObject("nativedistricts", districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
						modelAndView.addObject("cullingingcenters", cullingCentersRepository.findDistrictWiseCullingCenters(userBean.getDistrict()));
					modelAndView.setViewName("district/applicationdetailsview");
					return modelAndView;
				}
				
				
}
