RESTful API with php. 
http://weblab.cs.uml.edu/~sfomiche/wrkr/api/api.php

Methods:

GET
-Check if user exist
{
"exist":
"email": Email  //string
}
Respond 
header - 200
{
"id": id
"name": name
}

POST
-insert new user as a participant
PUT
-update exercises state

