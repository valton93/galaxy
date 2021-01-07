package com.kran.project.user.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.kran.project.farmer.entities.AnimalDetails;
import com.kran.project.farmer.entities.FarmerDetails;
import com.kran.project.farmer.entities.repo.AnimalDetailsRepository;
import com.kran.project.farmer.entities.repo.DistrictSetupRepository;
import com.kran.project.farmer.entities.repo.FarmerDetailsRepository;
import com.kran.project.report.service.CustomReportDetailsService;
import com.kran.project.user.dto.FilterVO;
import com.kran.project.user.entities.CullingCenterUsers;
import com.kran.project.user.entities.DistrictUsers;
import com.kran.project.user.entities.repo.CullingCenterUsersRepository;
import com.kran.project.user.entities.repo.DistrictUsersRepository;
import com.kran.project.utilities.CustomUtils;


@Controller
@RequestMapping("report")
@Scope("prototype")
public class ReportsController {
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
	ServletContext servletContext;

	
	@GetMapping("generateEntryForm/{id}")
	public ResponseEntity<InputStreamResource> generateEntryForm(@PathVariable Long id) throws Exception {
		if (id != null && id > 0) {
			Optional<FarmerDetails> migrant = farmerDetailsRepository.findById(id);
			if (migrant.isPresent()) {
				FarmerDetails migrantDetail = migrant.get();

				Path destinationPath = null;
				Path filePath = null;
				String fileName = null;

				SimpleDateFormat dateFormatForFolder = new SimpleDateFormat("ddMMyyyy-HHmm");
				String dateAndTimeForFolder = dateFormatForFolder.format(new Date());

				final String documentFormat = "pdf";

				SimpleDateFormat generatedFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
				String tempString;
				
//				destinationPath = Paths.get(File.separator + "home" + File.separator + "kran-mfp-pc1" + File.separator	+ "temp_uploads" + File.separator + dateAndTimeForFolder);

				destinationPath = Paths.get(File.separator + "home" +  File.separator + "kran-pc" + File.separator	+ "temp_uploads" + File.separator + dateAndTimeForFolder);
				if (destinationPath.toFile().exists() == false) {
					Files.createDirectories(destinationPath);
				}

				fileName = "";
				filePath = null;

				/*** FILENAME ***/
				if (migrantDetail.getMobile() != null) {
					fileName += migrantDetail.getMobile();
				} else {
					fileName += "NULL";
				}

				fileName += "_";
				fileName += migrantDetail.getName().replaceAll("[^a-zA-Z0-9]", "");
				fileName += "." + documentFormat;
				/*** FILENAME ***/

				filePath = Paths.get(destinationPath + File.separator + migrantDetail.getMobile());
				if (!Files.exists(filePath)) {
					Files.createDirectories(filePath);
				}

				filePath = Paths.get(filePath + File.separator + fileName);

				try (FileOutputStream outputStream = new FileOutputStream(filePath.toString());
						PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
						Document document = new Document(pdfDocument, PageSize.A4);) {
					PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
					PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

					tempString = "Generated on " + generatedFormat.format(new Date());
					Paragraph header = new Paragraph(tempString);
					header.setFontSize(8);
					header.setFontColor(ColorConstants.GRAY);

					tempString = "TID: " + migrantDetail.getTrackingId();
					Paragraph tid = new Paragraph(tempString);
					tid.setFontSize(12);
					tid.setFontColor(ColorConstants.BLACK);
					tid.setFont(fontBold);

					float[] pointColumnWidths = { (float) 25, (float) 25, (float) 25, (float) 25 };
					Table table = new Table(UnitValue.createPercentArray(pointColumnWidths));
					table.setWidth(UnitValue.createPercentValue(100));
					table.setFixedLayout();

					Style headerText = new Style();
					headerText.setFontSize(12);
					headerText.setFontColor(ColorConstants.BLACK);
					headerText.setFontFamily(StandardFonts.HELVETICA_BOLD);

					Style theader = new Style();
					theader.setFontSize(10);
					theader.setFontColor(ColorConstants.WHITE);
					theader.setBackgroundColor(ColorConstants.DARK_GRAY);
					theader.setFontFamily(StandardFonts.HELVETICA_BOLD);

					Style labelStyle = new Style();
					labelStyle.setFontSize(10);
					labelStyle.setFontFamily(StandardFonts.HELVETICA);
					labelStyle.setBackgroundColor(ColorConstants.LIGHT_GRAY);
					labelStyle.setFontColor(ColorConstants.BLACK);
					labelStyle.setBorder(new SolidBorder(ColorConstants.GRAY, 1));

					Style contentStyle = new Style();
					contentStyle.setFontSize(10);
					contentStyle.setFontFamily(StandardFonts.HELVETICA);
					contentStyle.setFontColor(ColorConstants.BLACK);
					contentStyle.setBorder(new SolidBorder(ColorConstants.GRAY, 1));

					Cell cell;

					cell = new Cell(0, 4);
					tempString = "";
					cell.add(new Paragraph(tempString));
					cell.setFont(fontBold);
					cell.addStyle(headerText);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorder(Border.NO_BORDER);
					cell.setPadding(5f);
					table.addHeaderCell(cell);

					cell = new Cell(0, 4);
					tempString = "FARMER DETAILS";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(theader);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderBottom(Border.NO_BORDER);
					cell.setPadding(4f);
					table.addHeaderCell(cell);

					cell = new Cell(0, 1);
					tempString = "Name";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table.addHeaderCell(cell);

					cell = new Cell(0, 3);
					tempString = migrantDetail.getName();
					cell.add(new Paragraph(tempString));
					cell.setFont(fontBold);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table.addHeaderCell(cell);

					cell = new Cell(0, 1);
					tempString = "Mobile";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table.addHeaderCell(cell);

					cell = new Cell(0, 3);
					if (migrantDetail.getMobile() != null) {
						tempString = migrantDetail.getMobile();
					} else {
						tempString = "--";
					}
					cell.add(new Paragraph(tempString));
					cell.setFont(fontBold);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table.addHeaderCell(cell);
					document.add(table);

					Table table5 = new Table(UnitValue.createPercentArray(pointColumnWidths));
					table5.setWidth(UnitValue.createPercentValue(100));
					table5.setFixedLayout();

					cell = new Cell(0, 4);
					tempString = "";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(headerText);
					cell.setTextAlignment(TextAlignment.CENTER);
					cell.setBorder(Border.NO_BORDER);
					cell.setPadding(5f);
					table5.addHeaderCell(cell);

					cell = new Cell(0, 4);
					tempString = "ADDRESS DETAILS";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(theader);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderBottom(Border.NO_BORDER);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);

					cell = new Cell(0, 1);
					tempString = "Native District";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);

					cell = new Cell(0, 1);
					if (migrantDetail.getNativeDistrict() != null) {
						tempString = migrantDetail.getNativeDistrict().getDistrictName();
					} else {
						tempString = "--";
					}
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);

					cell = new Cell(0, 1);
					tempString = "Epi Center";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);

