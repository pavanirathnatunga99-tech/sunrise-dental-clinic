CREATE DATABASE IF NOT EXISTS sunrise_dental CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'sunrise'@'%' IDENTIFIED BY 'sunrise123';
GRANT ALL PRIVILEGES ON sunrise_dental.* TO 'sunrise'@'%';
FLUSH PRIVILEGES;

-- Hibernate creates and updates the application tables from the JPA entities.
-- This file intentionally limits manual SQL to database/user provisioning.
