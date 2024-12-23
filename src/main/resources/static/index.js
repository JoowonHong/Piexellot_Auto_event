/**
 * 
 */
//document.write('<script src="https://cdn.jsdelivr.net/npm/hls.js@latest"></script>')

var script = document.createElement('script');
script.src = 'https://cdn.jsdelivr.net/npm/hls.js@latest';
document.head.appendChild(script);

function get_defurl()	{
	const host = window.location.host;
	if(host == "localhost:8080")
		return "";
	return "/pixellot"
}
function btn_click_login() {
    // 버튼이 클릭되었을 때 실행되는 JavaScript 함수
    $.ajax({
        type: "POST",
        url: get_defurl() + "/login",
        success: function(data) {
            // 성공적으로 응답을 받았을 때의 처리
            $("#result").html(data);
            var text_login = document.getElementById("text_login");
            text_login.innerText = data;
            if (data.includes("200")) {
                // 파싱된 데이터 사용
//	            document.getElementById("textInput_eventID").value = "656d214635c723b2505fdf38";
//	            document.getElementById("textInput_overlayURL").value = "http://ystcorp07296.cafe24.com/web2.jsp?id=yst7";
            }
        },
        error: function(xhr, status, error) {
            // 요청이 실패했을 때의 처리
            console.error("Error:", error);
            alert("Error:", error, xhr, status);
        }
    });

}

function btn_click_overlay() {
    // 버튼이 클릭되었을 때 실행되는 JavaScript 함수
    var eventID = document.getElementById("textInput_eventID").value;
    var overlayURL = document.getElementById("textInput_overlayURL").value;

    var requestData = {
        	eventID : eventID,
        	overlayUrl : overlayURL
        };

    var textInput = document.getElementById("text_overlay");
    textInput.innerText = "처리중..";

    $.ajax({
        url: get_defurl() + "/overlayurl",
    	type: "PATCH",
        contentType: 'application/json',
        data: JSON.stringify(requestData),
        success: function(data) {
            // 성공적으로 응답을 받았을 때의 처리
            $("#result").html(data);
            textInput.innerText = data;
        },
        error: function(xhr, status, error) {
            // 요청이 실패했을 때의 처리
            alert(error.message, xhr, status);
        }
    });
}
	    
function btn_click_clubs() {
    // 버튼이 클릭되었을 때 실행되는 JavaScript 함수
    var textInput = document.getElementById("text_clubs");
    textInput.innerText = "처리중..";
    $.ajax({
    	type: "GET",
        url: get_defurl() + "/clubs",
        success: function(data) {
            // 성공적으로 응답을 받았을 때의 처리
            $("#result").html(data);
            textInput.innerText = data;
        },
        error: function(xhr, status, error) {
            // 요청이 실패했을 때의 처리
            console.error("Error:", error);
            alert("Error:", error, xhr, status);
        }
    });
}

function btn_click_event_name() {
    // 버튼이 클릭되었을 때 실행되는 JavaScript 함수
	var comboBox = document.getElementById("myComboBox");
	var textInput = document.getElementById("text_event_name");

	while (comboBox.options.length > 0) {
		comboBox.remove(0);
	}
	var option = document.createElement("option");
	option.text = "---------------- 이벤트 선택 ----------------";
	option.value = "";
	comboBox.add(option);

	textInput.innerText = "처리중..";

    $.ajax({
    	type: "GET",
        url: get_defurl() + "/event",
        success: function(data) {
            // 성공적으로 응답을 받았을 때의 처리
            $("#result").html(data);
            
            const jsonArray = JSON.parse(data);
            textInput.innerText =jsonArray.length; 
            for(var i=0; i<jsonArray.length; i++)
            {
				var option = document.createElement("option");
				option.text = jsonArray[i].eventName;
				option.text += " (" ;
				if ('teams' in jsonArray[i].labels && 'homeTeamName' in jsonArray[i].labels.teams)
					option.text += jsonArray[i].labels.teams.homeTeamName;
				option.text += " vs "; 
				if ('teams' in jsonArray[i].labels && 'awayTeamName' in jsonArray[i].labels.teams)
					option.text += jsonArray[i].labels.teams.awayTeamName;
				option.text += ") - "; 

				var date = new Date(jsonArray[i].start$date);
//				date.setHours(date.getHours() + 9);
				var formattedDate = date.toLocaleString('ko-KR', {
					year: 'numeric',
					month: '2-digit',
					day: '2-digit',
					hour: '2-digit',
					minute: '2-digit',
					hour12: false
				}).replace('. ', '.').replace('. ', '.').replace(' ', ' ').replace(':', ':');
				option.text += formattedDate;
				option.text += " - "; 
				option.text += jsonArray[i].labels.systemName;
				
				option.value = jsonArray[i]._id; 
				comboBox.add(option);
			}
			cb_Selected();
        },
        error: function(xhr, status, error) {
            // 요청이 실패했을 때의 처리
            console.error("Error:", error);
            alert("Error:", error, xhr, status);
        }
    });
}
function cb_Selected() {
	var comboBox = document.getElementById("myComboBox");
	document.getElementById("textInput_eventID").value = comboBox.value;
}

