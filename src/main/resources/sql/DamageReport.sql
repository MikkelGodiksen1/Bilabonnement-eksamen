Use bilabbonnementdb;

DROP TABLE IF EXISTS DamageReport;

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