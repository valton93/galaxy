package com.kran.project.user.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kran.project.farmer.dto.FinanceReportForm;
import com.kran.project.farmer.entities.FarmerDetails;
import com.kran.project.farmer.entities.FinanceReport;
import com.kran.project.farmer.entities.FinanceReportDetails;
import com.kran.project.farmer.entities.FinanceUploadReport;
import com.kran.project.farmer.entities.repo.AnimalDetailsHistoryRepository;
import com.kran.project.farmer.entities.repo.AnimalDetailsRepository;
import com.kran.project.farmer.entities.repo.DistrictSetupRepository;
import com.kran.project.farmer.entities.repo.FarmerDetailsHistoryRepository;
import com.kran.project.farmer.entities.repo.FarmerDetailsRepository;
import com.kran.project.farmer.entities.repo.FinanceReportDetailsRepository;
import com.kran.project.farmer.entities.repo.FinanceReportRepository;
import com.kran.project.farmer.entities.repo.FinanceUploadReportRepository;
import com.kran.project.farmer.entities.repo.StateSetupRepository;
import com.kran.project.user.dto.Pagination;
import com.kran.project.user.dto.Pagination.PageStatusConstants;
import com.kran.project.user.dto.UserBean;
import com.kran.project.user.entities.repo.CarcassRateSetupRepository;
import com.kran.project.utilities.MessageService;
import com.kran.project.utilities.MessageService.Provider;

@Controller
@Service
@RequestMapping("finance")
@Scope("prototype")
public class FinanceController {
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
	FinanceReportRepository financeReportRepository;
	@Autowired
	FinanceReportDetailsRepository financeReportDetailsRepository;
	
	@Autowired
	FinanceUploadReportRepository financeUploadReportRepository;
	@Value("${templates.fundtransfersuccess}")
    private String templatesFundTransferSuccess;
	@Value("${templates.fundtransferfailed}")
    private String templatesFundTransferFailed;
	
	private Map<String, String> fieldErrors;
	
