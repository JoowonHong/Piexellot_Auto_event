package com.example.pixellot.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class PixellotController {
	
	static final String DEF_URL = "https://api.pixellot.tv/v1/";
	static final String DEF_STAGE_URL = "https://api.stage.pixellot.tv/v1/";
	static final String LOGIN_INFO = "{\"username\":\"yst_api\", \"password\":\"yst7tRTe7q7R3qFTfGvUg8TRayUuR9e\"}";
	static final String LOGIN_STAGE_INFO = "{\"username\":\"yst_stage_api\", \"password\":\"yst4DByLQZELW7j7OWV0Mk79BIOVBs\"}";

	protected boolean m_isReal = true;

	protected String m_strToken = "";



	protected String m_strStageToken = ""; 
	long m_lExpiry = 0;
	long m_lStageExpiry = 0;

	@GetMapping("/index")
	public String index(){
		return "index.html";
	}

	@ResponseBody 
	@PostMapping("/login")	
	public String getLogin() {		
		return _getLogin(m_isReal);
	}

	@PatchMapping("/overlayurl")	
	public String overlayURL(@RequestBody MyRequestData requestData) {
		return _overlayURL(m_isReal, requestData.getEventID(), requestData.getOverlayUrl());
	}

	@ResponseBody 
	@GetMapping("/clubs")	
	public String getClubs() {
		return _getClubs();
	}

	@ResponseBody 
	@GetMapping("/event")	
	public String  getEvent() {		
		return _getEvent();
	}
	 
	@ResponseBody 
	@PostMapping("/vas_stagejoin")	
	public String getStageVasJoin(@RequestBody Map<String, Object> data) {
		return _getStageVasJoin(MapToJson(data));
	}
	@ResponseBody 
	@PostMapping("/vas_stageleave")	
	public String getStageVasLeave(@RequestBody Map<String, Object> data) {
		return _getStageVasLeave(MapToJson(data));
	}

    @ResponseBody 
	@PostMapping("/vas_stagewebhook")	
	public String getStageVasWebhook(@RequestBody Map<String, Object> data) {
		return _getStageVasWebhook(MapToJson(data));
	}

	@ResponseBody 
	@PostMapping("/vas_join")	
	public String getVasJoin(@RequestBody Map<String, Object> data) {
		return _getVasJoin(MapToJson(data));
	}
	@ResponseBody 
	@PostMapping("/vas_leave")	
	public String getVasLeave(@RequestBody Map<String, Object> data) {
		return _getVasLeave(MapToJson(data));
	}

    @ResponseBody 
	@PostMapping("/vas_webhook")	
	public String getVasWebhook(@RequestBody Map<String, Object> data) {
		return _getVasWebhook(MapToJson(data));
	}    
    
    @ResponseBody 
	@PostMapping("/webhook")	
	public String getWebhook(@RequestBody Map<String, Object> data) {
		return _getWebhook(MapToJson(data));
	}
	
    @ResponseBody 
	@PostMapping("/stagewebhook")	
	public String getStageWebhook(@RequestBody Map<String, Object> data) {
		return _getStageWebhook(MapToJson(data));
	}

    @ResponseBody
    @GetMapping("/hogak")
    public void hogak_url(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userAgent = request.getHeader("User-Agent").toLowerCase();

        if (userAgent.contains("android")) {
            response.sendRedirect("https://play.google.com/store/apps/details?id=kr.co.ystcorp.sports.app");
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
            response.sendRedirect("https://apps.apple.com/kr/app/%ED%98%B8%EA%B0%81-%ED%94%8C%EB%9F%AC%EC%8A%A4/id6477554361");
        } else {
        }
    }

    
	public String createEvent(String strClubID, String strName, String strStart, String strEnd, String strVenueID, String strNumber) {
		return _createEvent(strClubID, strName, strStart, strEnd, strVenueID, strNumber);
	}

	static  class MyRequestData {
		public  String eventID;
		public  String overlayUrl;
        public String getEventID() {	return eventID;	}
        public String getOverlayUrl() {	return overlayUrl;	}
	}

	public String getToken(boolean isReal)
	{
        long now = new Date().getTime();

		if(isReal)	// 상용서버
		{
			if(m_lExpiry < now + 10000)
				_getLogin(true);
			return m_strToken;
		}

		// Stage
		if(m_lStageExpiry < now + 10000)
			_getLogin(false);
		return m_strStageToken;
	}

	public String getDefURL(boolean isReal)
	{
		if(isReal)
			return DEF_URL;
		else 
			return DEF_STAGE_URL;
	}

	// 파일에 데이터 기록 함수
    public static void writeToFile(String fileName, String data) {
        try {
            Files.write(Paths.get(fileName), 
                        Collections.singletonList(data), // 데이터를 리스트로 변환
                        StandardOpenOption.CREATE,       // 파일이 없으면 생성
                        StandardOpenOption.APPEND);      // 파일 끝에 데이터 추가
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일에 데이터 기록 함수
    public String MapToJson(Map<String, Object> data) {
        String jsonStr = "";
        try {
            // Map 데이터를 JSON 문자열로 변환
        	ObjectMapper objectMapper = new ObjectMapper();
        	jsonStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);

        } catch (Exception e) {
//        	logger.error("Error while processing webhook data", e);
        }
        return jsonStr;
    }
	
	String _getLogin(boolean isReal)
	{
		String retStr = "_getLogin";
		String apiUrl = getDefURL(isReal)+"login";
/*		String apiUrl = "https://api.stage.pixellot.tv/v1/"+"login";
		if(isReal)
			apiUrl = "https://api.pixellot.tv/v1/"+"login";
*/        
        // Request Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Request Body
        String requestBody = LOGIN_STAGE_INFO;
		if(isReal)
			requestBody = LOGIN_INFO;
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);

        // Handle the response
        System.out.println("Response: " + response.getBody());

        retStr = response.getStatusCode().toString();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            if(isReal)
            {
            	m_strToken = jsonNode.get("token").asText();
            	m_lExpiry = jsonNode.get("expiry").asLong();
            }
            else
            {
            	m_strStageToken = jsonNode.get("token").asText();
            	m_lStageExpiry = jsonNode.get("expiry").asLong();
            }

        } catch (JsonMappingException e) {
            // JsonMappingException을 처리하는 코드 작성
            e.printStackTrace(); // 예외 내용을 출력하거나 로깅할 수도 있음
        } catch (Exception e) {
            // 다른 예외를 처리하는 코드 작성
            e.printStackTrace(); // 예외 내용을 출력하거나 로깅할 수도 있음
        }

        return retStr;
	}

	
	String _overlayURL(boolean isReal, String eventID, String overlayURL) {
	    String retStr = "_overlayURL";
	    String apiUrl = getDefURL(isReal)+"events/" + eventID + "/vas/kScore";

        // Request Headers
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.set("Authorization", getToken(isReal));

	    // Request Body
	    String requestBody = "{\"overlayUrl\":\"" + overlayURL + "\"}";
	    HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

	    // RestTemplate
	    try {
	    	RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
	        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.PATCH, requestEntity, String.class);

	        // Check response status
	        if (response.getStatusCode().is2xxSuccessful()) {
	            ObjectMapper objectMapper = new ObjectMapper();
	            Object json = objectMapper.readValue(response.getBody(), Object.class);
	            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
	            System.out.println("Response: ");
	            System.out.println(prettyJson);
	            retStr = response.getStatusCode().toString();

	        } else {
	            System.err.println("Error Status Code: " + response.getStatusCode().value());
	            System.err.println("Error Response Body: " + response.getBody());
	            retStr = "Error";
	        }
        } catch (Exception e) {
            e.printStackTrace();
            retStr = "Error: " + e.getMessage();
        }

	    return retStr;
    }
	String _getClubs()
	{
		String retStr = "_getClubs";
		String apiUrl = getDefURL(m_isReal)+"clubs?limit=100&skip=0";

        // Request Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getToken(m_isReal)); // 예시: OAuth2 토큰이나 API 키 등

        // Request Body
        String requestBody = "";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, String.class);

        // Handle the response
        System.out.println("Response: " + response.getBody());
        
        retStr = response.getStatusCode().toString();
        retStr += response.getBody();

        return retStr;
    }

	public String _getEvent()
	{
		String retStr = "_getEvent";
//        List<String> strings = new ArrayList<>();

		String apiUrl = "";//DEF_URL + "events?criteria=" + "&limit=100&skip=0&sort=";

		try {
			String criteria = URLEncoder.encode("{\"status\":\"active\"}", StandardCharsets.UTF_8.toString()); 
	        apiUrl = getDefURL(m_isReal) + "events?criteria=" + criteria + "&limit=100&skip=0&sort=";
//	        apiUrl = getDefURL(m_isReal) + "events/65f0f16947776abe436a99d9";
		} catch (Exception e) {
            e.printStackTrace();
        }
	
        // Request Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getToken(m_isReal)); // 예시: OAuth2 토큰이나 API 키 등

        // Request Body
        String requestBody = "";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // RestTemplate
	    try {
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, String.class);
	
	        // Check response status
	        if (response.getStatusCode().is2xxSuccessful()) {
		        // Handle the response
		        System.out.println("Response: " + response.getBody());
		        
//		        retStr = response.getStatusCode().toString();
		        retStr = response.getBody();
        
	        } else {
	            System.err.println("Error Status Code: " + response.getStatusCode().value());
	            System.err.println("Error Response Body: " + response.getBody());
	            retStr = "Error";
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        retStr = "Error: " + e.getMessage();
	    }

        return retStr;
    }

	public String _getWebhook(String srtEvent)
	{
		String retStr = srtEvent;
        String fileName = "event_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".log";
        writeToFile(fileName, retStr);
        
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(srtEvent);
            String strEvent = jsonNode.get("eventId").asText(); 
            String strWhat = jsonNode.get("what").asText(); 
            if(strWhat.equals("scheduled")) 
            {
            	JsonNode jsonEvent = jsonNode.get("event"); 
//            	JsonNode jsonLabels = jsonEvent.get("labels"); 
//            	String strType = jsonLabels.get("sportType").asText(); 
//                if(strType.equals("volleyball")) 
            	{
            		String venueId = jsonEvent.get("venueId").asText();
            		String strOverlay = "";
            		if(venueId.equals("65f2b30e6c4bba77b6ab5af4"))	// 
                    	strOverlay = _overlayURL(true, strEvent, "http://ystcorp07296.cafe24.com/web2.jsp?id=yst1");
                    else if(venueId.equals("668f7f147aaf73e20077e360"))	// 
                    	strOverlay = _overlayURL(true, strEvent, "http://ystcorp07296.cafe24.com/web2.jsp?id=yst2");
                    else if(venueId.equals("637cb9de6cca8121659ef2f1"))	// 
                    	strOverlay = _overlayURL(true, strEvent, "http://ystcorp07296.cafe24.com/web2.jsp?id=yst6");
					else if(venueId.equals("633e6daf3cb88edce2bc1ac5"))	//
						strOverlay = _overlayURL(true, strEvent, "http://ystcorp07296.cafe24.com/web2.jsp?id=yst6");

					if(!strOverlay.isEmpty())
                    	writeToFile(fileName, strOverlay);
            	}
            }
        } catch (JsonMappingException e) {
            // JsonMappingException을 처리하는 코드 작성
            e.printStackTrace(); // 예외 내용을 출력하거나 로깅할 수도 있음
        } catch (Exception e) {
            // 다른 예외를 처리하는 코드 작성
            e.printStackTrace(); // 예외 내용을 출력하거나 로깅할 수도 있음
        }


        return retStr;
	}
	
	public String _getStageWebhook(String srtEvent)
	{
		String retStr = srtEvent;
        String fileName = "event_stage_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".log";
        writeToFile(fileName, retStr);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(srtEvent);
            String strEvent = jsonNode.get("eventId").asText(); 
            String strWhat = jsonNode.get("what").asText(); 
            if(strWhat.equals("scheduled")) 
            {
            	JsonNode jsonEvent = jsonNode.get("event"); 
            	JsonNode jsonLabels = jsonEvent.get("labels"); 
            	String strType = jsonLabels.get("sportType").asText(); 
                if(strType.equals("volleyball")) 
            	{
            		String venueId = jsonEvent.get("venueId").asText(); 
                    if(venueId.equals("657a8e0395e8656763a68381"))	// yst6
                    {
                    	String strOverlay = _overlayURL(false, strEvent, "http://ystcorp07296.cafe24.com/web2.jsp?id=yst6");
                        writeToFile(fileName, strOverlay);
                    }
            	}
            }
        } catch (JsonMappingException e) {
            // JsonMappingException을 처리하는 코드 작성
            e.printStackTrace(); // 예외 내용을 출력하거나 로깅할 수도 있음
        } catch (Exception e) {
            // 다른 예외를 처리하는 코드 작성
            e.printStackTrace(); // 예외 내용을 출력하거나 로깅할 수도 있음
        }
        
        return retStr;
	}

	public String _getVasJoin(String srtData)
	{
		writeToFile("vasjoin.log", "start");
		writeToFile("vasjoin.log", srtData);
		
		String retStr = "_getVasJoin";
		String apiUrl = getDefURL(true)+"monitoring/subscriptions";

        // Request Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getToken(true)); // 예시: OAuth2 토큰이나 API 키 등

        // Request Body
        String requestBody = 
        "{" +
        "\"messageType\": \"VasEventTimestamp\"," +
        "\"tenant\": \"yst\"," +
        "\"url\": \"http://yunwonwoo.cafe24.com/pixellot/vas_webhook\"," +
        "\"secret\": \"yst\"," +
        "\"emails\": \"yunwonwoo@ystcorp.co.kr\"" +
        "}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

	    try {
	        // RestTemplate
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);
	
	        // Handle the response
	        System.out.println("Response: " + response.getBody());
	        
	        retStr = response.getStatusCode().toString();
	        retStr += response.getBody();
	    } catch (Exception e) {
	        e.printStackTrace();
	        retStr = "Error: " + e.getMessage();
	    }

		writeToFile("vasjoin.log", retStr);
		retStr = srtData;
        return retStr;

