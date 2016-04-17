var canvas = document.getElementById("circle");
var output = document.getElementById("output");
var start = document.getElementById("start-wrkr");

var spreadImg = new Image();
spreadImg.src = "img/spread.png";

var knuckleImg = new Image();
knuckleImg.src = "img/knuckle.jpg";

var thumbImg = new Image();
thumbImg.src = "img/thumb.jpg";

var twistsImg = new Image();
twistsImg.src = "img/twists.jpg";

var twistsV = new Image();
twistsV.src = "img/twistsV.jpg";


var hand;
var handType = [["Right", "left", 1]/*,["Left", "right", 1], ["Both", "", 2]*/];
var handStatus;
var hs;
var extendedFingers = 0;
var wristNormal;
var repDone = 0;
var exerciseCounter = 0;
var exerciseRepeats = 0;
var exercise;
var currWrkrDOM;

var ctx = canvas.getContext('2d');
var centerX = canvas.width / 2;
var centerY = canvas.height / 2;
var radius = 10;
var color = '#6520c7';
var normalizedPosition = [];

var incomplete;




var controller = new Leap.Controller({
	enableGestures: true,
	frameEventName: 'animationFrame'
}), 
callMuteRequestMade = false;
controller.use('handEntry');
controller.on('handLost', function(hand){
	$(".connect-device").show();
});
controller.on('handFound', function(hand){
	$(".connect-device").hide();
});


function draw_circle(){
	canvas.width = canvas.width;

	
	ctx.moveTo(0, canvas.height/2);
	ctx.lineTo(canvas.width/2, 0);
	ctx.moveTo(canvas.width/2, 0);
	ctx.lineTo(canvas.width, canvas.height/2);
	ctx.moveTo(canvas.width, canvas.height/2);
	ctx.lineTo(canvas.width/2, canvas.height);
	ctx.moveTo(canvas.width/2, canvas.height);
	ctx.lineTo(0, canvas.height/2);
	ctx.strokeStyle = '#337ab7';
	ctx.stroke();

	

}
function interactive(frame){	
	draw_circle();
	
	//console.log(frame.interactionBox.width.toString());
	for(i = 0; i < frame.hands.length; i++){
		for(j = 0; j < frame.hands[i].fingers.length; j++){
			var finger = frame.hands[i].fingers[j];
		
			var position = finger.dipPosition;
			//console.log(position[1]);
			normalizedPosition[0] = (position[0] + 200)/400;
			normalizedPosition[1] = (position[1] - 25)/350;
			var canvasX = canvas.width * normalizedPosition[0];
			var canvasY = canvas.height * (1 - normalizedPosition[1]);
			ctx.beginPath(); 
			ctx.fillStyle = color;
			ctx.arc(canvasX, canvasY, radius, 0, 2 * Math.PI, true); 
				ctx.fill();
			}
		
	}
	



}

function abort()
{
   throw new Error('Exercise is over');
}

function next_wrkr(){
	exerciseCounter = 0;
	
	
	var nextWrkrDOM = $(currWrkrDOM).next();
	exercise = $(nextWrkrDOM).attr( "id" );
	if(exercise === undefined){ // exercise ended
		$(".action").removeClass("list-group-item-success");
		
		handStatus = hs.next();
		if(handStatus.done === true){
			incomplete--;
			$(".incomplete").text(incomplete);
			if(incomplete == 0){
				$(".exList, .exDescription, .ibox").remove();
				$(".hand").removeClass("bg-success").addClass("bg-warning").html("<p>You are all set. You can go back to the website now.</p>");
				abort();
			}
			$("#start-wrkr").trigger("click");
			return false;
		}

		$(".hand").html("<p>"+ handType[handStatus.value][0] +" hand(s)</p>");
		exercise = Object.keys(exercises)[0];
		
		$(".exDescription").html("<h3>"+ exercises[exercise].name +"</h3><p>"+ exercises[exercise].text() +"</p>");
		$(".exDescription").append(exercises[exercise].picture);
	
		currWrkrDOM = $( ".action" ).first();
		$(currWrkrDOM).addClass( "active" );
		
	
		exerciseRepeats = exercises[exercise].numRepeats;
		$("#exerciseRepeats").text(exerciseRepeats);
		//repDone = 0;
		
		return false;
	}
	$(nextWrkrDOM).addClass( "active" );
	$(".exDescription").html("<h3>"+ exercises[exercise].name +"</h3><p>"+ exercises[exercise].text() +"</p>");
	$(".exDescription").append(exercises[exercise].picture);
	exerciseRepeats = exercises[exercise].numRepeats;
	$("#exerciseRepeats").text(exerciseRepeats);
	
	
	currWrkrDOM = nextWrkrDOM;
	
}

