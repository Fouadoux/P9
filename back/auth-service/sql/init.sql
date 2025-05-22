SET NAMES utf8mb4;

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users`
(
    `id`         BIGINT                                  NOT NULL AUTO_INCREMENT,
    `active`     TINYINT(1) NOT NULL,
    `email`      VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `first_name` VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `last_name`  VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `password`   VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `role`       ENUM('ADMIN', 'USER', 'PENDING') COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `users` (`id`, `active`, `email`, `first_name`, `last_name`, `password`, `role`)
VALUES (1, 1, 'admin@example.com', 'Admin', 'Test', '$2a$10$P0XpTTAwWrQMFLpgCC4LLeKqmzqYIaJRhfm5PkQHUDNsKRQNV1NKS',
        'ADMIN'),
       (2, 1, 'user@example.com', 'User', 'Test', '$2a$10$ZvH8hIU3UHgkymD8Iwa6mOrun/GPvboQdJwhBR5RhYzRfxVI8GIbq',
        'USER'),
       (3, 1, 'pending@example.com', 'Pending', 'Test', '$2b$10$WT0rOLs4X9WWCIk10sg5wOG4WqAG6Rp9WA5j.vErbCeJrqjN/oWEi',
        'PENDING'),
       (4, 0, 'notactive@example.com', 'Not', 'Test', '$2b$10$WT0rOLs4X9WWCIk10sg5wOG4WqAG6Rp9WA5j.vErbCeJrqjN/oWEi',
        'USER');

INSERT INTO `users` (`active`, `email`, `first_name`, `last_name`, `password`, `role`)
VALUES (1, 'alice.dupont@example.com', 'Alice', 'Dupont',
        '$2a$10$3IWZ6K2u4xOuhYIpN/5V6OhIjiyzjcp00KfrYOisOFGYODWX9tIaW', 'USER'),
       (1, 'bob.martin@example.com', 'Bob', 'Martin', '$2a$10$3IWZ6K2u4xOuhYIpN/5V6OhIjiyzjcp00KfrYOisOFGYODWX9tIaW',
        'USER'),
       (1, 'caroline.moreau@example.com', 'Caroline', 'Moreau',
        '$2a$10$3IWZ6K2u4xOuhYIpN/5V6OhIjiyzjcp00KfrYOisOFGYODWX9tIaW', 'ADMIN'),
       (1, 'david.leroy@example.com', 'David', 'Leroy', '$2a$10$3IWZ6K2u4xOuhYIpN/5V6OhIjiyzjcp00KfrYOisOFGYODWX9tIaW',
        'USER'),
       (1, 'emma.fabre@example.com', 'Emma', 'Fabre', '$2a$10$3IWZ6K2u4xOuhYIpN/5V6OhIjiyzjcp00KfrYOisOFGYODWX9tIaW',
        'PENDING'),
       (1, 'francois.bernard@example.com', 'François', 'Bernard',
        '$2a$10$3IWZ6K2u4xOuhYIpN/5V6OhIjiyzjcp00KfrYOisOFGYODWX9tIaW', 'USER'),
       (1, 'gwenaelle.roux@example.com', 'Gwenaëlle', 'Roux',
        '$2a$10$3IWZ6K2u4xOuhYIpN/5V6OhIjiyzjcp00KfrYOisOFGYODWX9tIaW', 'USER'),
       (1, 'hugo.leclerc@example.com', 'Hugo', 'Leclerc',
        '$2a$10$3IWZ6K2u4xOuhYIpN/5V6OhIjiyzjcp00KfrYOisOFGYODWX9tIaW', 'ADMIN'),
       (1, 'ines.garcia@example.com', 'Inès', 'Garcia', '$2a$10$3IWZ6K2u4xOuhYIpN/5V6OhIjiyzjcp00KfrYOisOFGYODWX9tIaW',
        'USER'),
       (1, 'julien.morel@example.com', 'Julien', 'Morel',
        '$2a$10$3IWZ6K2u4xOuhYIpN/5V6OhIjiyzjcp00KfrYOisOFGYODWX9tIaW', 'USER');