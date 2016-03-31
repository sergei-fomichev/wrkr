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

	function userData() {
		if (isset($_GET["exist"]) && isset($_GET["email"])){
			$email = $_GET["email"];	
			$stmt = $this->db->prepare('SELECT id, name FROM users WHERE email=?');
			$stmt->bind_param('s', $email);
			$stmt->execute();
			$stmt->bind_result($id, $name);
			$stmt->fetch();
			$stmt->close();
			
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
		elseif(isset($_GET["exercises"]) && isset($_GET["id"])){
			$user_id = $_GET["id"];
			
			$stmt = $this->db->prepare('SELECT * FROM users WHERE id=?');
			$stmt->bind_param('s', $user_id);
			$stmt->execute();
			$stmt->store_result();
			//$stmt->fetch();

			if ($stmt->num_rows == 0) {
				sendResponse(401, 'User doesnt exist');
				$stmt->close();
				return false;
			}
			else{
				$stsm = $this->db->prepare('SELECT ts FROM exercises WHERE user_id=?');
				$stsm->bind_param('i', $user_id);
				$stsm->execute();
				$stsm->bind_result($ts);
				$stsm->store_result();
				$stsm->fetch();
			
			
				$result = array(
					"status" => "ok",
					"exercises" => $stsm->num_rows,
					"timestamp" => $ts
				);
				$stsm->close();
				sendResponse(200, json_encode($result));
				return true;
			}
		}
		elseif(isset($_POST["email"])){
			$email = $_POST["email"];
			$name = $_POST["name"];
			$stsm = $this->db->prepare('SELECT id FROM users WHERE email=?');
			$stsm->bind_param('s', $email);
			$stsm->execute();
			$stsm->bind_result($id);
			$stsm->fetch();
			$stsm->close();
			if(!$id){
				$stsm = $this->db->prepare('INSERT INTO users (name, email) VALUES (?, ?)');
				$stsm->bind_param('ss', $name, $email);
				$stsm->execute();
				$id = $this->db->insert_id;
				$stsm->close();
			
				$result = array(
					"status" => "ok",
					"id" => $id
				);
				sendResponse(200, json_encode($result));
				return true;
			}
			else{
				sendResponse(401, 'User already exists');
				return false;
			}			
		}
		elseif(isset($_POST["id"])){
			$user_id = $_POST["id"];
			$timestamp = $_POST["timestamp"];
			$stsm = $this->db->prepare('INSERT INTO exercises (user_id, ts) VALUES (?, ?)');
			$stsm->bind_param('ii', $user_id, $timestamp);
			$stsm->execute();
			$stsm->close();
			
			$stsm = $this->db->prepare('SELECT id FROM exercises WHERE user_id=?');
			$stsm->bind_param('i', $user_id);
			$stsm->execute();
			$stsm->store_result();
			
			$result = array(
				"status" => "ok",
				"exercises" => $stsm->num_rows
			);
			$stsm->close();
			sendResponse(200, json_encode($result));
			return true;
			
		}
		sendResponse(400, 'Invalid request');
		return false;
		
	}
		
}
 
$api = new userAPI;
$api->userData();
?>