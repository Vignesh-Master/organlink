-- MySQL Database Creation Script for OrganLink
-- Run this script as root user before starting the application

-- Create database
CREATE DATABASE IF NOT EXISTS organlink_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Use the database
USE organlink_db;

-- Create user if needed (optional)
-- CREATE USER 'organlink_user'@'localhost' IDENTIFIED BY '12345';
-- GRANT ALL PRIVILEGES ON organlink_db.* TO 'organlink_user'@'localhost';

-- Grant privileges to root user
GRANT ALL PRIVILEGES ON organlink_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;

-- Display success message
SELECT 'OrganLink MySQL database created successfully!' as message;
