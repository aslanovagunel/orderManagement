--CREATE TABLE users (
--  id CHAR(36) PRIMARY KEY,
--  phone_number VARCHAR(20) NOT NULL UNIQUE,
--  full_name VARCHAR(100) NOT NULL,
--  email VARCHAR(100) NOT NULL UNIQUE,
--  role ENUM('ADMIN', 'USER') NOT NULL,
--  is_active BOOLEAN DEFAULT TRUE,
--  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
--);
--

INSERT INTO users (
  id, phone_number, full_name, email, role, is_active, created_at, updated_at
) VALUES (
  UUID(),
  CONCAT('+99455', FLOOR(1000000 + RAND()*8999999)),
  'aslanli',
  CONCAT('user', FLOOR(1000 + RAND()*8999), '@gmail.com'),
  'ADMIN',
  TRUE,
  '2025-06-06',
  '2025-06-03'
);