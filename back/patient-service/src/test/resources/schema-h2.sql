CREATE TABLE patients (
                          uid VARCHAR(255) NOT NULL PRIMARY KEY,
                          first_name VARCHAR(255) NOT NULL,
                          last_name VARCHAR(255) NOT NULL,
                          birth_date DATE NOT NULL,
                          gender VARCHAR(255) NOT NULL,
                          address VARCHAR(255),
                          phone VARCHAR(255),
                          active TINYINT(1) NOT NULL

);
