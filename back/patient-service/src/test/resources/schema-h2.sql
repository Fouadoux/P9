CREATE TABLE patients (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          first_name VARCHAR(255) NOT NULL,
                          last_name VARCHAR(255) NOT NULL,
                          birth_date DATE NOT NULL,
                          gender VARCHAR(255) NOT NULL,
                          address VARCHAR(255),
                          phone_number VARCHAR(255)
);
