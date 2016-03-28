<?php
include 'respond.php';

class userAPI {
	
	private $db;

	function __construct(){
		$this->db = new mysqli('localhost', 'sfomiche', 'sf5481', 'sfomiche') or die(mysql_error());
		if ($this->db->connect_error) {
			die('Connect Error (' . $this->db->connect_errno . ') '
				. $this->db->connect_error);
		}
		
		$this->db->autocommit(FALSE);
	}
	function __destruct(){
		$this->db->close();
	}

	function get_user() {
		if (isset($_GET["exist"]) && isset($_GET["email"])) {
			$email = $_GET["email"];	
			$stmt = $this->db->prepare('SELECT id, name FROM users WHERE email=?');
			$stmt->bind_param('s', $email);
			$stmt->execute();
			$stmt->bind_result($id, $name);
			while ($stmt->fetch()) {
				//echo "$name has $email email!";
				break;
			}
			if ($id <= 0) {
				sendResponse(401, 'User doesnt exist');
				return false;
			}
			$result = array(
				"id" => $id,
				"name" => $name
			);
            
			sendResponse(200, json_encode($result));
			return true;
		}
		sendResponse(400, 'Invalid request');
		return false;
	}
		
}
 
$api = new userAPI;
$api->get_user();
?>