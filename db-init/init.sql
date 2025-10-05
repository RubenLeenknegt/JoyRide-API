-- 1. CREATE TABLES
CREATE TABLE IF NOT EXISTS Users (
                                     id CHAR(36) PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    age INT,
    emailAddress VARCHAR(100) UNIQUE NOT NULL,
    passwordHash VARCHAR(255) NOT NULL,
    userType ENUM('RENTER','OWNER','ADMIN') NOT NULL
    );

CREATE TABLE IF NOT EXISTS Cars (
                                   id CHAR(36) PRIMARY KEY,
    ownerId CHAR(36),
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
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
    locationY FLOAT,
    licensePlate VARCHAR(10)
    );

CREATE TABLE IF NOT EXISTS Reservations (
                                           id CHAR(36) PRIMARY KEY,
    userId CHAR(36),
    carId CHAR(36),
    startDate DATETIME NOT NULL,
    endDate DATETIME NOT NULL
    );

CREATE TABLE IF NOT EXISTS Availabilities (
                                        id CHAR(36) PRIMARY KEY,
    carId CHAR(36),
    availableFrom DATETIME NOT NULL,
    availableTo DATETIME NULL
    );

CREATE TABLE IF NOT EXISTS Rides (
                                    id CHAR(36) PRIMARY KEY,
    startX FLOAT,
    startY FLOAT,
    endX FLOAT,
    endY FLOAT,
    length INT,
    duration INT,
    reservationId CHAR(36)
    );

