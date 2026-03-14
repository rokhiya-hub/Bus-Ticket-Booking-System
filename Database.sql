-- =================================================================
--   BUSWAY — Complete Database Schema
--   Database: busbooking
-- =================================================================

CREATE DATABASE busbooking;
USE busbooking;

-- -----------------------------------------------------------------
-- 1. USERS
-- -----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS register_user (
    user_id   INT PRIMARY KEY AUTO_INCREMENT,
    name      VARCHAR(100) NOT NULL,
    email     VARCHAR(100) NOT NULL UNIQUE,
    phone     VARCHAR(15),
    password  VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------
-- 2. BUSES
-- -----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS buses (
    bus_id   INT PRIMARY KEY AUTO_INCREMENT,
    bus_no   VARCHAR(50) UNIQUE,
    route    VARCHAR(150),         -- e.g. "Hyderabad - Bangalore"
    type     VARCHAR(50),          -- AC Sleeper / Non-AC Seater / AC Seater
    fare     DOUBLE,
    seats    INT,
    total_seats INT DEFAULT 40,
    departure_time VARCHAR(20),    -- e.g. "06:00 AM"
    arrival_time   VARCHAR(20),    -- e.g. "02:00 PM"
    operator_name  VARCHAR(100)
);

-- -----------------------------------------------------------------
-- 3. BOARDING / DROPPING POINTS (per city)
-- -----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS city_stops (
    stop_id   INT PRIMARY KEY AUTO_INCREMENT,
    city      VARCHAR(100) NOT NULL,
    stop_name VARCHAR(150) NOT NULL,
    stop_type ENUM('boarding','dropping','both') DEFAULT 'both'
);

-- -----------------------------------------------------------------
-- 4. BOOKINGS  (supports multiple passengers per booking)
-- -----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bookings (
    booking_id      INT PRIMARY KEY AUTO_INCREMENT,
    user_id         INT NOT NULL,
    bus_id          INT NOT NULL,
    boarding_point  VARCHAR(150),
    dropping_point  VARCHAR(150),
    journey_date    DATE,
    booking_date    DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_fare      DOUBLE,
    passenger_count INT DEFAULT 1,
    payment_method  VARCHAR(30),   -- UPI / Card / Wallet
    payment_status  VARCHAR(20) DEFAULT 'Pending',  -- Paid / Pending / Failed
    booking_status  VARCHAR(20) DEFAULT 'Confirmed', -- Confirmed / Cancelled
    FOREIGN KEY (user_id) REFERENCES register_user(user_id),
    FOREIGN KEY (bus_id)  REFERENCES buses(bus_id)
);

-- -----------------------------------------------------------------
-- 5. PASSENGERS  (one row per passenger per booking)
-- -----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS passengers (
    passenger_id   INT PRIMARY KEY AUTO_INCREMENT,
    booking_id     INT NOT NULL,
    seat_no        VARCHAR(10) NOT NULL,
    passenger_name VARCHAR(100) NOT NULL,
    age            INT,
    gender         VARCHAR(10),
    phone          VARCHAR(15),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

-- -----------------------------------------------------------------
-- SEED DATA
-- -----------------------------------------------------------------

-- Sample buses
INSERT IGNORE INTO buses (bus_no,route,type,fare,seats,total_seats,departure_time,arrival_time,operator_name) VALUES
('AP1234','Hyderabad - Bangalore','AC Sleeper',    850.00,40,40,'10:00 PM','06:00 AM','RedBus Express'),
('AP5678','Hyderabad - Bangalore','Non-AC Seater', 450.00,50,50,'09:00 PM','07:00 AM','VRL Travels'),
('TN2345','Hyderabad - Chennai',  'AC Seater',     700.00,45,45,'08:00 PM','06:00 AM','KPN Travels'),
('MH3456','Hyderabad - Pune',     'AC Sleeper',    950.00,36,36,'07:00 PM','05:00 AM','Shyamoli'),
('KA4567','Bangalore - Chennai',  'Non-AC Seater', 350.00,55,55,'08:30 PM','04:30 AM','SRS Travels'),
('KA7890','Bangalore - Hyderabad','AC Sleeper',    850.00,40,40,'09:00 PM','07:00 AM','Orange Travels'),
('TN6789','Chennai - Hyderabad',  'AC Seater',     700.00,45,45,'07:00 PM','05:00 AM','Parveen Travels'),
('MH9012','Chennai - Pune',       'Non-AC Seater', 500.00,50,50,'06:00 PM','08:00 AM','Neeta Travels'),
('AP3456','Pune - Hyderabad',     'AC Sleeper',    950.00,36,36,'05:00 PM','03:00 AM','Prasanna'),
('KA5678','Pune - Bangalore',     'AC Seater',     600.00,40,40,'06:30 PM','05:00 AM','SRM Travels');

-- City boarding/dropping points
INSERT IGNORE INTO city_stops (city,stop_name,stop_type) VALUES
('Hyderabad','MGBS (Mahatma Gandhi Bus Station)','both'),
('Hyderabad','Secunderabad Railway Station','both'),
('Hyderabad','Dilsukhnagar','both'),
('Hyderabad','LB Nagar','both'),
('Hyderabad','Kukatpally (KPHB)','both'),
('Hyderabad','Miyapur','both'),
('Hyderabad','Gachibowli','both'),
('Hyderabad','Uppal','both'),
('Bangalore','Majestic (KSRTC)','both'),
('Bangalore','Silk Board','both'),
('Bangalore','Electronic City','both'),
('Bangalore','Whitefield','both'),
('Bangalore','Hebbal','both'),
('Bangalore','Marathahalli','both'),
('Bangalore','Koramangala','both'),
('Bangalore','Yeshwanthpur','both'),
('Chennai','CMBT (Koyambedu)','both'),
('Chennai','Tambaram','both'),
('Chennai','Anna Nagar','both'),
('Chennai','Guindy','both'),
('Chennai','Chromepet','both'),
('Chennai','Perambur','both'),
('Pune','Swargate','both'),
('Pune','Shivajinagar','both'),
('Pune','Hinjewadi','both'),
('Pune','Wakad','both'),
('Pune','Katraj','both'),
('Pune','Hadapsar','both');

-- Admin user  (login: admin@gmail.com / admin123)
INSERT IGNORE INTO register_user (name,email,phone,password)
VALUES ('Admin User','admin@gmail.com','9999999999','admin123');

-- Test user   (login: test@gmail.com / test123)
INSERT IGNORE INTO register_user (name,email,phone,password)
VALUES ('Test User','test@gmail.com','9876543210','test123');

-- =================================================================
--   SELECT * FROM buses;
--   SELECT * FROM city_stops WHERE city='Hyderabad';
--   DESCRIBE bookings;
--   DESCRIBE passengers;
-- =================================================================