var exercises = {
	twistsH : {
		name: "Palm twists",
		numRepeats: 3,
		text: function(){
			return "Rotate your forearm " + this.numRepeats + " times, so that your palm faces up and then down.";
		},
		picture: twistsImg,
		exercise: function(frames){
			
			if(len == 2){
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
		name: "Wrist Bend",
		numRepeats: 4,
		text: function(){
			return "Wrists flexion and extension.";
		},
		picture: twistsV,
		exercise: function(frames){
			var grabStrength = Math.round(hand.grabStrength);
			//console.log(Math.round(frames.hands[0].palmNormal[2]) +", "+ grabStrength);
			if(len == 2){
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
	},
	knuckle_bend: {
		name: "Knuckle bend",
		numRepeats: 3,
		text: function(){
			return "Bend your fingers using the middle and end joints, but keep the knuckles straight.";
		},
		picture: knuckleImg,
		exercise: function(frames){
			var clawState = 0;
			var clawStateL = 0, fingerL;
			var clawStateR = 0, fingerR;
			if(len == 2){
				for(j = 0; j < hand.fingers.length; j++){
					
					
					
					fingerL = frames.hands[0].fingers[j];
					fingerR = frames.hands[1].fingers[j];
					if(fingerL.type >= 1 && fingerL.type <=4 && fingerL.extended == true){
						clawStateL++;
					}
					if(fingerR.type >= 1 && fingerR.type <=4 && fingerR.extended == true){
						clawStateR++;
					}
				}
				if(clawStateL >= 2 && clawStateR >= 2)
					repDone = 0;
				
				//console.log("1 " + clawStateL + "2 " + clawStateR);
				if(clawStateL == 0 && clawStateR == 0 && repDone == 0){
					exerciseCounter++;
					repDone = 1;
				}
			}else{
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
		}
	},
	thumb_bend: {
		name: "Thumb bend",
		numRepeats: 5,
		text: function(){
			return "Move the thumb "+ this.numRepeats +" times across the palm and back to the starting position.";
		},
		picture: thumbImg,
		exercise: function(frames){
			if(len == 2){
				if(frames.hands[0].thumb.extended == true && frames.hands[1].thumb.extended == true){
					repDone = 0;
				}
			
				if(frames.hands[0].thumb.extended == false && frames.hands[1].thumb.extended == false && repDone == 0){
					exerciseCounter++;
					repDone = 1;
				}
			}
			else{
				if(hand.thumb.extended == true){
					repDone = 0;
				}
			
				if(hand.thumb.extended == false && repDone == 0){
					exerciseCounter++;
					repDone = 1;
				}
			}
		}
	}/*,
	Spread Fingers {
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