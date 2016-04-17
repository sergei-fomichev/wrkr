    function onSuccess(googleUser) {
		
		 $("#signout").show();
		 $("#given-name").text(googleUser.getBasicProfile().getGivenName());
		 //$(".user-image").attr( "src", function() {
		  // return googleUser.getBasicProfile().getImageUrl();
		// });
		$(".dash").show();
		if(document.title == 'WRKR - Dashboard')
			get_status(googleUser);
		if(document.title == 'WRKR - Statistic')
			get_statistic(googleUser);
       }
       function onFailure(googleUser) {
         console.log(error);
       }
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
    function signOut() {
      var auth2 = gapi.auth2.getAuthInstance();
      auth2.signOut().then(function () {
        //console.log('User signed out.');
		 $("#signout").hide();
		 $(".dash").hide();
      });
    }
	
	function get_status(googleUser){
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
					//console.log(msg.exercises);
					$("#ex-requested").text(msg.exercises);
					$(".score-canvas").append("<text x='73' y='120' font-size='58' font-family='Veranda' fill='white'>"+userScore+"</text>");
					$(".score").html($(".score").html());
				});
	  });
	}
	function get_statistic(googleUser){
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
					//console.log(msg.exercises);
					
					for(var [ts, state] of msg.timestamp){
						var date = new Date(ts);
						if(state){
							state = 'Complete';
						}else{
							state = 'Incomplete';
						}
						$(".exer-list").append(" <li class='list-group-item'><span class='badge'>"+state+"</span>"+date+"</li>")
					}
				});
	  });
	}
	var exNum = 0;
	var userId;
	var userScore;