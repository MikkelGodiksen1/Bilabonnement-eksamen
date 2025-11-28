package com.example.demo.model;

import jakarta.persistence.*;

import java.sql.Date;
import java.time.LocalDate;

@Entity
public class LeaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;



    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerModel customer;

    public void setCustomer(CustomerModel customer) {
        this.customer = customer;
    }

    private String vinId;
    private int kmStart;

    public LeaseModel() {

    }


    public void setId(long id) {
        this.id = id;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setVinId(String vinId) {
        this.vinId = vinId;
    }



    public void setKmStart(int kmStart) {
        this.kmStart = kmStart;
    }

    public long getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public LocalDate getEndDate() {
        return endDate;
    }


    public String getVinId() {
        return vinId;
    }

    public int getKmStart() {
        return kmStart;
    }




}
