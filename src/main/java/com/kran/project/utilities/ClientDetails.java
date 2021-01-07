package com.kran.project.utilities;

import javax.servlet.http.HttpServletRequest;

import lombok.Getter;

@Getter
public class ClientDetails {
	private String userAgent;
	
	private String clientBrowser = "Unknown";
	private String clientOS = "Unknown";
	private String clientIP = "Unknown";

	public ClientDetails(HttpServletRequest request) {
		if (request != null) {
			userAgent = request.getHeader("User-Agent");
			
			// ***DETERMINE BROWSER NAME
			if (userAgent.indexOf("MSIE 5") != -1)
				clientBrowser = "Internet Explorer 5";
			else if (userAgent.indexOf("MSIE 6") != -1)
				clientBrowser = "Internet Explorer 6";
			else if (userAgent.indexOf("AOL") != -1)
				clientBrowser = "AOL";
			else if (userAgent.indexOf("Opera") != -1 || userAgent.indexOf("OPR") != -1)
				clientBrowser = "Opera";
			else if (userAgent.indexOf("Safari") != -1 && userAgent.indexOf("Chrome") == -1
					&& userAgent.indexOf("Chromium") == -1)
				clientBrowser = "Safari";
			else if (userAgent.indexOf("Gecko") != -1) {
				if (userAgent.indexOf("Firefox") != -1)
					clientBrowser = "Firefox";
				else if (userAgent.indexOf("Chrome") != -1 && userAgent.indexOf("Chromium") == -1)
					clientBrowser = "Chrome";
				else
					clientBrowser = "Mozilla";
			}
			
			// ***DETERMINE OPERATING SYSTEM NAME
			if (userAgent.indexOf("Windows NT 5.0") != -1)
				clientOS = "Windows 2000";
			else if (userAgent.indexOf("Windows NT 5.1") != -1)
				clientOS = "Windows XP";
			else if (userAgent.toLowerCase().indexOf("windows") != -1)
				clientOS = "Windows";
			else if (userAgent.indexOf("Linux") != -1)
				clientOS = "Linux";
			else if (userAgent.toLowerCase().indexOf("x11") != -1)
				clientOS = "Unix";
			else if (userAgent.toLowerCase().indexOf("android") != -1)
				clientOS = "Android";
			else if (userAgent.toLowerCase().indexOf("cros") != -1)
				clientOS = "Chrome OS";
			else if (userAgent.toLowerCase().indexOf("mac") != -1)
				clientOS = "Mac OS";
			else if (userAgent.toLowerCase().indexOf("iphone") != -1)
				clientOS = "iOS";
			
			// ***DETERMINE IP
			clientIP = request.getHeader("X-Forwarded-For");
			if (clientIP == null 
				|| clientIP.isBlank()
				|| clientIP.equals("127.0.0.1")) {
				clientIP = request.getHeader("X-Real-IP");
			} else {
				clientIP = request.getRemoteAddr();
			}
		}
	}

}