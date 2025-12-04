Use bilabbonnementdb;

DROP TABLE IF EXISTS Customer;

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