package com.example.demo.controller;

import com.example.demo.dto.LeaseRequest;
import com.example.demo.model.CustomerModel;
import com.example.demo.model.LeaseModel;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.LeaseRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/lease")
public class LeaseController {


    private final LeaseRepository leaseRepository;
    private final CustomerRepository customerRepository;



    public LeaseController(LeaseRepository leaseRepository, CustomerRepository customerRepository) {
        this.leaseRepository = leaseRepository;
        this.customerRepository = customerRepository;
    }


    @GetMapping
    public String showLeaseForm(Model model) {
        model.addAttribute("leaseRequest", new LeaseRequest());
        return "pages/lease";
    }

    @PostMapping
    public String CreateLeaseAndAddToDB(@ModelAttribute LeaseRequest leaseRequest, Model model) {

        try {

            CustomerModel customer = customerRepository.save(leaseRequest.getCustomer());

            LeaseModel lease = leaseRequest.getLease();
            lease.setCustomer(customer);

            leaseRepository.save(lease);

            model.addAttribute("lease", lease);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("success", false);
        }

        model.addAttribute("leaseRequest", new LeaseRequest());
        return "pages/lease";
    }




}
