-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 14, 2026 at 06:41 PM
-- Server version: 8.0.30
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
                         `Admin_id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                         `User_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`Admin_id`, `User_id`) VALUES
    ('admin0001', 1);

-- --------------------------------------------------------

--
-- Table structure for table `attendance`
--

CREATE TABLE `attendance` (
                              `Attendance_id` int NOT NULL,
                              `Session` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                              `Session_type` enum('T','P') COLLATE utf8mb4_general_ci NOT NULL,
                              `Status` enum('present','absent') COLLATE utf8mb4_general_ci NOT NULL COMMENT 'present or absent',
                              `Date` date NOT NULL,
                              `Course_id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `conducted_courses`
--

CREATE TABLE `conducted_courses` (
                                     `Lecturer_Id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                                     `Course_id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `course`
--

CREATE TABLE `course` (
                          `Course_id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                          `Name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                          `Credit` int NOT NULL,
                          `Type` enum('T','P') COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `exam_marks`
--

CREATE TABLE `exam_marks` (
                              `Exam_id` int NOT NULL,
                              `Grade` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                              `Session_type` enum('T','P') COLLATE utf8mb4_general_ci NOT NULL,
                              `Assessment_type` enum('T','P') COLLATE utf8mb4_general_ci NOT NULL,
                              `Mark` int NOT NULL,
                              `Stu_id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                              `Lecturer_id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                              `Course_id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `exam_medical`
--

CREATE TABLE `exam_medical` (
                                `Emed_id` int NOT NULL,
                                `Exam_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `lecturer`
--

CREATE TABLE `lecturer` (
                            `Lecturer_Id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                            `Department` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                            `Office_room` int NOT NULL,
                            `User_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `medical`
--

CREATE TABLE `medical` (
                           `Medical_id` int NOT NULL,
                           `Session` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                           `Date` date NOT NULL,
                           `Approval` enum('yes','no') COLLATE utf8mb4_general_ci NOT NULL COMMENT 'yes or no',
                           `Attendance_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `notice`
--

CREATE TABLE `notice` (
                          `Notice_id` int NOT NULL,
                          `Title` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                          `Content` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                          `Date` date NOT NULL,
                          `Admin_id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `student_courses`
--

CREATE TABLE `student_courses` (
                                   `Stu_id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                                   `Course_id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tec_officer`
--

CREATE TABLE `tec_officer` (
                               `T_officer_id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                               `Hire_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               `User_id` int NOT NULL,
                               `Department` varchar(100) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `time_table`
--

CREATE TABLE `time_table` (
                              `Timetable_id` int NOT NULL,
                              `Day` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                              `Time` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                              `Session_type` enum('T','P') COLLATE utf8mb4_general_ci NOT NULL,
                              `Department` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                              `Course_id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                              `Admin_id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `undergraduate`
--

CREATE TABLE `undergraduate` (
                                 `Stu_id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                                 `Batch` int NOT NULL,
                                 `Level` int NOT NULL,
                                 `Semester` int NOT NULL,
                                 `Department` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                                 `Attendance_id` int NOT NULL,
                                 `Medical_id` int NOT NULL,
                                 `User_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
                        `User_id` int NOT NULL,
                        `Fullname` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                        `Address` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                        `Dob` date NOT NULL,
                        `Gender` enum('M','F') COLLATE utf8mb4_general_ci NOT NULL,
                        `Password` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                        `Email` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                        `Profile_picture` varchar(100) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '/profile_pics/default_pic.jpg'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`User_id`, `Fullname`, `Address`, `Dob`, `Gender`, `Password`, `Email`, `Profile_picture`) VALUES
    (1, 'admin', 'Faculty of Technology,University of Ruhuna, Karagoda, Uyangoda', '2000-01-01', 'M', 'admin', 'admin_acadflow@fot.ruh.ac.lk', '/profile_pics/default_pic.jpg');

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
  ADD KEY `Course_id` (`Course_id`);

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
  ADD KEY `User_id` (`User_id`),
  ADD KEY `Medical_id` (`Medical_id`),
  ADD KEY `Attendance_id` (`Attendance_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
    ADD PRIMARY KEY (`User_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `attendance`
--
ALTER TABLE `attendance`
    MODIFY `Attendance_id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `exam_marks`
--
ALTER TABLE `exam_marks`
    MODIFY `Exam_id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `exam_medical`
--
ALTER TABLE `exam_medical`
    MODIFY `Emed_id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `medical`
--
ALTER TABLE `medical`
    MODIFY `Medical_id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notice`
--
ALTER TABLE `notice`
    MODIFY `Notice_id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `time_table`
--
ALTER TABLE `time_table`
    MODIFY `Timetable_id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
    MODIFY `User_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

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
    ADD CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`Course_id`) REFERENCES `course` (`Course_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `conducted_courses`
--
ALTER TABLE `conducted_courses`
    ADD CONSTRAINT `conducted_courses_ibfk_1` FOREIGN KEY (`Course_id`) REFERENCES `course` (`Course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `conducted_courses_ibfk_2` FOREIGN KEY (`Lecturer_Id`) REFERENCES `lecturer` (`Lecturer_Id`) ON DELETE CASCADE ON UPDATE CASCADE;

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
    ADD CONSTRAINT `undergraduate_ibfk_1` FOREIGN KEY (`User_id`) REFERENCES `user` (`User_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `undergraduate_ibfk_2` FOREIGN KEY (`Attendance_id`) REFERENCES `attendance` (`Attendance_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `undergraduate_ibfk_3` FOREIGN KEY (`Medical_id`) REFERENCES `medical` (`Medical_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
