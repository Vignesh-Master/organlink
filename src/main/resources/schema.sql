-- OrganLink Database Schema
-- This file creates all necessary tables for the OrganLink application

-- Create database (run manually if needed)
-- CREATE DATABASE IF NOT EXISTS organlink_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE organlink_db;

-- Countries table
CREATE TABLE IF NOT EXISTS countries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(10) NOT NULL UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    INDEX idx_country_code (code),
    INDEX idx_country_active (is_active)
);

-- States table
CREATE TABLE IF NOT EXISTS states (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(10) NOT NULL,
    country_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE,
    UNIQUE KEY uk_state_country (name, country_id),
    INDEX idx_state_country (country_id),
    INDEX idx_state_code (code),
    INDEX idx_state_active (is_active)
);

-- Hospitals table
CREATE TABLE IF NOT EXISTS hospitals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    address TEXT,
    city VARCHAR(100),
    contact_number VARCHAR(20),
    email VARCHAR(100),
    state_id BIGINT,
    license_number VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    tenant_id VARCHAR(50) DEFAULT 'default',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (state_id) REFERENCES states(id) ON DELETE SET NULL,
    INDEX idx_hospital_code (code),
    INDEX idx_hospital_state (state_id),
    INDEX idx_hospital_active (is_active),
    INDEX idx_hospital_tenant (tenant_id)
);

-- Hospital Users table
CREATE TABLE IF NOT EXISTS hospital_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    hospital_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    failed_login_attempts INT DEFAULT 0,
    account_locked_until TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE CASCADE,
    INDEX idx_hospital_user_id (user_id),
    INDEX idx_hospital_user_hospital (hospital_id),
    INDEX idx_hospital_user_active (is_active)
);

-- Organ Types table
CREATE TABLE IF NOT EXISTS organ_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    preservation_time_hours INT,
    compatibility_factors JSON,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    INDEX idx_organ_type_name (name),
    INDEX idx_organ_type_active (is_active)
);

-- Organizations table (for policy management)
CREATE TABLE IF NOT EXISTS organizations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL UNIQUE,
    location VARCHAR(100),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    can_vote BOOLEAN DEFAULT TRUE,
    can_propose BOOLEAN DEFAULT TRUE,
    register_blockchain BOOLEAN DEFAULT FALSE,
    transaction_hash VARCHAR(66),
    blockchain_address VARCHAR(42),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    INDEX idx_org_id (org_id),
    INDEX idx_org_username (username),
    INDEX idx_org_active (is_active),
    INDEX idx_org_location (location)
);

-- Users table (for general user management)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role ENUM('ADMIN', 'HOSPITAL', 'ORGANIZATION') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    failed_login_attempts INT DEFAULT 0,
    account_locked_until TIMESTAMP NULL,
    password_reset_token VARCHAR(255),
    password_reset_expires TIMESTAMP NULL,
    email_verified BOOLEAN DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    hospital_id BIGINT,
    organization_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE SET NULL,
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE SET NULL,
    INDEX idx_user_username (username),
    INDEX idx_user_email (email),
    INDEX idx_user_role (role),
    INDEX idx_user_active (is_active)
);

-- Donors table
CREATE TABLE IF NOT EXISTS donors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    donor_id VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    blood_group VARCHAR(5) NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    city VARCHAR(100),
    state_id BIGINT,
    hospital_id BIGINT,
    consent_form_hash VARCHAR(64),
    blockchain_tx_hash VARCHAR(66),
    is_active BOOLEAN DEFAULT TRUE,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (state_id) REFERENCES states(id) ON DELETE SET NULL,
    FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE SET NULL,
    INDEX idx_donor_id (donor_id),
    INDEX idx_donor_blood_group (blood_group),
    INDEX idx_donor_state (state_id),
    INDEX idx_donor_hospital (hospital_id),
    INDEX idx_donor_active (is_active)
);

-- Patients table
CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    blood_group VARCHAR(5) NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    city VARCHAR(100),
    state_id BIGINT,
    hospital_id BIGINT,
    organ_type_id BIGINT NOT NULL,
    urgency_level ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',
    medical_condition TEXT,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (state_id) REFERENCES states(id) ON DELETE SET NULL,
    FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE SET NULL,
    FOREIGN KEY (organ_type_id) REFERENCES organ_types(id) ON DELETE RESTRICT,
    INDEX idx_patient_id (patient_id),
    INDEX idx_patient_blood_group (blood_group),
    INDEX idx_patient_organ_type (organ_type_id),
    INDEX idx_patient_urgency (urgency_level),
    INDEX idx_patient_state (state_id),
    INDEX idx_patient_hospital (hospital_id),
    INDEX idx_patient_active (is_active)
);
