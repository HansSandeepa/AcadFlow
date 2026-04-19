-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 19, 2026 at 08:58 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `acadflow_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `Admin_id` varchar(100) NOT NULL,
  `User_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`Admin_id`, `User_id`) VALUES
('admin0001', 2);

-- --------------------------------------------------------

--
-- Table structure for table `attendance`
--

CREATE TABLE `attendance` (
  `Attendance_id` int(11) NOT NULL,
  `Session` varchar(100) NOT NULL,
  `Session_type` enum('T','P') NOT NULL,
  `Status` enum('present','absent') NOT NULL COMMENT 'present or absent',
  `Date` date NOT NULL,
  `Course_id` varchar(100) NOT NULL,
  `Stu_id` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `conducted_courses`
--

CREATE TABLE `conducted_courses` (
  `Lecturer_Id` varchar(100) NOT NULL,
  `Course_id` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `course`
--

CREATE TABLE `course` (
  `Course_id` varchar(100) NOT NULL,
  `Name` varchar(100) NOT NULL,
  `Credit` int(11) NOT NULL,
  `Type` enum('T','P') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `course`
--

INSERT INTO `course` (`Course_id`, `Name`, `Credit`, `Type`) VALUES
('BST2112', 'Application of Biosystems Technology', 2, 'T'),
('BST2123', 'Engineering Properties of Biomaterials', 3, 'P'),
('ENT2122', 'Thermodynamics', 2, 'T'),
('ENT2152', 'Properties of Materials and Application', 2, 'T'),
('ICT2122', 'Object Oriented Programming', 2, 'T'),
('ICT2132', 'Object Oriented Programming Practicum', 2, 'P'),
('TCS2112', 'Business Economics', 2, 'T');

-- --------------------------------------------------------

--
-- Table structure for table `course_material`
--

CREATE TABLE `course_material` (
  `Material_id` int(11) NOT NULL,
  `Material_path` varchar(200) NOT NULL,
  `Course_id` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `exam_marks`
--

CREATE TABLE `exam_marks` (
  `Exam_id` int(11) NOT NULL,
  `Grade` varchar(100) NOT NULL,
  `Session_type` enum('T','P') NOT NULL,
  `Assessment_type` enum('T','P') NOT NULL,
  `Mark` int(11) NOT NULL,
  `Stu_id` varchar(100) NOT NULL,
  `Lecturer_id` varchar(100) NOT NULL,
  `Course_id` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `exam_medical`
--

CREATE TABLE `exam_medical` (
  `Emed_id` int(11) NOT NULL,
  `Exam_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `lecturer`
--

CREATE TABLE `lecturer` (
  `Lecturer_Id` varchar(100) NOT NULL,
  `Department` varchar(100) NOT NULL,
  `Office_room` int(11) NOT NULL,
  `User_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `lecturer`
--

INSERT INTO `lecturer` (`Lecturer_Id`, `Department`, `Office_room`, `User_id`) VALUES
('lec0001', 'ICT', 1, 46),
('lec0002', 'MDS', 2, 47),
('lec0003', 'ICT', 3, 48),
('lec0004', 'ET', 4, 49),
('lec0005', 'BST', 5, 50),
('lec0006', 'BST', 6, 51);

-- --------------------------------------------------------

--
-- Table structure for table `medical`
--

CREATE TABLE `medical` (
  `Medical_id` int(11) NOT NULL,
  `Session` varchar(100) NOT NULL,
  `Date` date NOT NULL,
  `Approval` enum('yes','no') NOT NULL COMMENT 'yes or no',
  `Attendance_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `notice`
--

CREATE TABLE `notice` (
  `Notice_id` int(11) NOT NULL,
  `Title` varchar(100) NOT NULL,
  `Content` varchar(100) NOT NULL,
  `Date` date NOT NULL,
  `Admin_id` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `student_courses`
--

CREATE TABLE `student_courses` (
  `Stu_id` varchar(100) NOT NULL,
  `Course_id` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tec_officer`
--

CREATE TABLE `tec_officer` (
  `T_officer_id` varchar(100) NOT NULL,
  `Hire_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `User_id` int(11) NOT NULL,
  `Department` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tec_officer`
--

INSERT INTO `tec_officer` (`T_officer_id`, `Hire_date`, `User_id`, `Department`) VALUES
('to0001', '2020-05-11 18:30:00', 9, 'ICT'),
('to0002', '2021-03-07 18:30:00', 10, 'BST'),
('to0003', '2022-07-18 18:30:00', 11, 'ET'),
('to0004', '2023-01-24 18:30:00', 12, 'ICT'),
('to0005', '2024-11-02 18:30:00', 13, 'BST');

-- --------------------------------------------------------

--
-- Table structure for table `time_table`
--

CREATE TABLE `time_table` (
  `Timetable_id` int(11) NOT NULL,
  `Day` varchar(100) NOT NULL,
  `Time` varchar(100) NOT NULL,
  `Session_type` enum('T','P') NOT NULL,
  `Department` varchar(100) NOT NULL,
  `Course_id` varchar(100) NOT NULL,
  `Admin_id` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `undergraduate`
--

CREATE TABLE `undergraduate` (
  `Stu_id` varchar(100) NOT NULL,
  `Batch` int(11) NOT NULL,
  `Level` int(11) NOT NULL,
  `Semester` int(11) NOT NULL,
  `Department` varchar(100) NOT NULL,
  `User_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `User_id` int(11) NOT NULL,
  `Fullname` varchar(100) NOT NULL,
  `Address` varchar(100) NOT NULL,
  `Dob` date NOT NULL,
  `Gender` enum('M','F') NOT NULL,
  `Password` varchar(100) NOT NULL,
  `Email` varchar(100) NOT NULL,
  `Profile_picture` varchar(100) NOT NULL DEFAULT '/profile_pics/default_pic.jpg'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`User_id`, `Fullname`, `Address`, `Dob`, `Gender`, `Password`, `Email`, `Profile_picture`) VALUES
(2, 'admin', 'Faculty of Technology,University of Ruhuna, Karagoda, Uyangoda', '2000-01-01', 'M', '$2a$10$pnu/zs/sT7VGaR0TlRDi9.uq5SeU2ZIJvCOdO/BH3xpNQBpg3hZ.a', 'admin_acadflow@fot.ruh.ac.lk', '/profile_pics/default_pic.jpg'),
(9, 'Officer ICT1', 'Faculty of Technology, University of Ruhuna', '1990-05-12', 'M', '$2a$10$uhV5QDAeH1yTefOEU685H.2X4Q5qPa3r83tsCYtAlIllvmkErYQIe', 'ict1@company.com', '/profile_pics/default_pic.jpg'),
(10, 'Officer BST1', 'Faculty of Technology, University of Ruhuna', '1991-03-08', 'F', '$2a$10$IrVVJIlZdT4/YesEy3a79ufehh8vyE0EkaQDxUKObGFI6n4qMVb9y', 'bst1@company.com', '/profile_pics/default_pic.jpg'),
(11, 'Officer ET1', 'Faculty of Technology, University of Ruhuna', '1992-07-19', 'M', '$2a$10$ZTbMa5tIM.cyRX178QOorO5q4vMdH5egn0z8jed09UfZIPHbRbAvq', 'et1@company.com', '/profile_pics/default_pic.jpg'),
(12, 'Officer ICT2', 'Faculty of Technology, University of Ruhuna', '1993-01-25', 'F', '$2a$10$0A3KHj2s3rhy.I6.ymnEpe2K8zJs9osVqiin4jvG8dznIo27slPwa', 'ict2@company.com', '/profile_pics/default_pic.jpg'),
(13, 'Officer BST2', 'Faculty of Technology, University of Ruhuna', '1994-11-03', 'M', '$2a$10$Xw1e2Kk7u6OWw7P.fkIAcORg6YLiEDrOTv2sAD5/5KUDRLxBf/gVe', 'bst2@company.com', '/profile_pics/default_pic.jpg'),
(46, 'P.H.P. Nuwan Laksiri', 'Faculty of Technology, University of Ruhuna', '1990-05-12', 'M', '$2a$10$9969TMyXKUapBSeU0YhjCOHlqLtjKqmbZ5KqYDfnfeAIrrvE2RAJO', 'nuwanLaksiri@gmailcom', '/profile_pics/default_pic.jpg'),
(47, 'Dr. K K N B Adikaram', 'Faculty of Technology, University of Ruhuna', '1991-03-08', 'F', '$2a$10$H/ptYUjgY4aYhv7lqoEg7.fEzSu.TdEmJ0EQde5pzbHpr58ALnBHG', 'adhikaram@gmailcom', '/profile_pics/default_pic.jpg'),
(48, 'S.J.k RiDMI Kumarihami', 'Faculty of Technology, University of Ruhuna', '1992-07-19', 'F', '$2a$10$XWN9Y0/oRq6whuIy2rzsQ.6sHJXfQRHqiKADMSxMem6HAkByMVQkq', 'ridmiKumarihami@gmailcom', '/profile_pics/default_pic.jpg'),
(49, 'K.M.K. Weerasnghe', 'Faculty of Technology, University of Ruhuna', '1993-01-25', 'M', '$2a$10$VGh/qUuJcauKEvfHzbbnJuNa8zu3Ur74gMZZJhwVSbo3T/T2uUj6W', 'weerasinghe@gmailcom', '/profile_pics/default_pic.jpg'),
(50, 'J.M. Chinthaka Kumara', 'Faculty of Technology, University of Ruhuna', '1994-11-03', 'M', '$2a$10$vFqNl4UihY2dcHO9fEy8g.DTgGrbYKcUZ9fjMm8WoFsTclEtOCzr.', 'chinathaka@gmailcom', '/profile_pics/default_pic.jpg'),
(51, 'S.M. Kavindu Perera', 'Faculty of Technology, University of Ruhuna', '1991-03-08', 'F', '$2a$10$kWKqThP7X832GE5bMEB9BOy1rMBfXwS6Kn6vkQmzhD5Kp9HzF0TPm', 'kavindu.perera@company.com', '/profile_pics/default_pic.jpg');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`Admin_id`),
  ADD KEY `User_id` (`User_id`);

--
-- Indexes for table `attendance`
--
ALTER TABLE `attendance`
  ADD PRIMARY KEY (`Attendance_id`),
  ADD KEY `Course_id` (`Course_id`),
  ADD KEY `Stu_id` (`Stu_id`);

--
-- Indexes for table `conducted_courses`
--
ALTER TABLE `conducted_courses`
  ADD PRIMARY KEY (`Lecturer_Id`,`Course_id`),
  ADD KEY `Course_id` (`Course_id`);

--
-- Indexes for table `course`
--
ALTER TABLE `course`
  ADD PRIMARY KEY (`Course_id`);

--
-- Indexes for table `course_material`
--
ALTER TABLE `course_material`
  ADD PRIMARY KEY (`Material_id`),
  ADD KEY `Course_id` (`Course_id`);

--
-- Indexes for table `exam_marks`
--
ALTER TABLE `exam_marks`
  ADD PRIMARY KEY (`Exam_id`),
  ADD KEY `Course_id` (`Course_id`),
  ADD KEY `Lecturer_id` (`Lecturer_id`),
  ADD KEY `Stu_id` (`Stu_id`);

--
-- Indexes for table `exam_medical`
--
ALTER TABLE `exam_medical`
  ADD PRIMARY KEY (`Emed_id`),
  ADD KEY `Exam_id` (`Exam_id`);

--
-- Indexes for table `lecturer`
--
ALTER TABLE `lecturer`
  ADD PRIMARY KEY (`Lecturer_Id`),
  ADD KEY `User_id` (`User_id`);

--
-- Indexes for table `medical`
--
ALTER TABLE `medical`
  ADD PRIMARY KEY (`Medical_id`),
  ADD KEY `Attendance_id` (`Attendance_id`);

--
-- Indexes for table `notice`
--
ALTER TABLE `notice`
  ADD PRIMARY KEY (`Notice_id`),
  ADD KEY `Admin_id` (`Admin_id`);

--
-- Indexes for table `student_courses`
--
ALTER TABLE `student_courses`
  ADD PRIMARY KEY (`Stu_id`,`Course_id`),
  ADD KEY `Course_id` (`Course_id`);

--
-- Indexes for table `tec_officer`
--
ALTER TABLE `tec_officer`
  ADD PRIMARY KEY (`T_officer_id`),
  ADD KEY `User_id` (`User_id`);

--
-- Indexes for table `time_table`
--
ALTER TABLE `time_table`
  ADD PRIMARY KEY (`Timetable_id`),
  ADD KEY `Admin_id` (`Admin_id`),
  ADD KEY `Course_id` (`Course_id`);

--
-- Indexes for table `undergraduate`
--
ALTER TABLE `undergraduate`
  ADD PRIMARY KEY (`Stu_id`),
  ADD KEY `User_id` (`User_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`User_id`),
  ADD UNIQUE KEY `Email` (`Email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `attendance`
--
ALTER TABLE `attendance`
  MODIFY `Attendance_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `course_material`
--
ALTER TABLE `course_material`
  MODIFY `Material_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `exam_marks`
--
ALTER TABLE `exam_marks`
  MODIFY `Exam_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `exam_medical`
--
ALTER TABLE `exam_medical`
  MODIFY `Emed_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `medical`
--
ALTER TABLE `medical`
  MODIFY `Medical_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notice`
--
ALTER TABLE `notice`
  MODIFY `Notice_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `time_table`
--
ALTER TABLE `time_table`
  MODIFY `Timetable_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `User_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=61;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `admin`
--
ALTER TABLE `admin`
  ADD CONSTRAINT `admin_ibfk_1` FOREIGN KEY (`User_id`) REFERENCES `user` (`User_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `attendance`
--
ALTER TABLE `attendance`
  ADD CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`Course_id`) REFERENCES `course` (`Course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `attendance_ibfk_2` FOREIGN KEY (`Stu_id`) REFERENCES `undergraduate` (`Stu_id`);

--
-- Constraints for table `conducted_courses`
--
ALTER TABLE `conducted_courses`
  ADD CONSTRAINT `conducted_courses_ibfk_1` FOREIGN KEY (`Course_id`) REFERENCES `course` (`Course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `conducted_courses_ibfk_2` FOREIGN KEY (`Lecturer_Id`) REFERENCES `lecturer` (`Lecturer_Id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `course_material`
--
ALTER TABLE `course_material`
  ADD CONSTRAINT `course_material_ibfk_1` FOREIGN KEY (`Course_id`) REFERENCES `course` (`Course_id`);

--
-- Constraints for table `exam_marks`
--
ALTER TABLE `exam_marks`
  ADD CONSTRAINT `exam_marks_ibfk_1` FOREIGN KEY (`Lecturer_id`) REFERENCES `lecturer` (`Lecturer_Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `exam_marks_ibfk_2` FOREIGN KEY (`Stu_id`) REFERENCES `undergraduate` (`Stu_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `exam_marks_ibfk_3` FOREIGN KEY (`Course_id`) REFERENCES `course` (`Course_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `exam_medical`
--
ALTER TABLE `exam_medical`
  ADD CONSTRAINT `exam_medical_ibfk_1` FOREIGN KEY (`Exam_id`) REFERENCES `exam_marks` (`Exam_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `lecturer`
--
ALTER TABLE `lecturer`
  ADD CONSTRAINT `lecturer_ibfk_1` FOREIGN KEY (`User_id`) REFERENCES `user` (`User_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `medical`
--
ALTER TABLE `medical`
  ADD CONSTRAINT `medical_ibfk_1` FOREIGN KEY (`Attendance_id`) REFERENCES `attendance` (`Attendance_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `notice`
--
ALTER TABLE `notice`
  ADD CONSTRAINT `notice_ibfk_1` FOREIGN KEY (`Admin_id`) REFERENCES `admin` (`Admin_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `student_courses`
--
ALTER TABLE `student_courses`
  ADD CONSTRAINT `student_courses_ibfk_1` FOREIGN KEY (`Stu_id`) REFERENCES `undergraduate` (`Stu_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `student_courses_ibfk_2` FOREIGN KEY (`Course_id`) REFERENCES `course` (`Course_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `tec_officer`
--
ALTER TABLE `tec_officer`
  ADD CONSTRAINT `tec_officer_ibfk_1` FOREIGN KEY (`User_id`) REFERENCES `user` (`User_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `time_table`
--
ALTER TABLE `time_table`
  ADD CONSTRAINT `time_table_ibfk_1` FOREIGN KEY (`Course_id`) REFERENCES `course` (`Course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `time_table_ibfk_2` FOREIGN KEY (`Admin_id`) REFERENCES `admin` (`Admin_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `undergraduate`
--
ALTER TABLE `undergraduate`
  ADD CONSTRAINT `undergraduate_ibfk_1` FOREIGN KEY (`User_id`) REFERENCES `user` (`User_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
