-- Lab 7 - Database Connection with JDBC in Java Swing
-- Author: Shirabthinath Shivaji
--
-- The application creates the customerdb database and the student_profile
-- table automatically on the first run. These queries can also be run by
-- hand in MySQL Workbench.

SHOW DATABASES;

CREATE DATABASE IF NOT EXISTS customerdb;
USE customerdb;

-- Age is not stored: it is calculated from date_of_birth, so it never goes
-- out of date. email is UNIQUE so two students cannot share one email.
CREATE TABLE IF NOT EXISTS student_profile (
    student_id     INT PRIMARY KEY AUTO_INCREMENT,
    first_name     VARCHAR(30)  NOT NULL,
    last_name      VARCHAR(30)  NOT NULL,
    gender         VARCHAR(10)  NOT NULL,
    date_of_birth  DATE         NOT NULL,
    phone          VARCHAR(20)  NOT NULL,
    email          VARCHAR(100) NOT NULL UNIQUE,
    college        VARCHAR(50)  NOT NULL,
    continent      VARCHAR(30),
    experience     VARCHAR(500),
    hobbies        VARCHAR(200),
    photo_path     VARCHAR(500)
);

-- If student_profile was created by an earlier version without the continent
-- and experience columns, add them (the app also does this automatically):
-- ALTER TABLE student_profile ADD COLUMN continent  VARCHAR(30)  AFTER college;
-- ALTER TABLE student_profile ADD COLUMN experience VARCHAR(500) AFTER continent;

-- Read
SELECT * FROM student_profile;

-- Read with the age calculated in SQL
SELECT student_id, first_name, last_name,
       TIMESTAMPDIFF(YEAR, date_of_birth, CURDATE()) AS age, college
FROM student_profile;

-- Create
INSERT INTO student_profile
    (first_name, last_name, gender, date_of_birth, phone, email, college, continent,
     experience, hobbies, photo_path)
VALUES
    ('Shirabthinath', 'Shivaji', 'Male', '2001-11-05', '944-551-1456',
     'shivaji.s@northeastern.edu', 'College of Engineering', 'Asia',
     'Software engineering intern', 'Hiking and music', NULL);

-- Update (the WHERE clause uses the primary key, as safe update mode requires)
UPDATE student_profile SET hobbies = 'Cricket, music and hiking' WHERE student_id = 1;

-- Delete
DELETE FROM student_profile WHERE student_id = 1;
