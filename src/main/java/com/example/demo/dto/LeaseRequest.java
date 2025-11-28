package com.example.demo.dto;

import com.example.demo.model.CustomerModel;
import com.example.demo.model.LeaseModel;



public class LeaseRequest {

    private CustomerModel customer;
    private LeaseModel lease;

    public void setCustomer(CustomerModel customer) {
        this.customer = customer;
    }

    public void setLease(LeaseModel lease) {
        this.lease = lease;
    }

    public LeaseModel getLease() {
        return lease;
    }

    public CustomerModel getCustomer() {
        return customer;
    }
}
