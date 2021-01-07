package com.kran.project.user.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kran.project.farmer.entities.FarmerDetails;
import com.kran.project.farmer.entities.FinanceReport;
import com.kran.project.farmer.entities.FinanceReportDetails;
import com.kran.project.farmer.entities.repo.AnimalDetailsRepository;
import com.kran.project.farmer.entities.repo.DistrictSetupRepository;
import com.kran.project.farmer.entities.repo.FarmerDetailsRepository;
import com.kran.project.farmer.entities.repo.FinanceReportDetailsRepository;
import com.kran.project.farmer.entities.repo.FinanceReportRepository;
import com.kran.project.report.service.CustomReportDetailsService;
import com.kran.project.user.dto.FilterVO;
import com.kran.project.user.entities.repo.CullingCenterUsersRepository;
import com.kran.project.user.entities.repo.DistrictUsersRepository;
import com.kran.project.utilities.CustomUtils;



@Controller
@RequestMapping("report")
@Scope("prototype")
public class ReportsExcelController {
	@Autowired
	FarmerDetailsRepository farmerDetailsRepository;
	@Autowired
	AnimalDetailsRepository animalDetailsRepository;
	@Autowired
	DistrictSetupRepository districtSetupRepository;
	@Autowired
	DistrictUsersRepository districtUsersRepository;
	@Autowired
	CullingCenterUsersRepository cullingCenterUsersRepository;
	@Autowired
	FinanceReportRepository financeReportRepository ;
	@Autowired
	FinanceReportDetailsRepository financeReportDetailsRepository ;
	
	@Autowired
	CustomReportDetailsService customReportDetailsService;

	@Autowired
	ServletContext servletContext;

	
	@GetMapping("getReports")
	public ModelAndView getReports(HttpSession session, FilterVO filterVO) {
		session.setAttribute("tab", "report");

		ModelAndView modelAndView = new ModelAndView();

		modelAndView.addObject("nativedistricts",
				districtSetupRepository.findByStateCodeId(18L, Sort.by("districtName")));
		modelAndView.addObject("filterVO", new FilterVO());
		modelAndView.setViewName("report/reports");

		return modelAndView;
	}
	
