drop database if exists GroupChallenges;
create database GroupChallenges;
use GroupChallenges;

CREATE TABLE IF NOT EXISTS Collage(
	collage_ID INT  AUTO_INCREMENT PRIMARY KEY,
	collage_name VARCHAR(50),
	adress VARCHAR(50),
	telephone VARCHAR(10),
	email VARCHAR(50)
);
/*
CREATE TABLE IF NOT EXISTS Login_data(
	student_ID INT AUTO_INCREMENT PRIMARY KEY,
	password VARCHAR(30)
);
*/

CREATE TABLE IF NOT EXISTS Student(
	student_ID INT AUTO_INCREMENT PRIMARY KEY,
	first_name VARCHAR(30),
	last_name VARCHAR(30),
	username VARCHAR(30),
	telephone VARCHAR(10),
	email VARCHAR(30),
	collage_ID INT,
	FOREIGN KEY (collage_ID) REFERENCES Collage(collage_ID)
);

CREATE TABLE IF NOT EXISTS group_by_challenge(
	group_challenges_ID INT AUTO_INCREMENT PRIMARY KEY,
	date_creation date,
	groupname VARCHAR(30),
	description VARCHAR(160),
	url_whatsapp VARCHAR(60)
);

CREATE TABLE IF NOT EXISTS Group_student(
	group_challenges_ID INT,
	student_ID INT,
	FOREIGN KEY (group_challenges_ID) REFERENCES group_by_challenge(group_challenges_ID),
	FOREIGN KEY (student_ID) REFERENCES Student(student_ID)
);

CREATE TABLE IF NOT EXISTS Challenge(
	code_challenges INT AUTO_INCREMENT PRIMARY KEY,
	date_start date,
	date_finish date,
	status INT,
	name VARCHAR(60),
	category VARCHAR(30),
	info  VARCHAR(160),
	url_img VARCHAR(250)
);

CREATE TABLE IF NOT EXISTS Group_Challenges(
	code_challenges INT,
	group_challenges_ID INT,	
	FOREIGN KEY (code_challenges) REFERENCES Challenge(code_challenges),
    FOREIGN KEY (group_challenges_ID) REFERENCES group_by_challenge(group_challenges_ID)
);

CREATE TABLE IF NOT EXISTS Confirmation(
	join_ID INT  AUTO_INCREMENT PRIMARY KEY,
	available INT,
	code_challenges INT,
	favorite INT,
	student_ID  INT,
	FOREIGN KEY (student_ID ) REFERENCES Student(student_ID ),
	FOREIGN KEY (code_challenges) REFERENCES Challenge(code_challenges)
);