$(".action").click(function(){
	exercise = this.id;
	$(this).addClass( "active" );
	exerciseCounter = 0;
});

$("#start-wrkr").click(function begin_workout(){
		//for(var keys in exercises){
		//	console.log(exercises[keys].name);
		//}
		//console.log(Object.keys(exercises)[0]); //get key
		//console.log(exercises[Object.keys(exercises)[0]].name); //get value
		//console.log(exercises.hand_type[1]); // same value
		
		draw_circle();
		start.disabled = true;
		$(".connect-device").show();
		
		var exList = "";
		for(var keys in exercises)
			exList += "<button id='"+ keys +"' class='action list-group-item'>" + exercises[keys].name + " x" + exercises[keys].numRepeats +"</button>";
		$(".exList").html(exList);
		
	
		hs = handType.keys();
		handStatus = hs.next();

		$(".hand").html("<p>"+ handType[handStatus.value][0] +" hand</p>");
	

		exercise = Object.keys(exercises)[0];
	
		$(".exDescription").html("<h3>"+ exercises[exercise].name +"</h3><p>"+ exercises[exercise].text() +"</p>");
		$(".exDescription").append(exercises[exercise].picture);
		$(".ibox").show();
	
		currWrkrDOM = $( ".action" ).first();
		$(currWrkrDOM).addClass( "active" );
		exerciseCounter = 0;
		exerciseRepeats = exercises[exercise].numRepeats;
		$("#exerciseRepeats").text(exerciseRepeats);
	
	
		controller.loop(function(frame) {
			if (frame.hands.length < 1) return;
		  
			hand = frame.hands[0];
			len = frame.hands.length;

		
			interactive(frame);	//ibox
		
			var percentDone = Math.round((exerciseCounter/exerciseRepeats)*100);
			$(currWrkrDOM).css({
				backgroundImage: "url(img/bg.png)",
				backgroundRepeat: "repeat-y",
				backgroundSize: percentDone+"% auto"
			}); 
		
		
			if(exerciseCounter == exerciseRepeats){
				$(currWrkrDOM).removeClass( "active" ).addClass( "list-group-item-success");
				next_wrkr();
			}

				if(hand.type !== handType[handStatus.value][1] && len == handType[handStatus.value][2]){
					$(".hand").removeClass("bg-info").removeClass("bg-danger").addClass("bg-success");
				
					exercises[exercise].exercise(frame); ////Here is the thing
				}else{
					$(".hand").removeClass("bg-success").removeClass("bg-danger").addClass("bg-danger");
				}

				//counter.innerHTML = exerciseCounter; playback plugin is used
		
			});


	
	
			return false;

		});

		$.ajax({
			method: "GET",
			url: "js/exercises.js",
			dataType: "script"
		});

        function renderButton() {
          gapi.signin2.render('my-signin2', {
            'scope': 'profile email',
            'width': 200,
            'height': 35,
            'longtitle': false,
            'theme': 'dark',
            'onsuccess': onSuccess,
            'onfailure': onFailure
          });
        }
	    function onSuccess(googleUser) {
			$.when(
				$.ajax({
					method: "GET",
					async: false,
					url: "http://weblab.cs.uml.edu/~sfomiche/wrkr/api/api.php",
					data: { exist: "", email: googleUser.getBasicProfile().getEmail() },
					dataType: "json"
				})
				.done(function( msg ) {
					userId = msg.id;
					userScore = msg.karma;
				}))
				.then(function(msg){
					$.ajax({
						method: "GET",
						sync: false,
						url: "http://weblab.cs.uml.edu/~sfomiche/wrkr/api/api.php",
						data: { exercises: "", id: userId},
						dataType: "json"
					})
					.done(function( msg ) {
						incomplete = msg.exercises;
						$(".incomplete").text(msg.exercises);
					});
				});
		
	    }
        function onFailure(googleUser) {
          console.log(error);
        }

	
	
		controller.connect();
  