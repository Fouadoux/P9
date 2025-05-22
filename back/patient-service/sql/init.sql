SET NAMES utf8mb4;

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
INSERT INTO `patients` VALUES
-- Patients initiaux
('1287c5a3-ef61-4e2e-9153-b294d9892d74','Test','TestInDanger','2004-06-12','MALE','3 Club Road','300-444-5555',1),
('4f3e2d1a-9b21-4f49-8c16-64bbdfd4e5a2','Test','TestNone','1985-04-25','MALE','','',1),
('75c92e6f-c9c3-44a2-a10d-097f2277db01','Test','TestEarlyOnset','2002-06-28','FEMALE','4 Valley Dr','400-555-6666',1),
('9a3c46e1-1f12-4df6-9e0a-2b2cbb0fe648','Test','TestBorderline','1945-05-09','FEMALE','2 High St','',1),

-- 20 patients suppl�mentaires
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab001','Alice','Durand','1990-01-15','FEMALE','12 Rue Lafayette','0102030405',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab002','Bob','Martin','1982-07-09','MALE','3 Impasse des Lilas','0607080910',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab003','Chloé','Lemoine','1995-11-30','FEMALE','28 Boulevard Haussmann','0745123654',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab004','David','Nguyen','1978-03-22','MALE','14 Avenue de Paris','0688776655',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab005','Emma','Bernard','2000-08-10','FEMALE','9 Rue des Fleurs','0123456789',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab006','François','Petit','1988-02-17','MALE','5 Rue de Rome','0112233445',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab007','Gabriel','Moreau','1992-12-01','MALE','11 Rue des Lilas','0633221144',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab008','Héléne','Roy','1981-05-25','FEMALE','20 Chemin Vert','0655443322',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab009','Ismael','Garcia','1993-10-18','MALE','34 Rue Montmartre','0708091011',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab010','Julie','Roux','1986-09-07','FEMALE','7 Allée des Roses','0789654321',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab011','Kévin','Giraud','1991-04-04','MALE','6 Rue du Port','0666443322',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab012','Laura','Marchand','1989-06-20','FEMALE','18 Place Bellecour','0755123498',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab013','Mathieu','Noel','1996-03-03','MALE','10 Chemin du Moulin','0798765432',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab014','Nina','Barbier','2001-12-24','FEMALE','3 Rue des Prés','0677889900',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab015','Olivier','Adam','1984-11-11','MALE','22 Rue Victor Hugo','0644332211',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab016','Pauline','Fernandez','1998-05-05','FEMALE','1 Rue du Marché','0622334455',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab017','Quentin','Lucas','1979-01-09','MALE','45 Avenue Foch','0611223344',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab018','Romain','Chevalier','1994-02-14','MALE','13 Rue Victor Schoelcher','0677008899',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab019','Sophie','Leroux','1987-07-07','FEMALE','6 Quai de la Loire','0612345678',1),
('e1a1b92d-fc71-4f71-a820-1c9e6f4ab020','Thomas','Perrot','1990-10-10','MALE','8 Rue Voltaire','0655447766',1);

UNLOCK TABLES;
