var exercises = {
	twistsH : {
		name: "Palm twists",
		numRepeats: 5,
		text: "Rotate your forearm, so that your palm faces up and then down.",
		picture: twistsImg,
		exercise: function(){
			wristNormal = Math.round(hand.palmNormal[1]);
			if(wristNormal !== oldNormal && wristNormal != -1){
				exerciseCounter += 0.5;
				oldNormal = wristNormal;
			}
		}
	},
	knuckle_bend: {
		name: "Knuckle bend",
		numRepeats: 3,
		text: "Bend your fingers using the middle and end joints, but keep the knuckles straight.",
		picture: knuckleImg,
		exercise: function(){
			var clawState = 0;
			for(j = 0; j < hand.fingers.length; j++){
				finger = hand.fingers[j];
				if(finger.type >= 1 && finger.type <=4 && finger.extended == true){
					clawState++;
				}
			}
			if(clawState >= 2)
				repDone = 0;
		
			else if(clawState == 0 && repDone == 0){
				exerciseCounter++;
				repDone = 1;
			}
		}
	},
	thumb_bend: {
		name: "Thumb bend",
		numRepeats: 5,
		text: "Move the thumb across the palm and back to the starting position.",
		picture: thumbImg,
		exercise: function(){
			if(hand.thumb.extended == true){
				repDone = 0;
			}
			
			if(hand.thumb.extended == false && repDone == 0){
				exerciseCounter++;
				repDone = 1;
			}
		}
	},
	circle: {
		name: "Draw a circle",
		numRepeats: 3,
		text: "Draw a circle with your picky finger.",
		picture: circleImg,
		exercise: function(frame){
			var duration;
			frame.gestures.forEach(function(gesture){
				if(frame.valid && frame.gestures.length > 0){
					duration = gesture.duration;
					//counter.innerHTML = duration;
					
					duration /= 1000000;
					exerciseCounter = Math.round(duration);	
				}
			});
		}
	}
}