	@PostMapping("generateReportOne")
	public ResponseEntity<InputStreamResource> generateReportOne(FilterVO filterVO) {

		FileOutputStream fos = null;
		Workbook workbook = null;

		Path destinationPath = null;
		Path filePath = null;
		String fileName = null;
		String temp = "";
		String dateAndTimeForFolder =null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		List<FarmerDetails> farmerDetails=customReportDetailsService.getFarmerDetails(filterVO);
		
		
		try {
			fileName = "Master_Report_";
			dateAndTimeForFolder = sdf.format(new Date());
			fileName=fileName+dateAndTimeForFolder.toString()+".xls";
			destinationPath = Paths.get(File.separator + "home" + File.separator + "kran-pc" + File.separator
					+ "master_report" + File.separator + dateAndTimeForFolder);
			if (destinationPath.toFile().exists() == false) {
				Files.createDirectories(destinationPath);
			}

			filePath = Paths.get(destinationPath + File.separator + fileName);
			if (filePath.toFile().exists() == false) {
				
			workbook = new XSSFWorkbook();
			CellStyle defaultTitleCellStyle=this.defaultTitleCellStyle(workbook);
			CellStyle defaultHeaderCellStyle=this.defaultHeaderCellStyle(workbook);
			CellStyle defaultDataCellStyle=this.defaultDataCellStyle(workbook);
			
			Font headFont=this.getHeaderFont(workbook);
			Font titleFont=this.getTitleFont(workbook);
			Font contentFont=this.getContentFont(workbook);
			
			defaultTitleCellStyle.setFont(titleFont);
			defaultHeaderCellStyle.setFont(headFont);
			defaultDataCellStyle.setFont(contentFont);
			
			Sheet sheet = workbook.createSheet("Master Report");
			Row row = null;
			Cell cell = null;
			
			row = sheet.getRow(0);
			if (row == null) {
				row = sheet.createRow(0);
			}
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 35));
			cell = row.createCell(0);
			cell.getRow().setHeight((short)0);
			cell.setCellValue("Master Report Generated On -"+dateAndTimeForFolder.toString());
			cell.setCellStyle(defaultTitleCellStyle);
			row = sheet.getRow(1);
			if (row == null) {
				row = sheet.createRow(1);
			}
			cell = row.createCell(0);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Sl No");
			
			cell = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("TID");

			cell = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Mobile Number");
			
			cell = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Name");

			cell = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Address");

			cell = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("District");
			
			cell = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Epicenter");
			
			cell = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Circle");
			
			cell = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Block");
			
			cell = row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Rural/Urban");
			
			cell = row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Village");
			
			cell = row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Gram Panchayath");
			
			cell = row.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("City/Town/Local Body");
			
			cell = row.getCell(13, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Ward");
			
			cell = row.getCell(14, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Pincode");
			
			cell = row.getCell(15, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Category");

			cell = row.getCell(16, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Farm Category");
			
			cell = row.getCell(17, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Latitude");
			
			cell = row.getCell(18, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Longitude");
			
			cell = row.getCell(19, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Bank IFSC");
			
			cell = row.getCell(20, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Bank Account Number");
			
			cell = row.getCell(21, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Total Animal ");
			
			cell = row.getCell(22, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Amount");
			
			cell = row.getCell(23, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Submit Status");
			
			cell = row.getCell(24, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Submit On ");
			
			cell = row.getCell(25, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Verify Status");
			
			cell = row.getCell(26, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Verified On");
			
			cell = row.getCell(27, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Approve Status");
			
			cell = row.getCell(28, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Approved On");
			
			cell = row.getCell(29, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Fund Transfer Status");
			
			cell = row.getCell(30, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Fund Transfer Updated On");
			
			cell = row.getCell(31, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("PickUp Scheduled On");
			
			
			cell = row.getCell(32, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Collection Status");
			
			cell = row.getCell(33, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Collected On");
			
			cell = row.getCell(34, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Culling Status");
			
			cell = row.getCell(35, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellStyle(defaultHeaderCellStyle);
			cell.setCellValue("Culled On");
			
			int startRow = 2;
			if (farmerDetails != null && !farmerDetails.isEmpty()) {
				for (FarmerDetails frd : farmerDetails) {

					row = sheet.getRow(startRow);
					if (row == null) {
						row = sheet.createRow(startRow);
					}
					cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(startRow - 1);
					
					cell = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(frd.getTrackingId());
					
					cell = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(frd.getMobile());
					
					cell = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(frd.getName());
					
					cell = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(frd.getNativeAddress());
					
					cell = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(frd.getNativeDistrict().getDistrictName());
					
					cell = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					if(frd.getEpiCenter()==null) {
						cell.setCellValue("--");
					}else {
					cell.setCellValue(frd.getEpiCenter().getName());
					}
					
					cell = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(frd.getNativeCircle());
					
					cell = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(frd.getNativeBlock());
					
					cell = row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					if(frd.getNativeArea().equals("R")) {
					cell.setCellValue("Rural");
					}else {
						cell.setCellValue("Urban");
					}
					
					if(frd.getNativeArea().equals("R")) {
						cell = row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue(frd.getNativeVillage());
						
						cell = row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue(frd.getNativePanchayat());
						
						cell = row.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("--");
						
						cell = row.getCell(13, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("--");
					}else {
						cell = row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("--");
						
						cell = row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("--");
						
						cell = row.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue(frd.getNativeCity());
						
						cell = row.getCell(13, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue(frd.getNativeWard());
					}
					
					cell = row.getCell(14, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					if(frd.getNativePincode()==null) {
						cell.setCellValue("--");
					}else {
					cell.setCellValue(frd.getNativePincode());
					}
					cell = row.getCell(15, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					if(frd.getFarmerCategory().equals("G")) {
						cell.setCellValue("General");
					}else if(frd.getFarmerCategory().equals("SC"))
					{
						cell.setCellValue("SC");
					}else if(frd.getFarmerCategory().equals("ST"))
					{
						cell.setCellValue("ST");
					}
					
					cell = row.getCell(16, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					if(frd.getFarmCategory().equals("B")) {
						cell.setCellValue("Backyard");
					}else if(frd.getFarmCategory().equals("F"))
					{
						cell.setCellValue("Farm");
					}
					
					cell = row.getCell(17, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(frd.getLat());
					
					cell = row.getCell(18, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(frd.getLng());
					
					cell = row.getCell(19, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(frd.getIfscCode());
					
					cell = row.getCell(20, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(frd.getAccountNumber());
					
					cell = row.getCell(21, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(frd.getNoOfAnimal());
					
					cell = row.getCell(22, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(frd.getAmount());
					
					
					if(frd.getSubmitOn()==null) {
						cell = row.getCell(23, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("Not Submitted");	
					
					cell = row.getCell(24, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue("--");
					}
					else {
						cell = row.getCell(23, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("Submitted");	
					
					cell = row.getCell(24, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(sdf.format(frd.getSubmitOn()));
					}
					if(frd.getVerifiedOn()==null) {
						cell = row.getCell(25, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("Not Verified");	
					
					cell = row.getCell(26, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue("--");
					}
					else {
						cell = row.getCell(25, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("Verified");	
					
					cell = row.getCell(26, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(sdf.format(frd.getVerifiedOn()));
					}
					
					if(frd.getVerifiedOn()==null) {
						cell = row.getCell(27, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("Not Approved");	
					
					cell = row.getCell(28, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue("--");
					}
					else {
						cell = row.getCell(27, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("Approved");	
					
					cell = row.getCell(28, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(sdf.format(frd.getVerifiedOn()));
					}
					
					if(frd.getFundTransferStatus().equals("N")) {
						cell = row.getCell(29, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("Not Initiated");	
					
					cell = row.getCell(30, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue("--");
					}
					else if(frd.getFundTransferStatus().equals("I")) {
						cell = row.getCell(29, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("Processing");	
					
					cell = row.getCell(30, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(sdf.format(frd.getFundTransferInitOn()));
					}
					else if(frd.getFundTransferStatus().equals("S"))
					{
						cell = row.getCell(29, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("Success");	
					
					cell = row.getCell(30, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(sdf.format(frd.getFundTransferOn()));
					}else if(frd.getFundTransferStatus().equals("F")) {
						cell = row.getCell(29, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("Failed");	
					
					cell = row.getCell(30, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue(sdf.format(frd.getFundTransferRejectOn()));
					}
					
					if(frd.getPickupScheduledOn()==null) {
						cell = row.getCell(31, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("--");	
						
						cell = row.getCell(32, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("Not Scheduled");
					
					cell = row.getCell(33, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue("--");
					
					cell = row.getCell(34, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue("Pending");
					
					cell = row.getCell(35, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellStyle(defaultDataCellStyle);
					cell.setCellValue("--");
					}
					else {
						
						cell = row.getCell(31, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue(sdf.format(frd.getPickupScheduledOn()));	
						
						if(frd.getCollectedOn()==null) {
							cell = row.getCell(32, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
							cell.setCellStyle(defaultDataCellStyle);
							cell.setCellValue("Collection Pending");
						
						cell = row.getCell(33, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("--");
						
						cell = row.getCell(34, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("Pending");
						
						cell = row.getCell(35, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("--");
						}else {
							cell = row.getCell(32, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
							cell.setCellStyle(defaultDataCellStyle);
							cell.setCellValue("Collected");
							
							cell = row.getCell(33, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
							cell.setCellStyle(defaultDataCellStyle);
							cell.setCellValue(sdf.format(frd.getCollectedOn()));
							if(frd.getCulledOn()==null) {
								cell = row.getCell(34, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
								cell.setCellStyle(defaultDataCellStyle);
								cell.setCellValue("Pending");
								
								cell = row.getCell(35, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
								cell.setCellStyle(defaultDataCellStyle);
								cell.setCellValue("--");
							}else {
								cell = row.getCell(34, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
								cell.setCellStyle(defaultDataCellStyle);
								cell.setCellValue("Culled");
								
								cell = row.getCell(35, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
								cell.setCellStyle(defaultDataCellStyle);
								cell.setCellValue(sdf.format(frd.getCulledOn()));
							}
						
						}
						
						
					}
					
					
					startRow++;
				}
				
			}
			

				sheet.setColumnWidth(1, 10 * 256);
				sheet.setColumnWidth(2, 20 * 256);
				sheet.setColumnWidth(3, 20 * 256);
				sheet.setColumnWidth(4, 20 * 256);
				sheet.setColumnWidth(5, 20 * 256);
				sheet.setColumnWidth(6, 20 * 256);
				sheet.setColumnWidth(7, 20 * 256);
				sheet.setColumnWidth(8, 30 * 256);
				for(int i=9;i<=35;i++) {
					sheet.setColumnWidth(i, 20 * 256);
				}
				sheet.setColumnWidth(30, 35 * 256);

			}else {

			    InputStream inp = new FileInputStream(filePath.toString()); 
			    workbook = WorkbookFactory.create(inp); 
				//read the existing file
			}

			fos = new FileOutputStream(filePath.toString());
			workbook.write(fos);

			File file = new File(filePath.toString());
			MediaType mediaType = MediaType
					.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
					.contentType(mediaType).contentLength(file.length()).body(resource);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		} finally {
			CustomUtils.removeFile(filePath.toString());
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (workbook != null)
				try {
					workbook.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		


	}
	
	
	@GetMapping("generateFinanceInitiateTransactionReport/{id}")
	public ResponseEntity<InputStreamResource> generateFinanceInitiateTransactionReport(@PathVariable Long id) throws Exception {
			FileOutputStream fos = null;
			Workbook workbook = null;

			Path destinationPath = null;
			Path filePath = null;
			String fileName = null;
			String temp = "";
			String dateAndTimeForFolder =null;
			FinanceReport financeReport=financeReportRepository.getOne(id);
			ArrayList<FinanceReportDetails> financeReportDetails=  financeReportDetailsRepository.getAllActiveBeneficiaryListByReportId(financeReport.getId());
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			try {
				fileName = financeReport.getFileName();
				dateAndTimeForFolder = sdf.format(financeReport.getGeneratedOn());
				destinationPath = Paths.get(File.separator + "home" + File.separator + "kran-pc" + File.separator
						+ "finance_transactions" + File.separator + dateAndTimeForFolder);
				if (destinationPath.toFile().exists() == false) {
					Files.createDirectories(destinationPath);
				}

				filePath = Paths.get(destinationPath + File.separator + fileName);
				if (filePath.toFile().exists() == false) {
					
				workbook = new XSSFWorkbook();
				CellStyle defaultTitleCellStyle=this.defaultTitleCellStyle(workbook);
				CellStyle defaultHeaderCellStyle=this.defaultHeaderCellStyle(workbook);
				CellStyle defaultDataCellStyle=this.defaultDataCellStyle(workbook);
				
				Font headFont=this.getHeaderFont(workbook);
				Font titleFont=this.getTitleFont(workbook);
				Font contentFont=this.getContentFont(workbook);
				
				defaultTitleCellStyle.setFont(titleFont);
				defaultHeaderCellStyle.setFont(headFont);
				defaultDataCellStyle.setFont(contentFont);
				
				Sheet sheet = workbook.createSheet("Beneficiary List");
				Row row = null;
				Cell cell = null;
				
				row = sheet.getRow(0);
				if (row == null) {
					row = sheet.createRow(0);
				}
				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 8));
				cell = row.createCell(0);
				cell.setCellValue("Beneficiary List");
				cell.setCellStyle(defaultTitleCellStyle);
				row = sheet.getRow(1);
				if (row == null) {
					row = sheet.createRow(1);
				}
				cell = row.createCell(0);
				cell.setCellStyle(defaultHeaderCellStyle);
				cell.setCellValue("Sl No");
				
				cell = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cell.setCellStyle(defaultHeaderCellStyle);
				cell.setCellValue("TID");

				cell = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cell.setCellStyle(defaultHeaderCellStyle);
				cell.setCellValue("Mobile Number");
				
				cell = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cell.setCellStyle(defaultHeaderCellStyle);
				cell.setCellValue("Name");

				cell = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cell.setCellStyle(defaultHeaderCellStyle);
				cell.setCellValue("Bank IFSC");

				cell = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cell.setCellStyle(defaultHeaderCellStyle);
				cell.setCellValue("Bank Account");
				
				cell = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cell.setCellStyle(defaultHeaderCellStyle);
				cell.setCellValue("Amount");
				
				cell = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cell.setCellStyle(defaultHeaderCellStyle);
				cell.setCellValue("Transaction Status");
				
				cell = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cell.setCellStyle(defaultHeaderCellStyle);
				cell.setCellValue("Remarks");

				
				int startRow = 2;
				if (financeReportDetails != null && !financeReportDetails.isEmpty()) {
					for (FinanceReportDetails frd : financeReportDetails) {

						row = sheet.getRow(startRow);
						if (row == null) {
							row = sheet.createRow(startRow);
						}
						cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue(startRow - 1);
						
						cell = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue(frd.getTrackingId());
						
						cell = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue(frd.getMobile());
						
						cell = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue(frd.getName());
						
						cell = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue(frd.getIfscCode());
						
						cell = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue(frd.getAccountNumber());
						
						cell = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue(frd.getAmount());
						
						cell = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("Pending");
						
						cell = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellStyle(defaultDataCellStyle);
						cell.setCellValue("");

						startRow++;
					}
					
				}
				

				XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) row.getSheet());
				XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(new String[]{"Pending","Success","Failed"});

				CellRangeAddressList addressList = new CellRangeAddressList(2, startRow-1, 7, 7);

				XSSFDataValidation validation = (XSSFDataValidation)dvHelper.createValidation(
				dvConstraint, addressList);
				validation.setShowErrorBox(true);
				validation.createErrorBox("Invalid Input", "Please Update The Transaction Status");

				sheet.addValidationData(validation);

					sheet.setColumnWidth(1, 10 * 256);
					sheet.setColumnWidth(2, 20 * 256);
					sheet.setColumnWidth(3, 20 * 256);
					sheet.setColumnWidth(4, 20 * 256);
					sheet.setColumnWidth(5, 20 * 256);
					sheet.setColumnWidth(6, 20 * 256);
					sheet.setColumnWidth(7, 20 * 256);
					sheet.setColumnWidth(8, 30 * 256);

				}else {

				    InputStream inp = new FileInputStream(filePath.toString()); 
				    workbook = WorkbookFactory.create(inp); 
					//read the existing file
				}

				fos = new FileOutputStream(filePath.toString());
				workbook.write(fos);

				File file = new File(filePath.toString());
				MediaType mediaType = MediaType
						.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

				return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
						.contentType(mediaType).contentLength(file.length()).body(resource);

			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			} finally {
//				CustomUtils.removeFile(filePath.toString());
				if (fos != null)
					fos.close();
				if (workbook != null)
					workbook.close();
			}

	}
	
	
	protected CellStyle defaultTitleCellStyle(final Workbook wb) {
	    CellStyle style;
	    style = wb.createCellStyle();
	    style.setAlignment(HorizontalAlignment.CENTER);
	    style.setVerticalAlignment(VerticalAlignment.CENTER);
	    return style;
	}
	protected CellStyle defaultDataCellStyle(final Workbook wb) {
	    CellStyle style;
	    style = wb.createCellStyle();
	    style.setAlignment(HorizontalAlignment.CENTER);
	    style.setWrapText(true);
	    style.setBorderRight(BorderStyle.THIN);
	    style.setRightBorderColor(IndexedColors.BLACK.getIndex());
	    style.setBorderLeft(BorderStyle.THIN);
	    style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
	    style.setBorderTop(BorderStyle.THIN);
	    style.setTopBorderColor(IndexedColors.BLACK.getIndex());
	    style.setBorderBottom(BorderStyle.THIN);
	    style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//	    style.setDataFormat(doubleDataFormat);
	    return style;
	}
	protected CellStyle defaultHeaderCellStyle(final Workbook wb) {
	    CellStyle style;
	    style = wb.createCellStyle();
	    style.setAlignment(HorizontalAlignment.CENTER);
	    style.setVerticalAlignment(VerticalAlignment.CENTER);
	    style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setWrapText(true);
	    return style;
	}
	
	protected Font getTitleFont(final Workbook wb) {
		Font headerFont = wb.createFont();
		headerFont.setFontName("Calibri");
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setBold(true);
		return headerFont;
	}
	protected Font getHeaderFont(final Workbook wb) {
		Font headerFont = wb.createFont();
		headerFont.setFontName("Calibri");
		headerFont.setFontHeightInPoints((short) 10);
		headerFont.setBold(true);
		headerFont.setColor( HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		return headerFont;
	}
	protected Font getContentFont(final Workbook wb) {
		Font cellFont = wb.createFont();
		cellFont.setFontName("Calibri");
		cellFont.setFontHeightInPoints((short) 10);
		cellFont.setBold(false);;
		return cellFont;
	}
	
	protected Font getMonthFont(final Workbook wb) {
		Font monthFont = wb.createFont();
		monthFont.setFontHeightInPoints((short) 11);
		monthFont.setColor(IndexedColors.WHITE.getIndex());
		return monthFont;
	}
	


	
							
}
