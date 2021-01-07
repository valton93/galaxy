package com.kran.project.user.dto;

import lombok.Data;

@Data
public class Pagination {
	private String pageStatus = "F";
	private String pageSearch = "";
	private String pageSort = "D";
	private long id = 0;
	
	private int pageNumber = 1;
	private int pageFirst = 0;
	private int pageSize = 10;
	private long pageTotal = 0;
	private int pageCount = 0;
	private int currentPageSize = 0;
	
	public static Pagination updatePaginationAttributes(Pagination pagination) {
		PageStatusConstants pageStatusConstants 
			= PageStatusConstants.valueOf(pagination.getPageStatus());
		
		if (pagination.getPageTotal() < pagination.getPageSize()) {
			pagination.setPageCount(1);
		} else {
			pagination.setPageCount((int) pagination.getPageTotal() / pagination.getPageSize());
			if ((pagination.getPageTotal() % pagination.getPageSize()) > 0) {
				pagination.setPageCount(pagination.getPageCount() + 1);
			}
		}

		switch (pageStatusConstants) {
			case P:
				pagination.setPageNumber(pagination.getPageNumber() - 1);
				pagination.setPageFirst((pagination.getPageNumber() - 1) * pagination.getPageSize());
				pagination.setPageStatus("P");
				break;
	
			case N:
				pagination.setPageNumber(pagination.getPageNumber() + 1);
				pagination.setPageFirst((pagination.getPageNumber() - 1) * pagination.getPageSize());
				pagination.setPageStatus("N");
				break;
	
			case L:
				pagination.setPageNumber(pagination.getPageCount());
				pagination.setPageFirst((pagination.getPageNumber() - 1) * pagination.getPageSize());
				pagination.setPageStatus("L");
				break;
	
			case PP:
				pagination.setPageNumber(pagination.getPageNumber());
				pagination.setPageFirst((pagination.getPageNumber() - 1) * pagination.getPageSize());
				pagination.setPageStatus("PP");
				break;
	
			case fsearch:
				pagination.setPageNumber(1);
				pagination.setPageFirst(0);
				pagination.setPageStatus("F");
				break;
	
			case F:
				pagination.setPageNumber(1);
				pagination.setPageFirst(0);
				pagination.setPageStatus("F");
				break;
	
			case fsave:
				pagination.setPageFirst((pagination.getPageNumber() - 1) * pagination.getPageSize());
				pagination.setPageStatus("F");
				break;
	
			default:
				throw new IllegalArgumentException("Choice Not Found");
		}

		return pagination;
	}

	public static Pagination updatePaginationAttributesForSave(Pagination pagination) {
		if (pagination.getPageStatus().equals("P"))
			pagination.setPageNumber(pagination.getPageNumber() + 1);
		if (pagination.getPageStatus().equals("N"))
			pagination.setPageNumber(pagination.getPageNumber() - 1);
		return pagination;
	}

	public enum PageStatusConstants {
		P, N, L, F, PP, fsearch, fsave
	}

}