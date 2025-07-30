-- Create database command (run this in MySQL before starting the application)
-- CREATE DATABASE organlink_db;

-- Initial data for countries
INSERT IGNORE INTO countries (name, code, created_at, updated_at) VALUES
('India', 'IN', NOW(), NOW()),
('United States', 'US', NOW(), NOW()),
('United Kingdom', 'GB', NOW(), NOW());

-- Initial data for states
INSERT IGNORE INTO states (name, code, country_id, created_at, updated_at) VALUES
('Maharashtra', 'MH', (SELECT id FROM countries WHERE code = 'IN'), NOW(), NOW()),
('Delhi', 'DL', (SELECT id FROM countries WHERE code = 'IN'), NOW(), NOW()),
('Karnataka', 'KA', (SELECT id FROM countries WHERE code = 'IN'), NOW(), NOW()),
('Tamil Nadu', 'TN', (SELECT id FROM countries WHERE code = 'IN'), NOW(), NOW()),
('California', 'CA', (SELECT id FROM countries WHERE code = 'US'), NOW(), NOW()),
('New York', 'NY', (SELECT id FROM countries WHERE code = 'US'), NOW(), NOW()),
('England', 'ENG', (SELECT id FROM countries WHERE code = 'GB'), NOW(), NOW()),
('Scotland', 'SCT', (SELECT id FROM countries WHERE code = 'GB'), NOW(), NOW());

-- Initial data for organ types
INSERT IGNORE INTO organ_types (name, description, preservation_time_hours, compatibility_factors, created_at) VALUES
('Heart', 'Cardiac organ for transplantation', 4, '{"blood_type_compatible": true, "size_matching": true, "age_factor": true, "distance_factor": true}', NOW()),
('Liver', 'Hepatic organ for transplantation', 12, '{"blood_type_compatible": true, "size_matching": true, "age_factor": true}', NOW()),
('Kidney', 'Renal organ for transplantation', 24, '{"blood_type_compatible": true, "hla_matching": true, "crossmatch_compatible": true}', NOW()),
('Lung', 'Pulmonary organ for transplantation', 6, '{"blood_type_compatible": true, "size_matching": true, "age_factor": true, "distance_factor": true}', NOW()),
('Pancreas', 'Pancreatic organ for transplantation', 12, '{"blood_type_compatible": true, "size_matching": true, "age_factor": true}', NOW()),
('Cornea', 'Corneal tissue for transplantation', 168, '{"size_matching": true, "age_factor": false}', NOW()),
('Bone', 'Bone tissue for transplantation', 8760, '{"size_matching": true, "age_factor": false}', NOW()),
('Skin', 'Skin tissue for transplantation', 8760, '{"size_matching": false, "age_factor": false}', NOW());
