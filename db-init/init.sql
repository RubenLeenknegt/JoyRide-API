CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL
    );

CREATE TABLE IF NOT EXISTS Car (
    id VARCHAR(255) PRIMARY KEY,
    brand VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    buildYear INT NOT NULL,
    transmissionType ENUM('MANUAL','AUTOMATIC','SEMI_AUTOMATIC') NOT NULL,
    color ENUM('RED','BLUE','BLACK','WHITE','OTHER') NOT NULL,
    fuelType ENUM('PETROL','DIESEL','ELECTRIC','HYBRID') NOT NULL,
    length INT,
    width INT,
    seats INT,
    isofixCompatible BOOLEAN,
    phoneMount BOOLEAN,
    luggageSpace DOUBLE,
    parkingSensors BOOLEAN,
    locationX FLOAT,
    locationY FLOAT
    );

INSERT INTO users (name)
VALUES ('Alice'), ('Bob')
    ON DUPLICATE KEY UPDATE name=name;

INSERT INTO Car (
    id, brand, model, buildYear, transmissionType, color, fuelType,
    length, width, seats, isofixCompatible, phoneMount, luggageSpace,
    parkingSensors, locationX, locationY
) VALUES
      ('1', 'Fiat', 'Panda', 2003, 'MANUAL', 'RED', 'PETROL', 3500, 1600, 5, TRUE, FALSE, 200.0, TRUE, 52.3702, 4.8952),
      ('2', 'BMW', 'X5', 2018, 'AUTOMATIC', 'BLACK', 'DIESEL', 4900, 2000, 5, TRUE, TRUE, 650.0, TRUE, 48.8566, 2.3522),
      ('3', 'Toyota', 'Yaris', 2020, 'MANUAL', 'BLUE', 'HYBRID', 3950, 1695, 5, TRUE, TRUE, 270.0, FALSE, 51.9244, 4.4777);
