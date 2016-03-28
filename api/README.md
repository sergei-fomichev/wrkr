RESTful API with php. 
http://weblab.cs.uml.edu/~sfomiche/wrkr/api/api.php

###Methods:

GET
-Check if user exist
{
"exist":
"email": Email  //string
}
Respond [header - 200]
{
"id": id
"name": name
}
-Check users exircises requested 





POST
-insert a new user as a participant
when user installed the app it immidiatelly asks him to sign in with google profile and insert new user in the database
{
"email": email,  //string
"name": name  //string
}
Respond [header - 200]
{
"status": "ok"
}
When user needs an exercise




PUT
-update exercises state

