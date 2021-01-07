package com.kran.project.utilities;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@PropertySource("classpath:templates-alerts.properties")
public class MessageService {
	private static boolean production;
	private static String localhostPath;
	
	private static boolean isProduction() {
		return production;
	}
	
	private static String getLocalhostPath() {
		return localhostPath;
	}
	
	@Autowired
	public MessageService(@Value("${environment.production}") boolean production,
					   	  @Value("${host.domain}") String localhostPath) {
		MessageService.production = production;
		MessageService.localhostPath = localhostPath;
	}

	public static boolean sendMessage(String message, String[] recipients, String serviceProvider)
			throws UnknownHostException, UnsupportedEncodingException {
		boolean messageStatus = false;
		if (isProduction()) {
			RestTemplate restTemplate = new RestTemplate();
			
			if (serviceProvider == null) {
				serviceProvider = Provider.SPRING_EDGE;
			}

			MultiValueMap<String, String> uriParams;
			uriParams = new LinkedMultiValueMap<>();
			uriParams.add("message", URLEncoder.encode(message, "UTF-8"));
			uriParams.add("recipients", String.join(",", recipients));
			uriParams.add("provider", serviceProvider);

			UriComponents uriComponents
				= UriComponentsBuilder.newInstance()
									  .scheme("http")
									  .host(getLocalhostPath())
									  .path("/message/outbox/sendMessage")
									  .queryParams(uriParams)
									  .build();

			ResponseEntity<?> messageResponse = restTemplate.getForEntity(uriComponents.toUriString(), Object.class);

			if (messageResponse.getStatusCode().equals(HttpStatus.OK)) {
				messageStatus = true;
			}
		}

		return messageStatus;
	}

	public final class Provider {
		private Provider() {
			throw new IllegalStateException("Utility Class");
		}

		public static final String SPRING_EDGE = "springEdge";
		public static final String SMS_ACHARIYA = "smsAchariya";
		public static final String SMS_TEXTLOCAL = "textLocal";
		public static final String SMS_POSTIEFS = "postiefs";
		public static final String SMS_SERVETEL = "servetel";
	}
}