CREATE TABLE IF NOT EXISTS Photos (
                                     id CHAR(36) PRIMARY KEY,
    carId CHAR(36),
    reservationId CHAR(36) NULL,
    filePath VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS BonusPoints (
                                           id CHAR(36) PRIMARY KEY,
    userId CHAR(36),
    rideId CHAR(36),
    points INT NOT NULL
    );

-- 2. INSERT USERS
INSERT INTO Users (id, firstName, lastName, age, emailAddress, passwordHash, userType) VALUES
                                                                                           ('3fa85f64-5717-4562-b3fc-2c963f66a001','Sanne','Jansen',28,'sanne.jansen@gmail.com','hash1','RENTER'),
                                                                                           ('3fa85f64-5717-4562-b3fc-2c963f66a002','Mohammed','El Amrani',35,'mohammed.elamrani@protonmail.com','hash2','OWNER'),
                                                                                           ('3fa85f64-5717-4562-b3fc-2c963f66a003','Anouk','van Dijk',42,'anouk.vandijk@outlook.com','hash3','ADMIN'),
                                                                                           ('3fa85f64-5717-4562-b3fc-2c963f66a004','Joris','Bakker',30,'joris.bakker@gmail.com','hash4','RENTER'),
                                                                                           ('3fa85f64-5717-4562-b3fc-2c963f66a005','Fatima','Karimi',27,'fatima.karimi@runbox.com','hash5','OWNER'),
                                                                                           ('3fa85f64-5717-4562-b3fc-2c963f66a006','Bas','de Vries',33,'bas.devries@gmail.com','hash6','RENTER'),
                                                                                           ('3fa85f64-5717-4562-b3fc-2c963f66a007','Lotte','Vermeulen',29,'lotte.vermeulen@protonmail.com','hash7','OWNER'),
                                                                                           ('3fa85f64-5717-4562-b3fc-2c963f66a008','Hassan','Bouhaddou',41,'hassan.bouhaddou@gmail.com','hash8','RENTER'),
                                                                                           ('3fa85f64-5717-4562-b3fc-2c963f66a009','Frits','Manuhutu',36,'frits.manuhutu@outlook.com','hash9','OWNER'),
                                                                                           ('3fa85f64-5717-4562-b3fc-2c963f66a010','Rachid','Mansour',25,'rachid.mansour@gmail.com','hash10','RENTER');

-- 3. INSERT CARS
INSERT INTO Cars (id, ownerId, brand, model, buildYear, transmissionType, color, fuelType, length, width, seats, isofixCompatible, phoneMount, luggageSpace, parkingSensors, locationX, locationY, licensePlate) VALUES
                                                                                                                                                                                                      ('4b285f64-5717-4562-b3fc-2c963f66b001','3fa85f64-5717-4562-b3fc-2c963f66a002','Fiat','Panda',2003,'MANUAL','RED','PETROL',3500,1600,5,TRUE,FALSE,200.0,TRUE,52.3702,4.8952, 'AA-00-AA'),
                                                                                                                                                                                                      ('4b285f64-5717-4562-b3fc-2c963f66b002','3fa85f64-5717-4562-b3fc-2c963f66a005','BMW','X5',2018,'AUTOMATIC','BLACK','DIESEL',4900,2000,5,TRUE,TRUE,650.0,TRUE,52.0924,5.1045, 'AA-00-AA'),
                                                                                                                                                                                                      ('4b285f64-5717-4562-b3fc-2c963f66b003','3fa85f64-5717-4562-b3fc-2c963f66a007','Toyota','Yaris',2020,'MANUAL','BLUE','HYBRID',3950,1695,5,TRUE,TRUE,270.0,FALSE,51.9244,4.4777, 'AA-00-AA'),
                                                                                                                                                                                                      ('4b285f64-5717-4562-b3fc-2c963f66b004','3fa85f64-5717-4562-b3fc-2c963f66a005','Audi','A3',2016,'AUTOMATIC','WHITE','PETROL',4500,1800,5,FALSE,TRUE,400.0,TRUE,50.8503,4.3517, 'AA-00-AA'),
                                                                                                                                                                                                      ('4b285f64-5717-4562-b3fc-2c963f66b005','3fa85f64-5717-4562-b3fc-2c963f66a007','Mercedes','C200',2019,'AUTOMATIC','BLACK','DIESEL',4700,1850,5,TRUE,TRUE,500.0,TRUE,52.0934,5.1110, 'AA-00-AA'),
                                                                                                                                                                                                      ('4b285f64-5717-4562-b3fc-2c963f66b006','3fa85f64-5717-4562-b3fc-2c963f66a002','Volkswagen','Golf',2015,'MANUAL','BLUE','PETROL',4300,1780,5,TRUE,FALSE,350.0,FALSE,51.4416,5.4697, 'AA-00-AA'),
                                                                                                                                                                                                      ('4b285f64-5717-4562-b3fc-2c963f66b007','3fa85f64-5717-4562-b3fc-2c963f66a010','Tesla','Model 3',2021,'AUTOMATIC','WHITE','ELECTRIC',4690,1850,5,FALSE,TRUE,450.0,TRUE,52.0907,5.1214, 'AA-00-AA'),
                                                                                                                                                                                                      ('4b285f64-5717-4562-b3fc-2c963f66b008','3fa85f64-5717-4562-b3fc-2c963f66a005','Renault','Clio',2017,'MANUAL','RED','PETROL',4050,1730,5,TRUE,FALSE,300.0,FALSE,52.0116,4.3571, 'AA-00-AA'),
                                                                                                                                                                                                      ('4b285f64-5717-4562-b3fc-2c963f66b009','3fa85f64-5717-4562-b3fc-2c963f66a007','Honda','Civic',2018,'SEMI_AUTOMATIC','BLACK','HYBRID',4600,1800,5,TRUE,TRUE,420.0,TRUE,51.9245,4.4780, 'AA-00-AA'),
                                                                                                                                                                                                      ('4b285f64-5717-4562-b3fc-2c963f66b010','3fa85f64-5717-4562-b3fc-2c963f66a002','Ford','Focus',2014,'MANUAL','WHITE','PETROL',4400,1760,5,FALSE,FALSE,380.0,FALSE,52.3700,4.8950, 'AA-00-AA');

-- 4. INSERT RESERVATIONS
INSERT INTO Reservations (id, userId, carId, startDate, endDate) VALUES
                                                                    ('5c385f64-5717-4562-b3fc-2c963f66c001','3fa85f64-5717-4562-b3fc-2c963f66a001','4b285f64-5717-4562-b3fc-2c963f66b001','2025-10-10 09:00','2025-10-10 18:00'),
                                                                    ('5c385f64-5717-4562-b3fc-2c963f66c002','3fa85f64-5717-4562-b3fc-2c963f66a004','4b285f64-5717-4562-b3fc-2c963f66b002','2025-10-11 08:00','2025-10-11 17:00'),
                                                                    ('5c385f64-5717-4562-b3fc-2c963f66c003','3fa85f64-5717-4562-b3fc-2c963f66a006','4b285f64-5717-4562-b3fc-2c963f66b003','2025-10-12 10:00','2025-10-12 16:00'),
                                                                    ('5c385f64-5717-4562-b3fc-2c963f66c004','3fa85f64-5717-4562-b3fc-2c963f66a008','4b285f64-5717-4562-b3fc-2c963f66b004','2025-10-13 09:00','2025-10-13 18:00'),
                                                                    ('5c385f64-5717-4562-b3fc-2c963f66c005','3fa85f64-5717-4562-b3fc-2c963f66a010','4b285f64-5717-4562-b3fc-2c963f66b005','2025-10-14 07:00','2025-10-14 15:00'),
                                                                    ('5c385f64-5717-4562-b3fc-2c963f66c006','3fa85f64-5717-4562-b3fc-2c963f66a003','4b285f64-5717-4562-b3fc-2c963f66b006','2025-10-15 12:00','2025-10-15 20:00'),
                                                                    ('5c385f64-5717-4562-b3fc-2c963f66c007','3fa85f64-5717-4562-b3fc-2c963f66a002','4b285f64-5717-4562-b3fc-2c963f66b007','2025-10-16 08:00','2025-10-16 18:00'),
                                                                    ('5c385f64-5717-4562-b3fc-2c963f66c008','3fa85f64-5717-4562-b3fc-2c963f66a009','4b285f64-5717-4562-b3fc-2c963f66b008','2025-10-17 09:00','2025-10-17 17:00'),
                                                                    ('5c385f64-5717-4562-b3fc-2c963f66c009','3fa85f64-5717-4562-b3fc-2c963f66a007','4b285f64-5717-4562-b3fc-2c963f66b009','2025-10-18 10:00','2025-10-18 16:00'),
                                                                    ('5c385f64-5717-4562-b3fc-2c963f66c010','3fa85f64-5717-4562-b3fc-2c963f66a001','4b285f64-5717-4562-b3fc-2c963f66b010','2025-10-19 09:00','2025-10-19 18:00');

-- 5. INSERT AVAILABILITIES
INSERT INTO Availabilities (id, carId, availableFrom, availableTo) VALUES
                                                                    ('9d785f64-5717-4562-b3fc-2c963f66d001','4b285f64-5717-4562-b3fc-2c963f66b001','2025-10-01 08:00','2025-10-31 20:00'),
                                                                    ('9d785f64-5717-4562-b3fc-2c963f66d002','4b285f64-5717-4562-b3fc-2c963f66b002','2025-10-01 08:00','2025-10-31 20:00'),
                                                                    ('9d785f64-5717-4562-b3fc-2c963f66d003','4b285f64-5717-4562-b3fc-2c963f66b003','2025-10-01 08:00','2025-10-31 20:00'),
                                                                    ('9d785f64-5717-4562-b3fc-2c963f66d004','4b285f64-5717-4562-b3fc-2c963f66b004','2025-10-01 08:00','2025-10-31 20:00'),
                                                                    ('9d785f64-5717-4562-b3fc-2c963f66d005','4b285f64-5717-4562-b3fc-2c963f66b005','2025-10-01 08:00','2025-10-31 20:00'),
                                                                    ('9d785f64-5717-4562-b3fc-2c963f66d006','4b285f64-5717-4562-b3fc-2c963f66b006','2025-10-01 08:00','2025-10-31 20:00'),
                                                                    ('9d785f64-5717-4562-b3fc-2c963f66d007','4b285f64-5717-4562-b3fc-2c963f66b007','2025-10-01 08:00','2025-10-31 20:00'),
                                                                    ('9d785f64-5717-4562-b3fc-2c963f66d008','4b285f64-5717-4562-b3fc-2c963f66b008','2025-10-01 08:00','2025-10-31 20:00'),
                                                                    ('9d785f64-5717-4562-b3fc-2c963f66d009','4b285f64-5717-4562-b3fc-2c963f66b009','2025-10-01 08:00', NULL),
                                                                    ('9d785f64-5717-4562-b3fc-2c963f66d010','4b285f64-5717-4562-b3fc-2c963f66b010','2025-10-01 08:00', NULL);

-- 6. INSERT RIDES
INSERT INTO Rides (id, startX, startY, endX, endY, length, duration, reservationId) VALUES
                                                                                       ('6d485f64-5717-4562-b3fc-2c963f66d001',52.3702,4.8952,52.0924,5.1045,35,60,'5c385f64-5717-4562-b3fc-2c963f66c001'),
                                                                                       ('6d485f64-5717-4562-b3fc-2c963f66d002',51.9244,4.4777,50.8503,4.3517,80,120,'5c385f64-5717-4562-b3fc-2c963f66c002'),
                                                                                       ('6d485f64-5717-4562-b3fc-2c963f66d003',52.0907,5.1214,52.3700,4.8950,60,90,'5c385f64-5717-4562-b3fc-2c963f66c003'),
                                                                                       ('6d485f64-5717-4562-b3fc-2c963f66d004',52.0116,4.3571,51.9245,4.4780,30,45,'5c385f64-5717-4562-b3fc-2c963f66c004'),
                                                                                       ('6d485f64-5717-4562-b3fc-2c963f66d005',51.4416,5.4697,52.0934,5.1110,70,105,'5c385f64-5717-4562-b3fc-2c963f66c005'),
                                                                                       ('6d485f64-5717-4562-b3fc-2c963f66d006',52.3700,4.8950,52.0924,5.1045,40,60,'5c385f64-5717-4562-b3fc-2c963f66c006'),
                                                                                       ('6d485f64-5717-4562-b3fc-2c963f66d007',52.3702,4.8952,52.0116,4.3571,55,80,'5c385f64-5717-4562-b3fc-2c963f66c007'),
                                                                                       ('6d485f64-5717-4562-b3fc-2c963f66d008',51.9244,4.4777,52.0907,5.1214,65,95,'5c385f64-5717-4562-b3fc-2c963f66c008'),
                                                                                       ('6d485f64-5717-4562-b3fc-2c963f66d009',52.0934,5.1110,51.9245,4.4780,75,110,'5c385f64-5717-4562-b3fc-2c963f66c009'),
                                                                                       ('6d485f64-5717-4562-b3fc-2c963f66d010',52.0116,4.3571,52.3702,4.8952,50,75,'5c385f64-5717-4562-b3fc-2c963f66c010');

-- 7. INSERT PHOTOS
INSERT INTO Photos (id, carId, reservationId, filePath) VALUES
                                                           ('7e585f64-5717-4562-b3fc-2c963f66e001','4b285f64-5717-4562-b3fc-2c963f66b001','5c385f64-5717-4562-b3fc-2c963f66c001','/images/cars/fiat_panda.jpg'),
                                                           ('7e585f64-5717-4562-b3fc-2c963f66e002','4b285f64-5717-4562-b3fc-2c963f66b002','5c385f64-5717-4562-b3fc-2c963f66c002','/images/cars/bmw_x5.jpg'),
                                                           ('7e585f64-5717-4562-b3fc-2c963f66e003','4b285f64-5717-4562-b3fc-2c963f66b003','5c385f64-5717-4562-b3fc-2c963f66c003','/images/cars/toyota_yaris.jpg'),
                                                           ('7e585f64-5717-4562-b3fc-2c963f66e004','4b285f64-5717-4562-b3fc-2c963f66b004','5c385f64-5717-4562-b3fc-2c963f66c004','/images/cars/audi_a3.jpg'),
                                                           ('7e585f64-5717-4562-b3fc-2c963f66e005','4b285f64-5717-4562-b3fc-2c963f66b005','5c385f64-5717-4562-b3fc-2c963f66c005','/images/cars/mercedes_c200.jpg'),
                                                           ('7e585f64-5717-4562-b3fc-2c963f66e006','4b285f64-5717-4562-b3fc-2c963f66b006','5c385f64-5717-4562-b3fc-2c963f66c006','/images/cars/volkswagen_golf.jpg'),
                                                           ('7e585f64-5717-4562-b3fc-2c963f66e007','4b285f64-5717-4562-b3fc-2c963f66b007','5c385f64-5717-4562-b3fc-2c963f66c007','/images/cars/tesla_model3.jpg'),
                                                           ('7e585f64-5717-4562-b3fc-2c963f66e008','4b285f64-5717-4562-b3fc-2c963f66b008','5c385f64-5717-4562-b3fc-2c963f66c008','/images/cars/renault_clio.jpg'),
                                                           ('7e585f64-5717-4562-b3fc-2c963f66e009','4b285f64-5717-4562-b3fc-2c963f66b009','5c385f64-5717-4562-b3fc-2c963f66c009','/images/cars/honda_civic.jpg'),
                                                           ('7e585f64-5717-4562-b3fc-2c963f66e010','4b285f64-5717-4562-b3fc-2c963f66b010','5c385f64-5717-4562-b3fc-2c963f66c010','/images/cars/ford_focus.jpg');

-- 8. INSERT BONUSPOINTS
INSERT INTO BonusPoints (id, userId, rideId, points) VALUES
                                                         ('8f685f64-5717-4562-b3fc-2c963f66f001','3fa85f64-5717-4562-b3fc-2c963f66a001','6d485f64-5717-4562-b3fc-2c963f66d001',50),
                                                         ('8f685f64-5717-4562-b3fc-2c963f66f002','3fa85f64-5717-4562-b3fc-2c963f66a004','6d485f64-5717-4562-b3fc-2c963f66d002',40),
                                                         ('8f685f64-5717-4562-b3fc-2c963f66f003','3fa85f64-5717-4562-b3fc-2c963f66a006','6d485f64-5717-4562-b3fc-2c963f66d003',30),
                                                         ('8f685f64-5717-4562-b3fc-2c963f66f004','3fa85f64-5717-4562-b3fc-2c963f66a008','6d485f64-5717-4562-b3fc-2c963f66d004',25),
                                                         ('8f685f64-5717-4562-b3fc-2c963f66f005','3fa85f64-5717-4562-b3fc-2c963f66a010','6d485f64-5717-4562-b3fc-2c963f66d005',35),
                                                         ('8f685f64-5717-4562-b3fc-2c963f66f006','3fa85f64-5717-4562-b3fc-2c963f66a003','6d485f64-5717-4562-b3fc-2c963f66d006',45),
                                                         ('8f685f64-5717-4562-b3fc-2c963f66f007','3fa85f64-5717-4562-b3fc-2c963f66a002','6d485f64-5717-4562-b3fc-2c963f66d007',55),
                                                         ('8f685f64-5717-4562-b3fc-2c963f66f008','3fa85f64-5717-4562-b3fc-2c963f66a009','6d485f64-5717-4562-b3fc-2c963f66d008',20),
                                                         ('8f685f64-5717-4562-b3fc-2c963f66f009','3fa85f64-5717-4562-b3fc-2c963f66a007','6d485f64-5717-4562-b3fc-2c963f66d009',60),
                                                         ('8f685f64-5717-4562-b3fc-2c963f66f010','3fa85f64-5717-4562-b3fc-2c963f66a001','6d485f64-5717-4562-b3fc-2c963f66d010',30);

-- 9. REFERENCES / Foreign Keys
ALTER TABLE Cars ADD CONSTRAINT fk_owners FOREIGN KEY (ownerId) REFERENCES Users(id);
ALTER TABLE Reservations ADD CONSTRAINT fk_users FOREIGN KEY (userId) REFERENCES Users(id);
ALTER TABLE Reservations ADD CONSTRAINT fk_cars FOREIGN KEY (carId) REFERENCES Cars(id);
ALTER TABLE Rides ADD CONSTRAINT fk_reservations FOREIGN KEY (reservationId) REFERENCES Reservations(id);
ALTER TABLE Photos ADD CONSTRAINT fk_car_photos FOREIGN KEY (carId) REFERENCES Cars(id);
ALTER TABLE Photos ADD CONSTRAINT fk_reservations_photos FOREIGN KEY (reservationId) REFERENCES Reservations(id);
ALTER TABLE BonusPoints ADD CONSTRAINT fk_users_bonus FOREIGN KEY (userId) REFERENCES Users(id);
ALTER TABLE BonusPoints ADD CONSTRAINT fk_rides_bonus FOREIGN KEY (rideId) REFERENCES Rides(id);
ALTER TABLE Availabilities ADD CONSTRAINT fk_cars_availabilities FOREIGN KEY (carId) REFERENCES Cars(id);