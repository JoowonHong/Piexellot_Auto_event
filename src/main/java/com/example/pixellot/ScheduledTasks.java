package com.example.pixellot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.pixellot.controller.PixellotController;

@Component
public class ScheduledTasks {
    private PixellotController m_pCon = new PixellotController();

	@Scheduled(cron = "0 0 1 * * ?")
//   @Scheduled(cron = "0 */5 * * * *")
    public void callKDH() {
       	_callKDH();
    }
    
    public String UTCtime(int nHour, int nMin) {
//        LocalDateTime localDateTime = LocalDateTime.now().withHour(nHour).withMinute(nMin).withSecond(0).withNano(0);
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(nHour, nMin, 0, 0));

        // 말레이시아 시간대 설정
        ZoneId malaysiaZoneId = ZoneId.of("Asia/Kuala_Lumpur");

        // 해당 시간대에 맞는 ZonedDateTime 생성
        ZonedDateTime malaysiaZonedDateTime = ZonedDateTime.of(localDateTime, malaysiaZoneId);

        // UTC 시간대로 변환
        ZonedDateTime utcTime = malaysiaZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));

        // ISO 8601 형식으로 포맷팅
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String formattedUtcTime = utcTime.format(formatter);

        // 결과 출력
        return formattedUtcTime;
    }
    public String VenueID(String strVenue) {
//    	return "643bc409dc90725ce2cd721e";
        if ("KDH 163".equals(strVenue))
            return "633acc8f2d4c96fa391ec7c6";
        else if("KDH 1MK SOLO".equals(strVenue))
            return "65f2a97f177cf6308f73d0c2";
        return "";

    }
    public String EName(String strName, int hour, int minute) {
		// [Date] 태그가 포함되어 있는지 확인
		if (strName.contains("[Date]")) {
	        // "d MMMM yyyy" 포맷과 영어 로캘을 사용하여 오늘 날짜를 가져옵니다.
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
	        String currentDate = LocalDate.now().format(formatter);
	        
	        // 시간과 분을 적절한 형식으로 표시합니다.
	        String amPm = hour >= 12 ? "pm" : "am";
	        int displayHour = (hour > 12) ? hour - 12 : (hour == 0 ? 12 : hour);
	        String formattedTime = String.format("%d.%02d%s", displayHour, minute, amPm);
	        
	        // [Date] 태그를 날짜와 시간 형식으로 대체합니다.
	        String replacedString = strName.replace("[Date]", formattedTime + " | " + currentDate);

	        
	        return replacedString;

		}
        return strName;
    }
    
	public void CEvent(int nClubID, String strName, int nStartHour, int nStartMin, int nEndHour, int nEndMin, String strVenue, String strNumber) {
		
		String strClubID = "66189c940226f33af3ad6355";
		switch (nClubID)  {
			case 0: strClubID = "66189c940226f33af3ad6355";	break;
			case 1: strClubID = "66bc609fe9853d4b396f23a8";	break;
			default: break;
		}
		m_pCon.createEvent(strClubID, EName(strName, nStartHour, nStartMin), UTCtime(nStartHour, nStartMin), UTCtime(nEndHour, nEndMin), VenueID(strVenue), strNumber);
	}
   
 
    public void _callKDH() {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
//        dayOfWeek = DayOfWeek.TUESDAY;

        switch (dayOfWeek) {
            case MONDAY:
            	CEvent(0, "U6 & U8 Development : Training @ DO Arena 163",	15, 30, 16, 30,	"KDH 163",		"8x8");
            	CEvent(0, "U10 Development : Training @ DO Arena 1MK",		16, 30, 17, 30,	"KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U8 Elite Team : Training @ DO Arena 163",		16, 35, 18, 0,	"KDH 163",		"8x8");
            	CEvent(0, "U13 Development : Training @ DO Arena 1MK",		17, 30, 18, 30,	"KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U10 Elite Team : Training @ DO Arena 163",		18, 0, 19, 25,	"KDH 163",		"8x8");
            	CEvent(0, "U14 Development : Training @ DO Arena 1MK",		18, 30, 19, 30,	"KDH 1MK SOLO",	"5x5");
            	CEvent(1, "Monday FC : [Date]",							19, 30, 21, 0,	"KDH 163",		"8x8");
            	break;
            case TUESDAY:
            	CEvent(0, "U6 Development : Training @ DO Arena 163",		15, 30, 16, 30, "KDH 163",		"8x8");
            	CEvent(0, "U8 Development : Training @ DO Arena 1MK",		16, 30, 17, 30, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U6 Elite & U10 Development : Training @ DO Arena 163",	16, 30, 17, 30, "KDH 163","8x8");
            	CEvent(0, "Girls Development : Training @ DO Arena 1MK",	17, 30, 18, 30, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "Girls Development : Training @ DO Arena 163",	17, 30, 18, 30, "KDH 163",		"8x8");
            	CEvent(0, "U13 Development : Training @ DO Arena 1MK",		18, 30, 19, 30, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "Girls Elite Team : Training @ DO Arena 163",	18, 30, 19, 30, "KDH 163",		"8x8");
            	CEvent(0, "Ladies Team : Training @ DO Arena 1MK",			19, 30, 21, 0, "KDH 1MK SOLO",	"5x5");
                break;
            case WEDNESDAY:
            	CEvent(0, "U6 & U8 Development : Training @ DO Arena 163",	15, 30, 16, 30, "KDH 163",		"8x8");
            	CEvent(0, "U7 Development : Training @ DO Arena 1MK",		16, 30, 17, 30, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U8 Elite Team : Training @ DO Arena 163",		16, 35, 18, 0, "KDH 163",		"8x8");
            	CEvent(0, "U12 Development : Training @ DO Arena 1MK",		17, 30, 18, 30, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U10 Elite Team : Training @ DO Arena 163",		18, 0, 19, 25, "KDH 163",		"8x8");
            	CEvent(0, "U14 Development : Training @ DO Arena 1MK",		18, 30, 19, 30, "KDH 1MK SOLO",	"5x5");
                break;
            case THURSDAY:
            	CEvent(0, "U7 Development : Training @ DO Arena 1MK",		16, 30, 17, 30, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U11 Elite Team : Training @ DO Arena 163",		16, 35, 18, 0, "KDH 163",		"8x8");
            	CEvent(0, "Girls Development : Training @ DO Arena 1MK",	17, 30, 18, 30, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U14 Development : Training @ DO Arena 1MK",		18, 30, 19, 30, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "Girls Elite Team : Training @ DO Arena 163",	19, 30, 20, 55, "KDH 163",		"8x8");
//				CEvent(0, "HJW TEST",	23, 30, 23, 55, "KDH 163",		"11x11");

				break;
            case FRIDAY:
            	CEvent(0, "U6 Elite Team : Training @ DO Arena 1MK",		15, 30, 16, 30, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U8 Elite Team : Training @ DO Arena 163",		15, 30, 16, 45, "KDH 163",		"8x8");
            	CEvent(0, "U9 Development : Training @ DO Arena 1MK",		16, 30, 17, 30, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U10 Elite Team : Training @ DO Arena 163",		16, 50, 18, 15, "KDH 163",		"8x8");
            	CEvent(0, "U11 Development : Training @ DO Arena 1MK",		17, 30, 18, 30, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "Girls Development : Training @ DO Arena 163",	18, 15, 19, 30, "KDH 163",		"8x8");
            	CEvent(0, "U13 Development : Training @ DO Arena 1MK",		18, 30, 19, 30, "KDH 1MK SOLO",	"5x5");
//            	CEvent(1, "yww test1",		19, 40, 19, 50, "KDH 163",		"8x8");
//            	CEvent(0, "yww test0",		23, 10, 23, 20, "KDH 1MK SOLO",	"5x5");
                break;
            case SATURDAY:
            	CEvent(1, "MK Twelve FC : [Date]",							7, 0, 9, 0, "KDH 163",		"8x8");
            	CEvent(0, "U8 Development : Training @ DO Arena 1MK",		11, 0, 12, 0, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U10 Development : Training @ DO Arena 1MK",		12, 0, 13, 0, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U7 Development : Training @ DO Arena 1MK",		13, 0, 14, 0, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U10 & U13 Development : Training @ DO Arena 163",13, 0, 14, 0, "KDH 163",		"8x8");
            	CEvent(0, "U9 Development : Training @ DO Arena 1MK",		14, 0, 15, 0, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U13 Development : Training @ DO Arena 163",		14, 0, 15, 0, "KDH 163",		"8x8");
            	CEvent(0, "U11 Development : Training @ DO Arena 1MK",		15, 0, 16, 0, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U15 Development : Training @ DO Arena 163",		15, 0, 16, 0, "KDH 163",		"8x8");
                break;
            case SUNDAY:
            	CEvent(0, "Girls Development : Training @ DO Arena 163",	8, 30, 9, 30, "KDH 163",		"8x8");
            	CEvent(0, "Girls Elite Team : Training @ DO Arena 163",	9, 30, 10, 30, "KDH 163",		"8x8");
            	CEvent(0, "U6 Elite Team : Training @ DO Arena 1MK",		10, 0, 11, 0, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U6 Development : Training @ DO Arena 1MK",		11, 0, 12, 0, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U10 Development : Training @ DO Arena 1MK",		12, 0, 13, 0, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U7 Development : Training @ DO Arena 1MK",		13, 0, 14, 0, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U10 & U13 Development : Training @ DO Arena 163",13, 0, 14, 0, "KDH 163",		"8x8");
            	CEvent(0, "U9 Development : Training @ DO Arena 1MK",		14, 0, 15, 0, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U11 Development : Training @ DO Arena 163",		14, 0, 15, 0, "KDH 163",		"8x8");
            	CEvent(0, "U10 Development : Training @ DO Arena 1MK",		15, 0, 16, 0, "KDH 1MK SOLO",	"5x5");
            	CEvent(0, "U13 Development : Training @ DO Arena 163",		15, 0, 16, 0, "KDH 163",		"8x8");
                break;
            default:
                break;
        }
    }
}
