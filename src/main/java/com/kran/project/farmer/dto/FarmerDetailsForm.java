package com.kran.project.farmer.dto;

import java.util.Date;
import java.util.List;

import com.kran.project.farmer.entities.AnimalDetails;
import com.kran.project.farmer.entities.FarmerDetails;

import lombok.Data;

@Data
public class FarmerDetailsForm {
	private FarmerDetails farmer;
	private List<AnimalDetails> animalDetails;
	
	public FarmerDetailsForm() {
		super();
	}

	public FarmerDetailsForm(FarmerDetails farmer) {
		super();
		this.farmer = farmer;
	}

	
}