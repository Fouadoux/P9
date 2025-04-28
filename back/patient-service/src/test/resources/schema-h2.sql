CREATE TABLE patients (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          uid BINARY(16) NOT NULL,
                          first_name VARCHAR(255) NOT NULL,
                          last_name VARCHAR(255) NOT NULL,
                          birth_date DATE NOT NULL,
                          gender VARCHAR(255) NOT NULL,
                          address VARCHAR(255),
                          phone VARCHAR(255),
                          active BIT NOT NULL

);
