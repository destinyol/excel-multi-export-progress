CREATE TABLE td_travel (
     id INT AUTO_INCREMENT PRIMARY KEY,
     user_id VARCHAR(255) NOT NULL,
     remarks VARCHAR(255),
     user_name VARCHAR(255) NOT NULL,
     depart_id VARCHAR(255) NOT NULL,
     depart_name VARCHAR(255) NOT NULL,
     serial_number VARCHAR(255) NOT NULL,
     project_code VARCHAR(255) NOT NULL,
     project_name VARCHAR(255) NOT NULL,
     amount DECIMAL(10, 2) NOT NULL,
     travel_status INT NOT NULL,
     occur_time VARCHAR(255) NOT NULL,
     create_time VARCHAR(255) NOT NULL
);