function btn_click_event_play() {
    // 버튼이 클릭되었을 때 실행되는 JavaScript 함수
	var videoContainer = document.getElementById('video-container');
	var textInput = document.getElementById("text_event");
	textInput.innerText = "처리중..";

    $.ajax({
    	type: "GET",
        url: get_defurl() + "/event",
        success: function(data) {
            // 성공적으로 응답을 받았을 때의 처리
            $("#result").html(data);
            
            const jsonArray = JSON.parse(data);
            textInput.innerText =jsonArray.length; 
            for(var i=0; i<jsonArray.length; i++)
            {
				if ('liveStreamUrls' in jsonArray[i] && 'hd' in jsonArray[i].liveStreamUrls) {
//		            textInput.innerText += jsonArray[i].liveStreamUrls.hd;
				    var eventInfo = document.createElement('div');
				    eventInfo.textContent = jsonArray[i].eventName + " / " + jsonArray[i].start$date;
			        videoContainer.appendChild(eventInfo);
	            
		            createVideoPlayer(jsonArray[i].liveStreamUrls.hd);
					if ('pano' in jsonArray[i].liveStreamUrls) {
			            createVideoPlayer(jsonArray[i].liveStreamUrls.pano);
		            }
	            }
			}
//			createVideoPlayer("https://dmvvk3pybr7s5.cloudfront.net/yst_stage/65b0ed33265675f8ca915882/cloud_hls/0_hd_hls.m3u8");
//			createVideoPlayer("https://dmvvk3pybr7s5.cloudfront.net/yst_stage/65b0ed33265675f8ca915882/hd-copied.mp4");
//			createVideoPlayer("https://dmvvk3pybr7s5.cloudfront.net/yst_stage/65b0ed33265675f8ca915882/venue_hls/hd_hls/hd_hls.m3u8");
/*			createVideoPlayer("https://dnw98ykn7b9iq.cloudfront.net/yst/64fe5f0b2d95c447f1b9fcf6/venue_hls/hd_hls/hd_hls.m3u8");
			createVideoPlayer("https://dnw98ykn7b9iq.cloudfront.net/yst/64f951486d56957a5fa97698/venue_hls/hd_hls/hd_hls.m3u8");
			createVideoPlayer("https://dnw98ykn7b9iq.cloudfront.net/yst/64f94bbfff860a77f2f59f31/venue_hls/hd_hls/hd_hls.m3u8");
			createVideoPlayer("https://dnw98ykn7b9iq.cloudfront.net/yst/64fe8c2c29a64ef5f4374170/venue_hls/hd_hls/hd_hls.m3u8");
			createVideoPlayer("https://dnw98ykn7b9iq.cloudfront.net/yst/64fe620d29a64ef5f4373ee2/venue_hls/hd_hls/hd_hls.m3u8");
			createVideoPlayer("https://dnw98ykn7b9iq.cloudfront.net/yst/64ffba0eab23e4715fd5301a/venue_hls/hd_hls/hd_hls.m3u8");
			createVideoPlayer("https://dnw98ykn7b9iq.cloudfront.net/yst/64fa62dc848889af4a6d2c94/venue_hls/hd_hls/hd_hls.m3u8");
			createVideoPlayer("https://dnw98ykn7b9iq.cloudfront.net/yst/64fa6774889d88718af4cc2c/venue_hls/hd_hls/hd_hls.m3u8");
			createVideoPlayer("https://dnw98ykn7b9iq.cloudfront.net/yst/64fa6029ba878ff4fe99de8a/venue_hls/hd_hls/hd_hls.m3u8");
*/			
//			createVideoPlayer("https://dnw98ykn7b9iq.cloudfront.net/yst/65bc6a2215603883ecc4f0d6/venue_hls/hd_hls/hd_hls.m3u8");
			createVideoPlayer("https://dnw98ykn7b9iq.cloudfront.net/yst/65ea7184d8704d58cf647a21/venue_hls/hd_hls/hd_hls.m3u8");
			createVideoPlayer("https://dnw98ykn7b9iq.cloudfront.net/yst/660d04a8827c5b82d0222c01/cloud_hls/0_hd_hls.m3u8");
        },
        error: function(xhr, status, error) {
            // 요청이 실패했을 때의 처리
            console.error("Error:", error);
            alert("Error:", error, xhr, status);
        }
    });
}