					cell = new Cell(0, 1);
					if (migrantDetail.getEpiCenter() != null) {
						tempString = migrantDetail.getEpiCenter().getName();
					} else {
						tempString = "--";
					}
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);

					cell = new Cell(0, 1);
					tempString = "Circle";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);

					cell = new Cell(0, 1);
					if (migrantDetail.getNativeCircle() != null) {
						tempString = migrantDetail.getNativeCircle();
					} else {
						tempString = "--";
					}
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);

					cell = new Cell(0, 1);
					tempString = "Block";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);

					cell = new Cell(0, 1);
					if (migrantDetail.getNativeBlock() != null) {
						tempString = migrantDetail.getNativeBlock();
					} else {
						tempString = "--";
					}
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);

					cell = new Cell(0, 2);
					tempString = "Do You Belong to Urban or Rural Area";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);

					cell = new Cell(0, 2);
					if (migrantDetail.getNativeArea().equals("U")) {
						tempString = "Urban";
					} else if (migrantDetail.getNativeArea().equals("R")) {
						tempString = "Rural";
					} else {
						tempString = "--";
					}
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);

					if (migrantDetail.getNativeArea().equals("U")) {
						cell = new Cell(0, 1);
						tempString = "City/ Town/ Local Body";
						cell.add(new Paragraph(tempString));
						cell.setFont(font);
						cell.addStyle(labelStyle);
						cell.setTextAlignment(TextAlignment.LEFT);
						cell.setPadding(4f);
						table5.addHeaderCell(cell);

						cell = new Cell(0, 1);
						if (migrantDetail.getNativeCity() != null) {
							tempString = migrantDetail.getNativeCity();
						} else {
							tempString = "--";
						}
						cell.add(new Paragraph(tempString));
						cell.setFont(font);
						cell.addStyle(contentStyle);
						cell.setTextAlignment(TextAlignment.LEFT);
						cell.setPadding(4f);
						table5.addHeaderCell(cell);

						cell = new Cell(0, 1);
						tempString = "Ward";
						cell.add(new Paragraph(tempString));
						cell.setFont(font);
						cell.addStyle(labelStyle);
						cell.setTextAlignment(TextAlignment.LEFT);
						cell.setPadding(4f);
						table5.addHeaderCell(cell);

						cell = new Cell(0, 1);
						if (migrantDetail.getNativeWard() != null) {
							tempString = migrantDetail.getNativeWard();
						} else {
							tempString = "--";
						}
						cell.add(new Paragraph(tempString));
						cell.setFont(font);
						cell.addStyle(contentStyle);
						cell.setTextAlignment(TextAlignment.LEFT);
						cell.setPadding(4f);
						table5.addHeaderCell(cell);
					}

					if (migrantDetail.getNativeArea().equals("R")) {
						cell = new Cell(0, 1);
						tempString = "Village";
						cell.add(new Paragraph(tempString));
						cell.setFont(font);
						cell.addStyle(labelStyle);
						cell.setTextAlignment(TextAlignment.LEFT);
						cell.setPadding(4f);
						table5.addHeaderCell(cell);

						cell = new Cell(0, 1);
						if (migrantDetail.getNativeVillage() != null) {
							tempString = migrantDetail.getNativeVillage();
						} else {
							tempString = "--";
						}
						cell.add(new Paragraph(tempString));
						cell.setFont(font);
						cell.addStyle(contentStyle);
						cell.setTextAlignment(TextAlignment.LEFT);
						cell.setPadding(4f);
						table5.addHeaderCell(cell);

						cell = new Cell(0, 1);
						tempString = "Gram Panchayat";
						cell.add(new Paragraph(tempString));
						cell.setFont(font);
						cell.addStyle(labelStyle);
						cell.setTextAlignment(TextAlignment.LEFT);
						cell.setPadding(4f);
						table5.addHeaderCell(cell);

						cell = new Cell(0, 1);
						if (migrantDetail.getNativePanchayat() != null) {
							tempString = migrantDetail.getNativePanchayat();
						} else {
							tempString = "--";
						}
						cell.add(new Paragraph(tempString));
						cell.setFont(font);
						cell.addStyle(contentStyle);
						cell.setTextAlignment(TextAlignment.LEFT);
						cell.setPadding(4f);
						table5.addHeaderCell(cell);
					}

					cell = new Cell(0, 1);
					tempString = "Address";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);

					cell = new Cell(0, 1);
					if (migrantDetail.getNativeAddress() != null) {
						tempString = migrantDetail.getNativeAddress();
					} else {
						tempString = "--";
					}
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);
					
					cell = new Cell(0, 1);
					tempString = "Pin code";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);

					cell = new Cell(0, 1);
					if (migrantDetail.getNativePincode() != null) {
						tempString = migrantDetail.getNativePincode().toString();
					} else {
						tempString = "--";
					}
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);
					
					cell = new Cell(0, 1);
					tempString = "Farmer Category";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);

					cell = new Cell(0, 1);
					if (migrantDetail.getFarmerCategory() != null) {
						if (migrantDetail.getFarmerCategory() .equals("G")) {
							tempString = "General";
						} else if(migrantDetail.getFarmerCategory() .equals("SC")) {
							tempString = "SC";
						}
						else if(migrantDetail.getFarmerCategory() .equals("ST")) {
							tempString = "ST";
						}else {
							tempString="--";
						}
					} else {
						tempString = "--";
					}
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);
					
					cell = new Cell(0, 1);
					tempString = "Farm Category";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);

					cell = new Cell(0, 1);
					if (migrantDetail.getFarmCategory() != null) {
						if (migrantDetail.getFarmCategory() .equals("B")) {
							tempString = "Backyard";
						} else if(migrantDetail.getFarmCategory() .equals("F")) {
							tempString = "Farm";
						}
						else {
							tempString="--";
						}
					} else {
						tempString = "--";
					}
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table5.addHeaderCell(cell);
					

					document.add(table5);

					Table table1 = new Table(UnitValue.createPercentArray(pointColumnWidths));
					table1.setWidth(UnitValue.createPercentValue(100));
					table1.setFixedLayout();

					cell = new Cell(0, 4);
					tempString = "";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(headerText);
					cell.setTextAlignment(TextAlignment.CENTER);
					cell.setBorder(Border.NO_BORDER);
					cell.setPadding(5f);
					table1.addHeaderCell(cell);

					cell = new Cell(0, 4);
					tempString = "BANK ACCOUNT DETAILS";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(theader);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderBottom(Border.NO_BORDER);
					cell.setPadding(4f);
					table1.addHeaderCell(cell);

					cell = new Cell(0, 1);
					tempString = "Account Number";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table1.addHeaderCell(cell);

					cell = new Cell(0, 3);
					tempString = "--";
					if (migrantDetail.getAccountNumber() != null) {
						tempString = migrantDetail.getAccountNumber();
					} else {
						tempString = "--";
					}
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table1.addHeaderCell(cell);

					cell = new Cell(0, 1);
					tempString = "IFSC Code";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table1.addHeaderCell(cell);

					cell = new Cell(0, 3);
					tempString = "";
					if (migrantDetail.getIfscCode() != null) {
						tempString = migrantDetail.getIfscCode();
					} else {
						tempString = "--";
					}
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table1.addHeaderCell(cell);

					document.add(table1);

					Table table3 = new Table(UnitValue.createPercentArray(pointColumnWidths));
					table3.setWidth(UnitValue.createPercentValue(100));
					table3.setFixedLayout();

					cell = new Cell(0, 4);
					tempString = "";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(headerText);
					cell.setTextAlignment(TextAlignment.CENTER);
					cell.setBorder(Border.NO_BORDER);
					cell.setPadding(5f);
					table3.addHeaderCell(cell);

					cell = new Cell(0, 4);
					tempString = "ANIMAL DETAILS";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(theader);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderBottom(Border.NO_BORDER);
					cell.setPadding(4f);
					table3.addHeaderCell(cell);
					
					
					cell = new Cell(0, 1);
					tempString = "No. of Animals";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table3.addHeaderCell(cell);

					cell = new Cell(0, 1);
					int tempint = 0;
					if (migrantDetail.getNoOfAnimal() != null) {
						tempint = migrantDetail.getNoOfAnimal();
					} else {
						tempint = 0;
					}
					cell.add(new Paragraph(tempint+""));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table3.addHeaderCell(cell);
					
					cell = new Cell(0, 1);
					tempString = "Total Amount(Rs.)";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table3.addHeaderCell(cell);

					cell = new Cell(0, 1);
					Long templong = 0l;
					if (migrantDetail.getAmount() != null) {
						templong = migrantDetail.getAmount();
					} else {
						templong = 0l;
					}
					cell.add(new Paragraph(templong+""));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table3.addHeaderCell(cell);
					document.add(table3);
					
					float[] pointColumnWidths1 = { (float) 20, (float) 20, (float) 20, (float) 20, (float) 20,(float) 20 };
					Table table4 = new Table(UnitValue.createPercentArray(pointColumnWidths1));
					table4.setWidth(UnitValue.createPercentValue(100));
					table4.setFixedLayout();
					
					cell = new Cell(0, 1);
					tempString = "Registration No./UID";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.CENTER);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table4.addHeaderCell(cell);
					
					cell = new Cell(0, 1);
					tempString = "Breed";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.CENTER);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table4.addHeaderCell(cell);
					
					cell = new Cell(0, 1);
					tempString = "Weight/ Carcass Weight (Kg)";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.CENTER);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table4.addHeaderCell(cell);
					
					cell = new Cell(0, 1);
					tempString = "Heart Girth \n(in Meters)";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.CENTER);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table4.addHeaderCell(cell);
					
					cell = new Cell(0, 1);
					tempString = "Length \n(in Meters)";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.CENTER);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table4.addHeaderCell(cell);
					
					cell = new Cell(0, 1);
					tempString = "Amount \n(in Rs.)";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.CENTER);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table4.addHeaderCell(cell);
					
					DecimalFormat df=new DecimalFormat("#0.00##");
					ArrayList<AnimalDetails> animalDetailsList=animalDetailsRepository.getActiveAnimalDetailsByFarmer(migrantDetail.getId());
				    if(animalDetailsList!=null && animalDetailsList.size()>0) {
					    for (AnimalDetails animalDetails : animalDetailsList) {
					    	cell = new Cell(0, 1);
					    	tempString = "";
							if (animalDetails.getRegId() != null) {
								tempString = animalDetails.getRegId();
							} else {
								tempString = "--";
							}
							cell.add(new Paragraph(tempString+""));
							cell.setFont(font);
							cell.addStyle(contentStyle);
							cell.setTextAlignment(TextAlignment.LEFT);
							cell.setBorderTop(Border.NO_BORDER);
							cell.setPadding(4f);
							table4.addHeaderCell(cell);
							
							cell = new Cell(0, 1);
					    	tempString = "";
							if (animalDetails.getBreed() != null) {
								if (animalDetails.getBreed() .equals("0")) {
									tempString = "--";
								}else if(animalDetails.getBreed() .equals("E")){
									tempString="Exotic";
								}
								else if(animalDetails.getBreed() .equals("C")){
									tempString="Cross";
								}else if(animalDetails.getBreed() .equals("L")){
									tempString="Local";
								}else {
									tempString="--";
								}
							} else {
								tempString = "--";
							}
							cell.add(new Paragraph(tempString+""));
							cell.setFont(font);
							cell.addStyle(contentStyle);
							cell.setTextAlignment(TextAlignment.LEFT);
							cell.setBorderTop(Border.NO_BORDER);
							cell.setPadding(4f);
							table4.addHeaderCell(cell);
							
							cell = new Cell(0, 1);
					    	tempString = "";
					    	if (animalDetails.getWeighStatus().equals("Y")){
								if (animalDetails.getLiveWeight() != null && animalDetails.getLiveWeight()!=0) {
									tempString = df.format(animalDetails.getLiveWeight());
								}else {
									tempString = "--";
								}
					    	}
					    	if (animalDetails.getWeighStatus().equals("N")){
								if(animalDetails.getCarcasWeight() != null && animalDetails.getCarcasWeight()!=0) {
									tempString = df.format(animalDetails.getCarcasWeight());
								}else {
									tempString = "--";
								}
					    	}
							cell.add(new Paragraph(tempString));
							cell.setFont(font);
							cell.addStyle(contentStyle);
							cell.setTextAlignment(TextAlignment.LEFT);
							cell.setBorderTop(Border.NO_BORDER);
							cell.setPadding(4f);
							table4.addHeaderCell(cell);
							
							cell = new Cell(0, 1);
					    	tempString = "";
							if (animalDetails.getHeartGirth() != null && animalDetails.getHeartGirth()!=0) {
								tempString = df.format(animalDetails.getHeartGirth());
							} else {
								tempString = "--";
							}
							cell.add(new Paragraph(tempString));
							cell.setFont(font);
							cell.addStyle(contentStyle);
							cell.setTextAlignment(TextAlignment.LEFT);
							cell.setBorderTop(Border.NO_BORDER);
							cell.setPadding(4f);
							table4.addHeaderCell(cell);
							
							cell = new Cell(0, 1);
					    	tempString = "";
							if (animalDetails.getLength() != null && animalDetails.getLength()!=0) {
								tempString = df.format(animalDetails.getLength());
							} else {
								tempString = "--";
							}
							cell.add(new Paragraph(tempString));
							cell.setFont(font);
							cell.addStyle(contentStyle);
							cell.setTextAlignment(TextAlignment.LEFT);
							cell.setBorderTop(Border.NO_BORDER);
							cell.setPadding(4f);
							table4.addHeaderCell(cell);
							
							cell = new Cell(0, 1);
					    	tempString = "";
							if (animalDetails.getAmount() != null && animalDetails.getAmount()!=0) {
								tempString = df.format(animalDetails.getAmount());
							} else {
								tempString = "--";
							}
							cell.add(new Paragraph(tempString));
							cell.setFont(font);
							cell.addStyle(contentStyle);
							cell.setTextAlignment(TextAlignment.LEFT);
							cell.setBorderTop(Border.NO_BORDER);
							cell.setPadding(4f);
							table4.addHeaderCell(cell);
					    }
				    }

					document.add(table4);
					
					
					
					Table table6 = new Table(UnitValue.createPercentArray(pointColumnWidths));
					table6.setWidth(UnitValue.createPercentValue(100));
					table6.setFixedLayout();

					cell = new Cell(0, 4);
					tempString = "";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(headerText);
					cell.setTextAlignment(TextAlignment.CENTER);
					cell.setBorder(Border.NO_BORDER);
					cell.setPadding(5f);
					table6.addHeaderCell(cell);

					cell = new Cell(0, 4);
					tempString = "STATUS DETAILS";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(theader);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderBottom(Border.NO_BORDER);
					cell.setPadding(4f);
					table6.addHeaderCell(cell);

					cell = new Cell(0, 1);
					tempString = "Submitted By";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table6.addHeaderCell(cell);

					cell = new Cell(0, 3);
					tempString = "--";
					if (migrantDetail.getSubmitBy() != null) {
						Optional<DistrictUsers> districtUser = districtUsersRepository.findById(migrantDetail.getSubmitBy());
						DistrictUsers user = districtUser.get();
						tempString = user.getUserName()+"  on "+new SimpleDateFormat("dd-MM-YYYY hh:mm:ss a").format(migrantDetail.getSubmitOn());
					} else {
						tempString = "--";
					}
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setBorderTop(Border.NO_BORDER);
					cell.setPadding(4f);
					table6.addHeaderCell(cell);

					cell = new Cell(0, 1);
					tempString = "Verified By";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table6.addHeaderCell(cell);

					cell = new Cell(0, 3);
					if (migrantDetail.getVerifiedBy() != null) {
						Optional<DistrictUsers> districtUser = districtUsersRepository.findById(migrantDetail.getVerifiedBy());
						DistrictUsers user = districtUser.get();
						tempString = user.getUserName()+"  on "+new SimpleDateFormat("dd-MM-YYYY hh:mm:ss a").format(migrantDetail.getVerifiedOn())
								+"\n Remarks: "+migrantDetail.getVerificationRemark();
					} else {
						tempString = "--";
					}
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table6.addHeaderCell(cell);
					
					cell = new Cell(0, 1);
					tempString = "Approved By";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table6.addHeaderCell(cell);

					cell = new Cell(0, 3);
					if (migrantDetail.getAcceptedBy() != null) {
						Optional<DistrictUsers> districtUser = districtUsersRepository.findById(migrantDetail.getAcceptedBy());
						DistrictUsers user = districtUser.get();
						tempString = user.getUserName()+"  on "+new SimpleDateFormat("dd-MM-YYYY hh:mm:ss a").format(migrantDetail.getAcceptedOn())
								+"\n Remarks: "+migrantDetail.getAcceptRemark();
					} else {
						tempString = "--";
					}
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table6.addHeaderCell(cell);
					
					if (migrantDetail.getPickupScheduledBy() != null) {
					cell = new Cell(0, 1);
					tempString = "Scheduled By";
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(labelStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table6.addHeaderCell(cell);

					cell = new Cell(0, 3);
					if (migrantDetail.getPickupScheduledBy() != null) {
						Optional<DistrictUsers> districtUser = districtUsersRepository.findById(migrantDetail.getPickupScheduledBy());
						DistrictUsers user = districtUser.get();
						tempString = user.getUserName()+"  Scheduled on "+new SimpleDateFormat("dd-MM-YYYY hh:mm:ss a").format(migrantDetail.getPickupScheduledOn())
								+"\n Culling Center: "+migrantDetail.getCullingCenter().getName()
								+"\n Remarks: "+migrantDetail.getScheduleRemark();
					} else {
						tempString = "--";
					}
					cell.add(new Paragraph(tempString));
					cell.setFont(font);
					cell.addStyle(contentStyle);
					cell.setTextAlignment(TextAlignment.LEFT);
					cell.setPadding(4f);
					table6.addHeaderCell(cell);
					}
					if (migrantDetail.getCollectedBy() != null) {
						cell = new Cell(0, 1);
						tempString = "Collected By";
						cell.add(new Paragraph(tempString));
						cell.setFont(font);
						cell.addStyle(labelStyle);
						cell.setTextAlignment(TextAlignment.LEFT);
						cell.setPadding(4f);
						table6.addHeaderCell(cell);

						cell = new Cell(0, 3);
						if (migrantDetail.getCollectedBy() != null) {
							Optional<CullingCenterUsers> cullingCenterUsers = cullingCenterUsersRepository.findById(migrantDetail.getCollectedBy());
							CullingCenterUsers user = cullingCenterUsers.get();
							tempString = user.getName()+"  Collected on "+new SimpleDateFormat("dd-MM-YYYY hh:mm:ss a").format(migrantDetail.getCollectedOn())
									+"\n Culling Center: "+migrantDetail.getCullingCenter().getName()
									+"\n Remarks: "+migrantDetail.getCollectedRemark();
						} else {
							tempString = "--";
						}
						cell.add(new Paragraph(tempString));
						cell.setFont(font);
						cell.addStyle(contentStyle);
						cell.setTextAlignment(TextAlignment.LEFT);
						cell.setPadding(4f);
						table6.addHeaderCell(cell);
						}
					if (migrantDetail.getCulledBy() != null) {
						cell = new Cell(0, 1);
						tempString = "Culled By";
						cell.add(new Paragraph(tempString));
						cell.setFont(font);
						cell.addStyle(labelStyle);
						cell.setTextAlignment(TextAlignment.LEFT);
						cell.setPadding(4f);
						table6.addHeaderCell(cell);

						cell = new Cell(0, 3);
						if (migrantDetail.getCollectedBy() != null) {
							Optional<CullingCenterUsers> cullingCenterUsers = cullingCenterUsersRepository.findById(migrantDetail.getCollectedBy());
							CullingCenterUsers user = cullingCenterUsers.get();
							tempString = user.getName()+"  Collected on "+new SimpleDateFormat("dd-MM-YYYY hh:mm:ss a").format(migrantDetail.getCulledOn())
									+"\n Culling Center: "+migrantDetail.getCullingCenter().getName()
									+"\n Remarks: "+migrantDetail.getCulledRemark();
						} else {
							tempString = "--";
						}
						cell.add(new Paragraph(tempString));
						cell.setFont(font);
						cell.addStyle(contentStyle);
						cell.setTextAlignment(TextAlignment.LEFT);
						cell.setPadding(4f);
						table6.addHeaderCell(cell);
						}
					document.add(table6);

					for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
						Rectangle pageSize = pdfDocument.getPage(i).getPageSize();
						float x = (float) (pageSize.getWidth() / 1.07);
						float y = pageSize.getTop() - 25;
						document.showTextAligned(header, x, y, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);

						float xTid = (pageSize.getWidth() / 5);
						float yTid = pageSize.getTop() - 35;
						document.showTextAligned(tid, xTid, yTid, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
					}

					document.close();

					File file = new File(filePath.toString());
					MediaType mediaType = MediaType.parseMediaType("application/pdf");
					InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

					return ResponseEntity.ok()
							.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
							.contentType(mediaType).contentLength(file.length()).body(resource);
				} catch (Exception e) {
					e.printStackTrace();
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
				} finally {
					CustomUtils.removeFile(filePath.toString());
				}
			}
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	}
	

	
							
}
