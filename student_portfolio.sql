-- Table structure for table `users`
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `student_id` varchar(50) DEFAULT NULL,
  `course_year` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` varchar(20) DEFAULT 'student',
  `bio` TEXT,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `portfolios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `project_name` varchar(255) NOT NULL,
  `description` TEXT,
  `file_data` LONGBLOB,      -- This stores the actual image/PDF
  `file_name` varchar(255),  -- To remember the original file name
  `upload_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`) ON DELETE CASCADE
);

-- Dumping data for table `users`
-- Note: Setting 'Administrator' to role 'admin'
LOCK TABLES `users` WRITE;
INSERT INTO `users` (`full_name`, `student_id`, `course_year`, `email`, `username`, `password`, `role`) VALUES 
('Administrator', '0000-0000', 'N/A', 'admin@nu-moa.edu.ph', 'admin', 'admin123', 'admin'),
('Jhulzen Guerrero', '2025-0001', 'BSIT-1', 'jhulzen@email.com', 'jhulzen', '102806', 'student'),
('Kristine Borres', '2025-0002', 'BSIT-1', 'tine@email.com', 'tine', '082507', 'student'),
('Chelsie Chavez', '2025-0003', 'BSIT-1', 'chels@email.com', 'chels', '12345678', 'student'),
('Aldrich Hilamon', '2025-0004', 'BSIT-1', 'babygirl@email.com', 'babygirl', '12345678', 'student'),
('Carlo Dingle', '2025-1055147', 'BSIT-1', 'carlo@email.com', 'cymric', 'locardingle', 'student');
UNLOCK TABLES;