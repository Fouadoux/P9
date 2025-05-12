
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `active` TINYINT(1) NOT NULL,
  `email` VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `first_name` VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_name` VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` ENUM('ADMIN', 'USER', 'PENDING') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `users` (`id`, `active`, `email`, `first_name`, `last_name`, `password`, `role`) VALUES
  (1, 1, 'admin@example.com', 'Admin', 'Test', '$2a$10$P0XpTTAwWrQMFLpgCC4LLeKqmzqYIaJRhfm5PkQHUDNsKRQNV1NKS', 'ADMIN'),
  (2, 1, 'user@example.com', 'User', 'Test', '$2a$10$ZvH8hIU3UHgkymD8Iwa6mOrun/GPvboQdJwhBR5RhYzRfxVI8GIbq', 'USER'),
  (3, 1, 'pending@example.com', 'Pending', 'Test', '$2b$10$WT0rOLs4X9WWCIk10sg5wOG4WqAG6Rp9WA5j.vErbCeJrqjN/oWEi', 'PENDING'),
  (4, 0, 'notactive@example.com', 'Not', 'Test', '$2b$10$WT0rOLs4X9WWCIk10sg5wOG4WqAG6Rp9WA5j.vErbCeJrqjN/oWEi', 'USER');
