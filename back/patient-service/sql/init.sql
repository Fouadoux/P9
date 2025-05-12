
DROP TABLE IF EXISTS `patients`;

CREATE TABLE `patients` (
  `uid` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `birth_date` date NOT NULL,
  `gender` enum('MALE','FEMALE') NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `uk_patients_identity` (`first_name`,`last_name`,`birth_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



LOCK TABLES `patients` WRITE;
INSERT INTO `patients` VALUES ('1287c5a3-ef61-4e2e-9153-b294d9892d74','Test','TestInDanger','2004-06-12','MALE','3 Club Road','300-444-5555',1),
('4f3e2d1a-9b21-4f49-8c16-64bbdfd4e5a2','Test','TestNone','1985-04-25','MALE','','',1),
('75c92e6f-c9c3-44a2-a10d-097f2277db01','Test','TestEarlyOnset','2002-06-28','FEMALE','4 Valley Dr','400-555-6666',1),
('9a3c46e1-1f12-4df6-9e0a-2b2cbb0fe648','Test','TestBorderline','1945-05-09','FEMALE','2 High St','',1);
UNLOCK TABLES;
