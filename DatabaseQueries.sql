-- Lab 7 - Database Connection with JDBC in Java Swing
-- Author: Shirabthinath Shivaji
--
-- The application creates the customerdb database and the student table
-- automatically on first run. These queries can also be run by hand in
-- MySQL Workbench.

SHOW DATABASES;

-- create the customerdb database
CREATE DATABASE IF NOT EXISTS customerdb;
USE customerdb;

-- CREATE TABLES & COLUMNS
CREATE TABLE IF NOT EXISTS student(
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(20),
    last_name  VARCHAR(20),
    college    VARCHAR(50),
    age        INT,
    hobbies    VARCHAR(100)
);

-- Read: display the data in the table
SELECT * FROM student;

-- Create: insert sample rows
INSERT INTO student(first_name, age, college, hobbies)
VALUES ('Shirab', 24, 'College of Engineering', 'Cricket, music');
INSERT INTO student(first_name, age, college, hobbies)
VALUES ('Alice', 22, 'Khoury College of Computer Sciences', 'Chess, reading');

-- Update: the WHERE clause uses the primary key (required by safe update mode)
UPDATE student SET hobbies = 'Cricket, music, hiking' WHERE student_id = 1;

-- Delete a row
DELETE FROM student WHERE student_id = 2;

-- Delete the entire table (only if you want to start over)
-- DROP TABLE student;
