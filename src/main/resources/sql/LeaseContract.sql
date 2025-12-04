Use bilabbonnementdb;

DROP TABLE IF EXISTS LeaseContract;

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
