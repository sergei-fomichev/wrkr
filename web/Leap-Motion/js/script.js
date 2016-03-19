var counter = document.getElementById("exerciseProgress");
var canvas = document.getElementById("circle");
var output = document.getElementById("output");
var circleImg = new Image();
circleImg.src = "img/CircleGesture.png";

var knuckleImg = new Image();
knuckleImg.src = "img/knuckle.jpg";

var thumbImg = new Image();
thumbImg.src = "img/thumb.jpg";

var twistsImg = new Image();
twistsImg.src = "img/twists.jpg";


var hand, finger;
var handType =  [["Right", 0], ["Left", 0]];
var handStatus;
var extendedFingers = 0;
var wristNormal, oldNormal = 0;
var repDone = 1;
var exerciseCounter = 0;
var exerciseRepeats = 0;
var exercise;
var currWrkrDOM;

var ctx = canvas.getContext('2d');
var centerX = canvas.width / 2;
var centerY = canvas.height / 2;
var radius = 50;
var amount = 0;
var progressStep = 1;

var controller = new Leap.Controller({
    enableGestures: true,
    frameEventName: 'animationFrame'
}), 
callMuteRequestMade = false;

function draw_circle(){
    ctx.beginPath(); 
	ctx.arc(centerX, centerY, radius, 0, 2 * Math.PI, false); 
	ctx.fillStyle = 'white'; 
	ctx.fill;
	ctx.lineWidth = 5; 
	ctx.strokeStyle = '#000000';
    ctx.stroke();
}
function clear(){
	amount = 0;
	ctx.clearRect(centerX - radius, centerY - radius, radius * 2, radius * 2);
}
function draw(){	
	counter.innerHTML = exerciseCounter; //digital counter
	
	
	
    ctx.beginPath();
    ctx.arc(centerX, centerY, radius, 0, 2 * Math.PI, false);
	ctx.clip(); 
	ctx.fillStyle = '#5cb85c';
	

	
	if(amount !== exerciseCounter*(Math.round(100/exerciseRepeats))){ /////here is a thing with exercise repeats
		amount += progressStep;
		ctx.fillRect(centerX - radius, centerY + radius, radius * 2, -amount);
		//return false;
	}
	else{
		return true;
	}
	
    ctx.beginPath();
    ctx.arc(centerX, centerY, radius, 0, 2 * Math.PI, false);
    ctx.lineWidth = 5;
    ctx.strokeStyle = '#000000';
    ctx.stroke();
	

	 
}

function next_wrkr(){
	exerciseCounter = 0;
	
	var nextWrkrDOM = $(currWrkrDOM).next();
	exercise = $(nextWrkrDOM).attr( "id" );
	if(exercise === undefined){
		$(".action").removeClass("list-group-item-success");
		
		if(handStatus == handType[0][0]){
			++handStatus[0][1];
			handStatus = handType[1][0];
		}else{
			++handStatus[1][1];
			handStatus = handType[0][0];
		}

		$(".hand").html("<p>"+ handStatus +" hand</p>");
		exercise = Object.keys(exercises)[0];
		
		$(".exDescription").html("<h3>"+ exercises[exercise].name +"</h3><p>"+ exercises[exercise].text +"</p>");
		$(".exDescription").append(exercises[exercise].picture);
	
		currWrkrDOM = $( ".action" ).first();
		$(currWrkrDOM).addClass( "active" );
		
	
		exerciseRepeats = exercises[exercise].numRepeats;
		$("#exerciseRepeats").text(exerciseRepeats);
		
		return false;
	}
	$(nextWrkrDOM).addClass( "active" );
	$(".exDescription").html("<h3>"+ exercises[exercise].name +"</h3><p>"+ exercises[exercise].text +"</p>");
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

$("#start-wrkr").click(function(){
	//for(var keys in exercises){
	//	console.log(exercises[keys].name);
	//}
	//console.log(Object.keys(exercises)[0]); //get key
	//console.log(exercises[Object.keys(exercises)[0]].name); //get value
	//console.log(exercises.hand_type[1]); // same value
	
	//<button id="twistsH" type="button" class="action list-group-item"></button>	
	
	for(var keys in exercises){
		$(".exList").append("<button id='"+ keys +"' class='action list-group-item'>" + exercises[keys].name + "</button>");
	}
	
	
	handStatus = handType[0][0];
	$(".hand").html("<p>"+ handStatus +" hand</p>");
	
	exercise = Object.keys(exercises)[0];
	$(".exDescription").html("<h3>"+ exercises[exercise].name +"</h3><p>"+ exercises[exercise].text +"</p>");
	$(".exDescription").append(exercises[exercise].picture);
	$(".progressArea").show();
	
	
	currWrkrDOM = $( ".action" ).first();
	$(currWrkrDOM).addClass( "active" );
	exerciseCounter = 0;
	exerciseRepeats = exercises[exercise].numRepeats;
	$("#exerciseRepeats").text(exerciseRepeats);
	
		//exercise = $(currWrkrDOM).attr( "id" );
	
	
    controller.loop(function(frame) {
		if(exerciseCounter == 0){
			clear();
			draw_circle();
		}
		else if(exerciseCounter >= exerciseRepeats && draw()){
			$(currWrkrDOM).removeClass( "active" ).addClass( "list-group-item-success");
			clear();
			draw_circle();
			
			next_wrkr();
		}
			
		for(i = 0, len = frame.hands.length; i < len; i++){
			hand = frame.hands[i];
			
			$(".progress-bar").attr({
				"aria-valuenow" : Math.round(hand.confidence*100),
				"style" : "width:" + Math.round(hand.confidence*100) + "%"  
			});
			
			if(handStatus.toLowerCase() === hand.type){
				$(".hand").removeClass("bg-danger").addClass("bg-success");
				exercises[exercise].exercise(frame); ////Here is the magic
			}else{
				$(".hand").removeClass("bg-success").addClass("bg-danger");
			}

			
		}

		draw();	
    });

	draw_circle();
	
	
	return false;

});

$.ajax({
  method: "GET",
  url: "js/exercises.js",
  dataType: "script"
});
		
//	controller.on('frame', function(frame){
/*
////exercises here
if(exercise == "twistsH"){

	exercises.twistsH.exercise();

	}
/*else if(exercise == "twistsV"){

	wristNormal = Math.round(hand.palmNormal[2]);
	console.log(wristNormal +","+ oldNormal);
	if(wristNormal !== oldNormal && wristNormal != 1){
		exerciseCounter += 0.5;
		oldNormal = wristNormal;
	}


}
	else if(exercise == "fist"){
	var fingerExt = 0; 
	for(j = 0, len1 = hand.fingers.length; j < len1; j++){
		finger = hand.fingers[j];
		if(finger.extended) 
			fingerExt ++;
	}
	if(fingerExt == 5){
		oldNormal = 1;
	}

	if(fingerExt <= 1 && hand.grabStrength == 1 && oldNormal == 1){
		exerciseCounter += 1;
		oldNormal = null;
	}
}
		
else if(exercise == "knuckle_bend"){
	exercises.knuckle_bend.exercise();
}

else if(exercise == "thumb_bend"){
	exercises.thumb_bend.exercise();

}
else if(exercise == "circle"){
	exercises.circle.exercise(frame);
}


*/
//	  });
  

//controller.connect();
  