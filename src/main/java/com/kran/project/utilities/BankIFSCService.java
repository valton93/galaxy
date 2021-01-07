package com.kran.project.utilities;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.kran.project.farmer.dto.BankDetails;
import com.kran.project.farmer.dto.BankDetailsRazor;

public class BankIFSCService {
	public static boolean verifyIFSCUsingDatayuge(String bankIFSC) {
		String url = "https://ifsc.datayuge.com/api/v1/{bankIFSC}";
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("DY-X-Authorization", "02449b2b8ad80aa516dc8ff4d5cfe6bf2c4a2e03");
		HttpEntity<BankDetails> request = new HttpEntity<>(headers);
		
		ResponseEntity<BankDetails> response
			= restTemplate.exchange(url, HttpMethod.GET, request, BankDetails.class, bankIFSC);
		
		BankDetails bankDetails = response.getBody();
		if (bankDetails != null && bankDetails.getIfsc() != null) {
			return true;
		}
		
		return false;
	}
	
	public static boolean verifyIFSCUsingRazorPay(String bankIFSC) {
		String url = "https://ifsc.razorpay.com/{bankIFSC}";
		RestTemplate restTemplate = new RestTemplate();
		
		try {
			ResponseEntity<BankDetails> response
				= restTemplate.getForEntity(url, BankDetails.class, bankIFSC);

			if (response.getStatusCode().is2xxSuccessful()) {
				return true;
			}
		} catch (RestClientException e) {
			return false;
		}
		
		return false;
	}
	
	public static BankDetails getIFSCUsingDatayuge(String bankIFSC) {
		String url = "https://ifsc.datayuge.com/api/v1/{bankIFSC}";
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("DY-X-Authorization", "02449b2b8ad80aa516dc8ff4d5cfe6bf2c4a2e03");
		HttpEntity<BankDetails> request = new HttpEntity<>(headers);
		
		ResponseEntity<BankDetails> response
			= restTemplate.exchange(url, HttpMethod.GET, request, BankDetails.class, bankIFSC);
		
		BankDetails bankDetails = response.getBody();
		if (bankDetails != null && bankDetails.getIfsc() != null) {
			bankDetails.setStatus("S");
		}else {
			bankDetails=new BankDetails();
			bankDetails.setStatus("F");
		}
		return bankDetails;
	}
	
	public static BankDetails getIFSCUsingRazorPay(String bankIFSC) {
		String url = "https://ifsc.razorpay.com/{bankIFSC}";
		RestTemplate restTemplate = new RestTemplate();
		BankDetails bankDetails =  new BankDetails();
		
		try {
			ResponseEntity<String> response
				= restTemplate.getForEntity(url, String.class, bankIFSC);
			String bankDetailsRazorStr = response.getBody();
			Gson gson = new Gson(); 
			  
	        // Converting json to object 
	        // first parameter should be prpreocessed json 
	        // and second should be mapping class 
			BankDetailsRazor bankDetailsRazor 
	            = gson.fromJson(bankDetailsRazorStr, 
	            		BankDetailsRazor.class); 
			if (response.getStatusCode().is2xxSuccessful()) {
				bankDetails.setBank(bankDetailsRazor.getBANK());
				bankDetails.setBranch(bankDetailsRazor.getBRANCH());
				bankDetails.setState(bankDetailsRazor.getSTATE());
				bankDetails.setDistrict(bankDetailsRazor.getDISTRICT());
				bankDetails.setCity(bankDetailsRazor.getCITY());
				bankDetails.setAddress(bankDetailsRazor.getADDRESS());
				bankDetails.setStatus("S");
			}
		} catch (RestClientException e) {
			bankDetails=new BankDetails();
			bankDetails.setStatus("F");
		}
		return bankDetails;
	}
	
}