	@RequestMapping(value = "getNonInitiatedTransactionList", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getNonInitiatedTransactionList(HttpSession session, Pagination pagination) {
		session.setAttribute("tab", "transactionPending");
		@SuppressWarnings("unused")
		UserBean userBean = (UserBean) session.getAttribute("userBean");

		ModelAndView modelAndView = new ModelAndView();

		if ((pagination == null || pagination.getPageTotal() == 0)
			&& !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
			pagination = new Pagination();
		}
		if (pagination.getPageTotal() == 0
			|| pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
			pagination.setPageTotal(farmerDetailsRepository.countByFundTransferNotInitiated(pagination.getPageSearch()));
		}

		Pagination.updatePaginationAttributes(pagination);
		List<FarmerDetails> details = null;
		if (pagination.getPageTotal() > 0) {
			PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
			details = farmerDetailsRepository.findByFundTransferNotInitiated(pagination.getPageSearch(), pageRequest);
			if (details != null) {
				pagination.setCurrentPageSize(details.size());
			}
		}
		modelAndView.addObject("pagination", pagination);
		modelAndView.addObject("details", details);
		modelAndView.setViewName("finance/noninitiatedtransaction");
		return modelAndView;
	}
	@RequestMapping(value = "gotoInitiateTransaction", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView gotoInitiateTransaction(HttpSession session, Pagination pagination) {
		session.setAttribute("tab", "transactionPending");
		ModelAndView modelAndView = new ModelAndView();
		List<FarmerDetails> details = null;
		details = farmerDetailsRepository.findByFundTransferNotInitiatedAll();
		FinanceReportForm financeReportForm=new FinanceReportForm();
		financeReportForm.setDetails(details);
		modelAndView.addObject("financeDetails", financeReportForm);
		modelAndView.addObject("details", details);
		modelAndView.setViewName("finance/newinitiatetransaction");
		return modelAndView;
	}
	@Transactional
	@PostMapping("initiateTransaction")
	public ModelAndView initiateTransaction(HttpSession session, FinanceReportForm financeReportForm) {
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		FinanceReport financeReport=financeReportForm.getFinanceReport();
		List<FinanceReportDetails> financeReportDetails=financeReportForm.getFinanceReportDetails();
		financeReport.setDeleteStatus("N");
		financeReport.setNoOfBeneficiery(financeReportDetails.size());
		financeReport.setStatus("O");
		financeReport.setReportDate(new Date());
		financeReport.setGeneratedBy(userBean.getUserId());
		financeReport.setGeneratedOn(new Date());
		financeReport.setPendingCount(Long.valueOf(financeReportDetails.size()));
		financeReport.setSuccessCount(0l);
		financeReport.setRejectCount(0l);
		SimpleDateFormat dateFormatForfile = new SimpleDateFormat("ddMMyyyy-HHmm");
		final String documentFormat = "xls";
		String fileName="";
		fileName += financeReport.getName().replaceAll("[^a-zA-Z0-9]", "");
		fileName += "_dated_"+dateFormatForfile.format(financeReport.getReportDate());
		fileName += "." + documentFormat;
		financeReport.setFileName(fileName);
		financeReportRepository.save(financeReport);
		for(FinanceReportDetails frd:financeReportDetails) {
			frd.setReportId(financeReport.getId());
			frd.setEntryOn(new Date());
			FarmerDetails farmerDetails= farmerDetailsRepository.getOne(frd.getFarmerId());
			financeReportDetailsRepository.save(frd);
			farmerDetails.setFundTransferInitOn(frd.getEntryOn());
			farmerDetails.setFundTransferInitBy(userBean.getUserId());
			farmerDetails.setFundTransferStatus("I");
			farmerDetailsRepository.save(farmerDetails);
		}
			return new ModelAndView("redirect:/finance/getNonInitiatedTransactionList");	
	}
			
	@RequestMapping(value = "getInitiatedTransactionList", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getInitiatedTransactionList(HttpSession session, Pagination pagination) {
		session.setAttribute("tab", "transactionInitial");
		UserBean userBean = (UserBean) session.getAttribute("userBean");

		ModelAndView modelAndView = new ModelAndView();

		if ((pagination == null || pagination.getPageTotal() == 0)
			&& !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
			pagination = new Pagination();
		}
		if (pagination.getPageTotal() == 0
			|| pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
			pagination.setPageTotal(financeReportRepository.countByFundTransferInitiatedByFO(userBean.getUserId(),pagination.getPageSearch()));
		}

		Pagination.updatePaginationAttributes(pagination);
		List<FinanceReport> details = null;
		if (pagination.getPageTotal() > 0) {
			PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
			details = financeReportRepository.findByFundTransferInitiatedByFO(userBean.getUserId(),pagination.getPageSearch(), pageRequest);
			if (details != null) {
				pagination.setCurrentPageSize(details.size());
			}
		}
		modelAndView.addObject("pagination", pagination);
		modelAndView.addObject("details", details);
		modelAndView.setViewName("finance/initiatedtransaction");
		return modelAndView;
	}		
	
	@RequestMapping(value = "getCompletedTransactionList", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getCompletedTransactionList(HttpSession session, Pagination pagination) {
		session.setAttribute("tab", "transactionCompleted");
		UserBean userBean = (UserBean) session.getAttribute("userBean");

		ModelAndView modelAndView = new ModelAndView();

		if ((pagination == null || pagination.getPageTotal() == 0)
			&& !pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
			pagination = new Pagination();
		}
		if (pagination.getPageTotal() == 0
			|| pagination.getPageStatus().equals(PageStatusConstants.fsearch.toString())) {
			pagination.setPageTotal(financeReportRepository.countByFundTransferCompletedByFO(userBean.getUserId(),pagination.getPageSearch()));
		}

		Pagination.updatePaginationAttributes(pagination);
		List<FinanceReport> details = null;
		if (pagination.getPageTotal() > 0) {
			PageRequest pageRequest = PageRequest.of(pagination.getPageNumber() - 1, pagination.getPageSize(), Sort.by("id").ascending());
			details = financeReportRepository.findByFundTransferCompletedByFO(userBean.getUserId(),pagination.getPageSearch(), pageRequest);
			if (details != null) {
				pagination.setCurrentPageSize(details.size());
			}
		}
		modelAndView.addObject("pagination", pagination);
		modelAndView.addObject("details", details);
		modelAndView.setViewName("finance/completedtransactions");
		return modelAndView;
	}	
	
	
	@SuppressWarnings("unused")
	@RequestMapping(value = "gotoTransactionDetails/{id}", method = { RequestMethod.GET })
	public ModelAndView gotoTransactionDetails(HttpSession session, @PathVariable Long id) {
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		ModelAndView modelAndView = new ModelAndView();
		FinanceReportForm financeReportForm=new FinanceReportForm();
		fieldErrors=new LinkedHashMap<>();
		FinanceUploadReport financeUploadReport=financeUploadReportRepository.getNonUpdatedFinanceUploadReportForReportId( id);
		List<FinanceReportDetails> financeReportDetails=new ArrayList<>();;
		FinanceReport financeReport=financeReportRepository.getOne(id);
		if(financeUploadReport==null) {
			
		financeReportDetails=financeReportDetailsRepository.getAllActiveBeneficiaryListByReportId(financeReport.getId());
		}else {
			financeReportForm.setFinanceUploadReport(financeUploadReport);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			String dateAndTimeForFolder = sdf.format(financeReport.getGeneratedOn());
			Path destinationPath = Paths.get(File.separator + "home" + File.separator + "kran-pc"
					+ File.separator + "finance_transactions" + File.separator + dateAndTimeForFolder
					+ File.separator + "uploads"+financeUploadReport.getFileName());
			try {
				FileInputStream inputStream = new FileInputStream(new File(destinationPath.toString()));
				 @SuppressWarnings("resource")
				Workbook workbook = new XSSFWorkbook(inputStream);
				 Sheet sheet = workbook.getSheetAt(0);
				 Iterator<Row> rows = sheet.iterator();
   				 int rowNumber = 0;
   				
   				while (rows.hasNext()) {
   					FinanceReportDetails frd=null;
   	   				Map<String, String> fieldLocalErrors=new LinkedHashMap<String, String>();
					Row currentRow = rows.next();
					// skip header
					if (rowNumber == 0||rowNumber==1) {
						rowNumber++;
						continue;
					}
					Iterator<Cell> cellsInRow = currentRow.iterator();
					int cellIndex = 0;
					
					String trackingId="";
					while (cellsInRow.hasNext()) {
						Cell currentCell = cellsInRow.next();
						if(cellIndex==0||cellIndex==2||cellIndex==3) {
							cellIndex++;
							continue;
						}
						
						 if (cellIndex == 1) { 
							 trackingId=currentCell.getStringCellValue().toString();
							 frd=financeReportDetailsRepository.getBeneficiaryByTrackingIdAndReportId(trackingId, id);
							 if(frd==null) {
								 fieldLocalErrors.put("Invalid Tracking Id", "Sl No :"+(rowNumber-1)+" Tracking Id :"+trackingId);
								 break;
							 }
						 }
						 if(cellIndex==4) {
							 String ifscCode=currentCell.getStringCellValue().toString(); 
							 if(!ifscCode.equals(frd.getIfscCode())){
								 fieldLocalErrors.put("IFSC code not matching", "Sl No :"+(rowNumber-1)+" Tracking Id :"+trackingId);
							 }
						 }
						 if(cellIndex==5) {
							 String accountNo=currentCell.getStringCellValue().toString(); 
							 if(!accountNo.equals(frd.getAccountNumber())){
								 fieldLocalErrors.put("Account Number not matching", "Sl No :"+(rowNumber-1)+" Tracking Id :"+trackingId);
							 }
						 }
						 if(cellIndex==6) {
							 Long amount=(long) currentCell.getNumericCellValue(); 
							 if(Integer.parseInt(amount.toString())!=Integer.parseInt(frd.getAmount().toString())){
								 fieldLocalErrors.put("Amount not matching", "Sl No :"+(rowNumber-1)+" Tracking Id :"+trackingId);
							 }
						 }
						 if(cellIndex==7) {
							 if(frd.getStatus().equals("N")) {
								 String status=currentCell.getStringCellValue().toString(); 
							 if(status.equalsIgnoreCase("Pending")||status.equalsIgnoreCase("Success")||status.equalsIgnoreCase("Failed"))
								 if(status.equalsIgnoreCase("Pending")) {
									 frd.setStatus("N");
								 } else if(status.equalsIgnoreCase("Success")) {
									 frd.setStatus("S");
								 } else if(status.equalsIgnoreCase("Failed")) {
									 frd.setStatus("F");
								 }
							 }else {
								 fieldLocalErrors.put("Wrong Transaction Status ", "Sl No :"+(rowNumber-1)+" Tracking Id :"+trackingId);
							 }
						 }
						cellIndex++;
					}
					if(fieldLocalErrors.isEmpty()) {
						financeReportDetails.add(frd);
					}
					else {
						fieldErrors.putAll(fieldLocalErrors);
					}	
   				}
   				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		financeReportForm.setFinanceReport(financeReport);
		financeReportForm.setFinanceReportDetails(financeReportDetails);
		modelAndView.addObject("financeDetails", financeReportForm);
		modelAndView.setViewName("finance/transactiondetails");
			return modelAndView;	
	}
		
	 @PostMapping("upload") // //new annotation since 4.3
	    public String singleFileUpload(FinanceReportForm financeFileUploadForm,
				RedirectAttributes redirectAttributes) throws IOException {

			if (financeFileUploadForm.getFiles().length > 0) {
				for (MultipartFile file : financeFileUploadForm.getFiles()) {
					if (file.isEmpty()) {
						redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
						return "redirect:/finance/gotoTransactionDetails/"
								+ financeFileUploadForm.getFinanceReport().getId();
					} else {
						Long financeReportId = financeFileUploadForm.getFinanceReport().getId();
						FinanceReport financeReport = financeReportRepository.getOne(financeReportId);
						Path destinationPath = null;
						String fileName = null;
						String dateAndTimeForFolder = null;
						FileOutputStream fos = null;
						SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
						dateAndTimeForFolder = sdf.format(financeReport.getGeneratedOn());

						Workbook workbook = new XSSFWorkbook(file.getInputStream());
						boolean sheetCheck = true;

						Sheet sheet = workbook.getSheetAt(0);
						Iterator<Row> rows = sheet.iterator();

						int rowNumber = 0;
						while (rows.hasNext()) {
							Row currentRow = rows.next();
							// skip header
							if (rowNumber == 0) {
								rowNumber++;
								continue;
							}
							if (rowNumber == 1) {
								Iterator<Cell> cellsInRow = currentRow.iterator();
								int cellIndex = 0;
								while (cellsInRow.hasNext()) {

									Cell currentCell = cellsInRow.next();

									if (cellIndex == 0) { // ID
										if (!currentCell.getStringCellValue().equals("Sl No")) {
											redirectAttributes.addFlashAttribute("message",
													"Failed!!!... Please Upload the same format");
											sheetCheck = false;
											break;
										}
									} else if (cellIndex == 1) { // Name
										if (!currentCell.getStringCellValue().equals("TID")) {
											redirectAttributes.addFlashAttribute("message",
													"Failed!!!... Please Upload the same format");
											sheetCheck = false;
											break;
										}
									} else if (cellIndex == 2) { // Name
										if (!currentCell.getStringCellValue().equals("Mobile Number")) {
											redirectAttributes.addFlashAttribute("message",
													"Failed!!!... Please Upload the same format");
											sheetCheck = false;
											break;
										}
									} else if (cellIndex == 3) { // Name
										if (!currentCell.getStringCellValue().equals("Name")) {
											redirectAttributes.addFlashAttribute("message",
													"Failed!!!... Please Upload the same format");
											sheetCheck = false;
											break;
										}
									} else if (cellIndex == 4) { // Name
										if (!currentCell.getStringCellValue().equals("Bank IFSC")) {
											redirectAttributes.addFlashAttribute("message",
													"Failed!!!... Please Upload the same format");
											sheetCheck = false;
											break;
										}
										} else if (cellIndex == 5) { // Name
											if (!currentCell.getStringCellValue().equals("Bank Account")) {
												redirectAttributes.addFlashAttribute("message",
														"Failed!!!... Please Upload the same format");
												sheetCheck = false;
												break;
											}
										} else if (cellIndex == 6) { // Name
											if (!currentCell.getStringCellValue().equals("Amount")) {
												redirectAttributes.addFlashAttribute("message",
														"Failed!!!... Please Upload the same format");
												sheetCheck = false;
												break;
											}
										} else if (cellIndex == 7) { // Name
											if (!currentCell.getStringCellValue().equals("Transaction Status")) {
												redirectAttributes.addFlashAttribute("message",
														"Failed!!!... Please Upload the same format");
												sheetCheck = false;
												break;
											}
										} else if (cellIndex == 8) { // Name
											if (!currentCell.getStringCellValue().equals("Remarks")) {
												redirectAttributes.addFlashAttribute("message",
														"Failed!!!... Please Upload the same format");
												sheetCheck = false;
												break;
											}
										}

									cellIndex++;	
								}
								rowNumber++;
								break;
							}
						}
//			 Saving the File

						if (sheetCheck) {
							Long countFinanceUpload=financeUploadReportRepository.getCountOfUploadsForReportId(financeReportId);
							destinationPath = Paths.get(File.separator + "home" + File.separator + "kran-pc"
									+ File.separator + "finance_transactions" + File.separator + dateAndTimeForFolder
									+ File.separator + "uploads"+File.separator +countFinanceUpload+"");
							if (destinationPath.toFile().exists() == false) {
								Files.createDirectories(destinationPath);
							}
							MultipartFile[] fpFile = financeFileUploadForm.getFiles();
							fileName = fpFile[0].getOriginalFilename();

							@SuppressWarnings("unused")
							MediaType mediaType = MediaType.parseMediaType(
									"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
							fos = new FileOutputStream(destinationPath.toString() + "/" + fileName);
							workbook.write(fos);
//					CustomUtils.removeFile(filePath.toString());
							if (fos != null)
								fos.close();
							if (workbook != null)
								workbook.close();
							FinanceUploadReport finUpReport=new FinanceUploadReport();
							finUpReport.setFileName("/"+countFinanceUpload+"/"+fileName);
							finUpReport.setReportId(financeFileUploadForm.getFinanceReport().getId());
							finUpReport.setUploadDate(new Date());
							financeUploadReportRepository.save(finUpReport);
							redirectAttributes.addFlashAttribute("message",
									"Successfully Uploaded");
						}
					}

				}
			}
			return "redirect:/finance/gotoTransactionDetails/" + financeFileUploadForm.getFinanceReport().getId();
		}
	 
	 
	 @PostMapping("deleteFile") // //new annotation since 4.3
	    public String deleteFile(FinanceReportForm financeFileUploadForm,
				RedirectAttributes redirectAttributes) throws IOException {
		 	Long financeReportId = financeFileUploadForm.getFinanceReport().getId();
			FinanceUploadReport fur=financeUploadReportRepository.getNonUpdatedFinanceUploadReportForReportId(financeReportId);
			fur.setDeleteStatus("Y");
			financeUploadReportRepository.save(fur);
			redirectAttributes.addFlashAttribute("message",
					"Successfully Removed");
			return "redirect:/finance/gotoTransactionDetails/" + financeFileUploadForm.getFinanceReport().getId();
		}
	 
	 @PostMapping("updateTransactionDetails") // //new annotation since 4.3
	    public String updateTransactionDetails(HttpSession session,FinanceReportForm financeFileUploadForm,
				RedirectAttributes redirectAttributes) throws IOException {
		 UserBean userBean = (UserBean) session.getAttribute("userBean");
		 	Long financeReportId = financeFileUploadForm.getFinanceReport().getId();
		 	FinanceReport financeReport=financeReportRepository.getOne(financeReportId);
			FinanceUploadReport fur=financeUploadReportRepository.getNonUpdatedFinanceUploadReportForReportId(financeReportId);
			for(FinanceReportDetails financeReportDetails:financeFileUploadForm.getFinanceReportDetails()) {
				FinanceReportDetails frdDB= financeReportDetailsRepository.getOne(financeReportDetails.getId());
				if(financeReportDetails.getStatus().equals("S")||financeReportDetails.getStatus().equals("F")) {
					frdDB.setStatus(financeReportDetails.getStatus());
					frdDB.setRemark(financeReportDetails.getRemark());
					financeReportDetailsRepository.save(frdDB);
					FarmerDetails farmerDetails=farmerDetailsRepository.getOne(frdDB.getFarmerId());
					if(financeReportDetails.getStatus().equals("S")) {
					farmerDetails.setFundTransferStatus("S");
					farmerDetails.setFundTransferOn(new Date());
					farmerDetails.setFundTransferBy(userBean.getUserId());
					farmerDetailsRepository.save(farmerDetails);
					try {
						String mobileNo=farmerDetails.getMobile().toString();
						String messageContent = templatesFundTransferSuccess.replaceAll("#FARMER1", farmerDetails.getName())
								.replaceAll("#AMT1", farmerDetails.getAmount().toString()).replaceAll("#ANIMAL1", farmerDetails.getNoOfAnimal().toString())
								.replaceAll("#TRACK1", farmerDetails.getTrackingId());
							MessageService.sendMessage(messageContent,
													   new String[] { mobileNo },
													   Provider.SMS_SERVETEL);
						} catch (Exception e) {
						} 
					
					}else {
						farmerDetails.setFundTransferStatus("F");	
						farmerDetails.setFundTransferRejectOn(new Date());
						farmerDetails.setFundTransferRejectBy(userBean.getUserId());
						farmerDetails.setFundTransferRejectRemark(frdDB.getRemark());
						farmerDetailsRepository.save(farmerDetails);
						
						try {
							String mobileNo=farmerDetails.getMobile().toString();
							String messageContent = templatesFundTransferSuccess.replaceAll("#FARMER1", farmerDetails.getName())
									.replaceAll("#AMT1", farmerDetails.getAmount().toString()).replaceAll("#ANIMAL1", farmerDetails.getNoOfAnimal().toString())
									.replaceAll("#TRACK1", farmerDetails.getTrackingId());
								MessageService.sendMessage(messageContent,
														   new String[] { mobileNo },
														   Provider.SMS_SERVETEL);
							} catch (Exception e) {
							} 
					}
				}
			}
			Long successCount=0l;
			Long failureCount=0l;
			Long pendingCount=0l;
			successCount=financeReportDetailsRepository.getStatusCountForReportId(financeReportId,"S");
			failureCount=financeReportDetailsRepository.getStatusCountForReportId(financeReportId,"F");
			pendingCount=financeReportDetailsRepository.getStatusCountForReportId(financeReportId,"N");
			financeReport.setSuccessCount(successCount);
			financeReport.setRejectCount(failureCount);
			financeReport.setPendingCount(pendingCount);
			if(pendingCount==0) {
				financeReport.setStatus("C");
			}
			financeReportRepository.save(financeReport);
			if(fur!=null) {
				fur.setUpdateStatus("Y");
				fur.setUpdateDate(new Date());
				financeUploadReportRepository.save(fur);
			}
			redirectAttributes.addFlashAttribute("update",
					"Successfully Updated");
			return "redirect:/finance/gotoTransactionDetails/" + financeFileUploadForm.getFinanceReport().getId();
		}
	 
	 @RequestMapping(value = "gotoViewTransactionDetails/{id}", method = { RequestMethod.GET })
		public ModelAndView gotoViewTransactionDetails(HttpSession session, @PathVariable Long id) {
			ModelAndView modelAndView = new ModelAndView();
			FinanceReportForm financeReportForm=new FinanceReportForm();
			List<FinanceReportDetails> financeReportDetails=new ArrayList<>();;
			FinanceReport financeReport=financeReportRepository.getOne(id);
			financeReportDetails=financeReportDetailsRepository.getAllActiveBeneficiaryListByReportId(financeReport.getId());
			financeReportForm.setFinanceReport(financeReport);
			financeReportForm.setFinanceReportDetails(financeReportDetails);
			modelAndView.addObject("financeDetails", financeReportForm);
			modelAndView.setViewName("finance/viewtransactiondetails");
				return modelAndView;	
		}
	
}
