/*
브라우저? 는 html 코드를 위에서 아래로 읽어드리는데 어떤 버튼, 어떤 텍스트
영역에 이벤트를 등록하고 싶어도 body에서 이벤트를 등록하고자 하는 버튼이나,
텍스트 영역을 읽어드리기 전에 버튼이나, 텍스트 영역에 이벤트를 등록하려고 하면
안된다. 따라서 window라는 DOM 객체의 프로퍼티중 하나인 onload를 이용한다. onload
프로퍼티는 window 객체의 이벤트중 하나로 브라우저가 html코드를 끝까지 다
읽었을때의 시점을 의미한다. 따라서 onload에 함수를 동록하면 해당함수는
브라우저가 html코드를 끝까지 다 읽었을 때 실행된다. 다 읽었다면 니가 읽은거중에
버튼이 하나있었지? 거기에 이 이벤트 등록해 라고 하는것이다. 웹 브라우저는
HTML문서를 위에서부터 차례대로 해석하면서 마크업한다.

도중에 <script></script>를 만나면 태그 내에 기술된 js를 해석하여 실행한다.
코드를 모두 해석해서 실행하고 나서야 다음 HTML를 해석하여 마크업해나간다.
이처럼 script요소는 동기 실행(블로킹 실행)된다.
결과적으로 script 요소안의 js코드 실행이 완료되기 전까지는 브라우저에 HTML문서
해석이 멈춘다.

이때 onload, onclick 같은 프로퍼티들을 이벤트 처리기 프로퍼티라고한다.
이벤트 처리 함수가 등록되어있지 않는 이벤트 처리기 프로퍼티는 null이 들어가있다.
따라서 이벤트 처리기를 제거 하고 싶다면 window.onload = null; 을 하면 된다.
onload 이벤트에 등록한 이벤트 처리기함수는 보통 아래와 같이 이벤트 처리기의
설정을 비롯한 기타 프로그램의 초기화 설정을 수행한다.
*/

var startTimer, startTime;
let submitExUseJs;
let encryptionObject = encryptorFunc();
let printCookies;
window.onload = function () {
	var startBtn = document.getElementById('start');  // start button
	var stopBtn = document.getElementById('stop');    // stop button
	var timerLabel = document.getElementById(
		'timeLabel');  // for to show elapsed time that until click the start
	// button and click stop button

	submitExUseJs = function () {
		let form = document.getElementById('loginform');
		let user_id = form.user_id.value;
		let user_pw = form.user_pw.value;

		if (user_id === '' || user_pw === '')
			alert('please insert id or pw');
		else {
			form.method = 'post';
			form.action = 'login';
			form.submit();
		}
	};
	function startFunc() {
		startBtn.onclick = null;
		startTime = new Date();
		startTimer = window.setInterval(function () {
			var now = new Date();
			timerLabel.innerText = ((now - startTime) / 1000).toFixed(2);
		})
	};

	function stopFunc() {
		startBtn.onclick = startFunc;
		window.clearInterval(startTimer);
	};

	printCookies = function () {
		let cookies = document.cookie;
		alert(cookies);
	}

	startBtn.onclick = startFunc;
	stopBtn.onclick = stopFunc;
} 