//        writeToFile("vasjoin", srtData);
//        return "OK";
	}
	public String _getVasLeave(String srtData)
	{
		writeToFile("vasleave.log", "start");
		String retStr = srtData;
        writeToFile("vasleave", retStr);
        return retStr;
	}
	public String _getVasWebhook(String srtData)
	{
		String retStr = srtData;
        String fileName = "vas_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".log";
		writeToFile(fileName, "start");
        writeToFile(fileName, retStr);
        return retStr;
	}

	public String _getStageVasJoin(String srtData)
	{
		writeToFile("vasjoin_Stage.log", "start");
		writeToFile("vasjoin_Stage.log", srtData);
		
		String retStr = "_getStageVasJoin";
		String apiUrl = getDefURL(false)+"monitoring/subscriptions";

        // Request Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getToken(false)); // 예시: OAuth2 토큰이나 API 키 등

        // Request Body
        String requestBody = 
        "{" +
        "\"messageType\": \"VasEventTimestamp\"," +
        "\"tenant\": \"yst_stage\"," +
        "\"url\": \"http://yunwonwoo.cafe24.com/pixellot/vas_stagewebhook\"," +
        "\"secret\": \"yst\"," +
        "\"emails\": \"yunwonwoo@ystcorp.co.kr\"" +
        "}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

	    try {
	        // RestTemplate
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);
	
	        // Handle the response
	        System.out.println("Response: " + response.getBody());
	        
	        retStr = response.getStatusCode().toString();
	        retStr += response.getBody();
	    } catch (Exception e) {
	        e.printStackTrace();
	        retStr = "Error: " + e.getMessage();
	    }

		writeToFile("vasjoin_Stage.log", retStr);
		retStr = srtData;
        return retStr;
	}
	public String _getStageVasLeave(String srtData)
	{
		writeToFile("vasleave_Stage.log", "start");
		String retStr = srtData;
        writeToFile("vasleave", retStr);
        return retStr;
	}
	public String _getStageVasWebhook(String srtData)
	{
		String retStr = srtData;
        String fileName = "vas_Stage_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".log";
		writeToFile(fileName, "start");
        writeToFile(fileName, retStr);
        return retStr;
	}

	public String _createEvent(String strClubID, String strName, String strStart, String strEnd, String strVenueID, String strNumber) {
        String fileName = "KDH_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".log";
		String retStr = "_createEvent";

		String apiUrl = getDefURL(true)+"clubs/" + strClubID + "/events";	// KDH
//		String apiUrl = getDefURL(true)+"clubs/" + "66189c940226f33af3ad6355" + "/events";	// KDH
//		String apiUrl = getDefURL(true)+"clubs/" + "617bcac5504fffce149ffb8a" + "/events";	// yst test

        // Request Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getToken(true)); // 예시: OAuth2 토큰이나 API 키 등

        // Request Body
        String requestBody = 
        "{" +
        "\"eventName\": \""	 + strName  + "\"," +
        "\"start$date\": \"" + strStart + "\"," +
        "\"end$date\": \""	 + strEnd   + "\"," +  
        "\"venue\": {" +
    	"\"_id\": \""        + strVenueID + "\"" + 
        	"}," +
        //"\"status\": \"archived\"," +
        "\"scoreboardData\": {" +
        	"\"enable\": true" +
           	"}," +
        "\"productionType\": \"soccer\"," +
        "\"numberOfPlayers\": \"" + strNumber + "\"," +
        "\"permission\": \"club\"," +
        "\"gameType\": \"other\"," +
        "\"commerceType\": \"premium\"," +
        "\"isTest\": false" +
        "}";
        
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

	    try {
	        // RestTemplate
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);
	
	        // Handle the response
	        System.out.println("Response: " + response.getBody());
	        
	        retStr = response.getStatusCode().toString();
	        retStr += response.getBody();
	    } catch (Exception e) {
	        e.printStackTrace();
	        retStr = "Error: " + e.getMessage();
	    }
        
	    LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        writeToFile(fileName, now.format(formatter));
        writeToFile(fileName, strName);
        writeToFile(fileName, strStart);
        writeToFile(fileName, retStr);
        return retStr;
	}
 }

