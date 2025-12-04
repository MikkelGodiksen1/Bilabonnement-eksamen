-- foreign keys correct order to drop tables
DROP TABLE IF EXISTS damagereport;
DROP TABLE IF EXISTS leasecontract;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS vehicle;
DROP TABLE IF EXISTS customer;

-- this Schema.sql file creates all the sql tables automatically each time the program starts up

CREATE TABLE Customer
(
    customer_id       INT(10) PRIMARY KEY AUTO_INCREMENT NOT NULL UNIQUE,
    first_name        VARCHAR(30)                        NOT NULL,
    last_name         VARCHAR(30)                        NOT NULL,
    email             VARCHAR(50)                        NOT NULL UNIQUE,
    phone             VARCHAR(20)                        NOT NULL UNIQUE,
    date_of_birth     DATE                               NOT NULL,
    driver_license_no VARCHAR(20)                        NOT NULL UNIQUE,
    street            VARCHAR(30)                        NOT NULL,
    house_no          VARCHAR(10)                        NOT NULL,
    postal_code       VARCHAR(10)                        NOT NULL,
    city              VARCHAR(30)                        NOT NULL,
    country           VARCHAR(20)                        NOT NULL

);

CREATE TABLE Vehicle
(
    vin_id          VARCHAR(30) PRIMARY KEY NOT NULL UNIQUE,
    registration_no VARCHAR(30)             NOT NULL UNIQUE,
    brand           VARCHAR(10)             NOT NULL,
    model           VARCHAR(10)             NOT NULL,
    model_year      int(10)                 NOT NULL

);

CREATE TABLE Employee
(
    employee_id INT(10)                                                                 NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(30)                                                             NOT NULL UNIQUE,
    password    VARCHAR(255)                                                            NOT NULL,
    name        VARCHAR(50)                                                             NOT NULL,
    role        ENUM ('Dataregistrering', 'Skade_og_udbedring', 'Forretningsudviklere') NOT NULL
);

CREATE TABLE LeaseContract
(
    lease_id    INT(10) AUTO_INCREMENT PRIMARY KEY,
    start_date  DATE           NOT NULL,
    end_date    DATE           NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    customer_id INT(10)        NOT NULL,
    vin_id      VARCHAR(30)    NOT NULL,
    km_start    INT(10)        NOT NULL,
    CONSTRAINT fk_lease_customer
        FOREIGN KEY (customer_id) REFERENCES Customer (customer_id),
    CONSTRAINT fk_lease_vehicle
        FOREIGN KEY (vin_id) REFERENCES Vehicle (vin_id)
);

CREATE TABLE DamageReport
(
    damage_id   INT(10) PRIMARY KEY AUTO_INCREMENT NOT NULL UNIQUE,
    damage_date DATE                               NOT NULL,
    description VARCHAR(255)                       NOT NULL,
    repair_cost DECIMAL(10, 2)                     NOT NULL,
    vin_id      VARCHAR(30)                        NOT NULL,
    employee_id INT(10)                            NOT NULL,
    lease_id    INT(10)                            NOT NULL,
    km_slut     INT(10)                            NOT NULL,
    CONSTRAINT fk_damage_vehicle
        FOREIGN KEY (vin_id) REFERENCES Vehicle (vin_id),
    CONSTRAINT fk_damage_employee
        FOREIGN KEY (employee_id) REFERENCES Employee (employee_id),
    CONSTRAINT fk_damage_lease
        FOREIGN KEY (lease_id) REFERENCES LeaseContract (lease_id)
);