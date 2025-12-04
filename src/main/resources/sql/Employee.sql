USE bilabbonnementdb;

DROP TABLE IF EXISTS Employee;

CREATE TABLE Employee
(
    employee_id INT(10)                                                                 NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(30)                                                             NOT NULL UNIQUE,
    password    VARCHAR(255)                                                            NOT NULL,
    name        VARCHAR(50)                                                             NOT NULL,
    role        ENUM ('Dataregistrering', 'Skade_og_udbedring', 'Forretningsudviklere') NOT NULL
);