function createVideoPlayer(streamUrl) {
    var videoContainer = document.getElementById('video-container');

    // 비디오 요소와 볼륨 정보 요소 생성
    var video = document.createElement('video');
    video.width = 320;
    video.height = 180;
    video.controls = true;
    video.autoplay = true;
    var volumeInfo = document.createElement('div');
    volumeInfo.textContent = '소리: 0 dB';

	var volumeBar = document.createElement('div');
	volumeBar.style.width = '0%';
	volumeBar.style.height = '20px';
	volumeBar.style.backgroundColor = 'blue';
	
	var volumeContainer = document.createElement('div');
	volumeContainer.style.width = '100%';
	volumeContainer.style.backgroundColor = '#ddd';
	volumeContainer.appendChild(volumeBar);
	
	videoContainer.appendChild(video);
    videoContainer.appendChild(volumeInfo);
	videoContainer.appendChild(volumeContainer); // 이제 volumeContainer를 추가합니다.



    // HLS 스트림 처리
    if (Hls.isSupported()) {
        var hls = new Hls();
        hls.loadSource(streamUrl);
        hls.attachMedia(video);
        
/*		hls.on(Hls.Events.MANIFEST_PARSED, function (event, data) {
			console.log('Available qualities:', data.levels);
			data.levels.forEach(function(level) {
				console.log('Resolution: ' + level.width + 'x' + level.height);
				console.log('Bitrate: ' + level.bitrate);
			});

		});
*/
    } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
        video.src = streamUrl;
    }

    // 오디오 컨텍스트와 분석기 설정
    var audioContext = new (window.AudioContext || window.webkitAudioContext)();
    var analyser = audioContext.createAnalyser();
    var source = audioContext.createMediaElementSource(video);
    source.connect(analyser);
    analyser.connect(audioContext.destination);

    analyser.fftSize = 256;
    var bufferLength = analyser.frequencyBinCount;
    var dataArray = new Uint8Array(bufferLength);

    function calculateVolume() {
        analyser.getByteFrequencyData(dataArray);

        var sum = 0;
        for(var i = 0; i < bufferLength; i++) {
            sum += dataArray[i];
        }
        var average = sum / bufferLength;
        var volume = Math.round(average);

        volumeInfo.textContent = '볼륨: ' + volume + ' dB';
        
        requestAnimationFrame(calculateVolume);

	    var volumePercentage = Math.round((average / 255) * 100); // 볼륨을 퍼센트로 변환
	    volumeBar.style.width = volumePercentage + '%'; // 볼륨 바의 너비를 업데이트
    }

    calculateVolume();
}


function btn_click_test() {
    var textInput = document.getElementById("text_test");
    textInput.innerText = "처리중..";
     $.ajax({
    	type: "GET",
        url: get_defurl() + "/hogak",
        success: function(data) {
            // 성공적으로 응답을 받았을 때의 처리
            $("#result").html(data);
            textInput.innerText = data;
        },
        error: function(xhr, status, error) {
            // 요청이 실패했을 때의 처리
            console.error("Error:", error);
            alert("Error:", error, xhr, status);
        }
    });
	
}