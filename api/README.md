RESTful API with php.  All calls will be made by the mobile app to web server.  
http://weblab.cs.uml.edu/~sfomiche/wrkr/api/api.php  

---

## Database Objects:

*Person*  
>{  
>int "id": userID  
>String "email": user's Google account email  
>String "name": user's Google account name  
>int "karma": user's score. default value is 80  
>int "exercises": # of exercises due. This is 0 at first, and incremented on each POST by mobile when an exercise is due.  It is reset to 0 when a user completes an exercise, but if the exercise times out, it is left as-is.  When a user goes to the website and has exercises to do, this becomes a multiplier of how many "reps" of each exercise he/she must do to complete it.  
>long "timestamp": the UNIX timestamp of the last issued exercise.  The user will have 2 hours to complete an exercise, so a "time.now()" (language dependent) - timestamp = how much time remaining the user has to complete the exercise before it expires.  
>}  

---

##Methods:


###GET  

*Check if user exist* `Done`
>{  
>"exist":  
>"email": Email  //string  
>}  
>Respond [header - 200]  
>{  
>"id": userID  
>"name": name  
>"karma": karma score 
>}  
>OR Respond [header - 401] if not exists  


*Check a user's outstanding exercise count* `Done` 
>{  
>"exercises":  
>"id": userID  
>}  
>Respond [header - 200]  
>{  
>"exercises": number of exercises  
>"timestamp":  a JSONArray of timestamps and states(1 - complete, 0 - incomplete ) e.g. [1459878297362, 0]  
>}  
>OR Respond [header - 401] if user not exists in database  


###POST  

*Insert a new user as a participant* `Done`
>when user installed the app it immediately asks him to sign in with google profile and insert new user in the database  
>{  
>"email": email  //string  
>"name": name //string (empty if unknown)
>}  
>Respond [header - 200]  
>{  
>"status": "ok"  
>"id": userID  
>}  
>OR Respond [header - 401] if already exists  
  
*User has a new exercise that is due*  `Done`
>{  
>"id": userID  
>"timestamp": UNIX timestamp of when the user got notified they have an exercise to do (to determine timeout)  
>}  
>Respond [header - 200]  
>{  
>"status": "ok"  
>"exercises": new exercise count  
>}  
>OR Respond [header - 401] if user does not exist  
