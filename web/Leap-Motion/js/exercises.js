var exercises = {
	twistsH : {
		name: "Horizontal palm twists",
		numRepeats: 3,
		text: function(){
			return "Rotate your forearm " + this.numRepeats + " times, so that your palm faces up and then down.";
		},
		picture: twistsImg,
		exercise: function(frames){
			
			if(handStatus.value[2] == 2){
				if(Math.round(frames.hands[0].palmNormal[1]) == Math.round(frames.hands[1].palmNormal[1]))
					wristNormal = Math.round(frames.hands[0].palmNormal[1]);
				
				if(wristNormal == -1)
					repDone = 0;
				
				if(repDone == 0 && wristNormal == 1){
					exerciseCounter += 1;
					repDone = 1;
				}
			}else{
				wristNormal = Math.round(hand.palmNormal[1]);

				if(wristNormal == -1)
					repDone = 0;
				
				if(repDone == 0 && wristNormal == 1){
					exerciseCounter += 1;
					repDone = 1;
				}
			}
		}
	},
	twistsV: {
		name: "Vertical palm twists",
		numRepeats: 2,
		text: function(){
			return "Wrists flexion and extension.";
		},
		picture: twistsV,
		exercise: function(frames){
			var grabStrength = Math.round(hand.grabStrength);
			console.log(Math.round(frames.hands[0].palmNormal[2]) +", "+ grabStrength);
			if(handStatus.value[2] == 2){
				if(Math.round(frames.hands[0].palmNormal[2]) == Math.round(frames.hands[1].palmNormal[2]))
					wristNormal = Math.round(frames.hands[0].palmNormal[2]);
				
				if(wristNormal == 0)
					repDone = 0;
				
				if(repDone == 0 && wristNormal == -1 && grabStrength !== 1){
					exerciseCounter += 1;
					repDone = 1;
				}
			}else{
				wristNormal = Math.round(hand.palmNormal[2]);

				if(wristNormal == 0)
					repDone = 0;
				
				if(repDone == 0 && wristNormal == -1 && grabStrength !== 1){
					exerciseCounter += 1;
					repDone = 1;
				}
			}
		}
	}/*,
	knuckle_bend: {
		name: "Knuckle bend",
		numRepeats: 3,
		text: function(){
			return "Bend your fingers using the middle and end joints, but keep the knuckles straight.";
		},
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
		numRepeats: 1,
		text: function(){
			return "Move the thumb "+ this.numRepeats +" times across the palm and back to the starting position.";
		},
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
		numRepeats: 1,
		text: function(){
			return "Draw a circle with your picky finger for "+ this.numRepeats +" seconds.";
		},
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
	*/
}