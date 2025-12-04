package com.example.demo.controller;
import org.springframework.ui.Model;


import com.example.demo.model.CustomerModel;
import com.example.demo.model.LeaseModel;
import com.example.demo.model.Vehicle;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.VehicleRepository;
import com.example.demo.service.LeaseService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

// TODO NEEDS CLEANUP
// TODO NEEDS CLEANUP
// TODO NEEDS CLEANUP
// TODO NEEDS CLEANUP

@Controller
public class LeaseController {

    private final LeaseService leaseService;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;

    public LeaseController(LeaseService leaseService,
                           CustomerRepository customerRepository,
                           VehicleRepository vehicleRepository) {
        this.leaseService = leaseService;
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping("/leaseContract")
    public String showCreateLeaseForm(
            @RequestParam(value = "customerId", required = false) Long customerId,
            @RequestParam(value = "vinId", required = false) String vinId,
            Model model) {

        var customers = customerRepository.findAll(Sort.by("firstName"));
        var vehicles  = vehicleRepository.findAll(Sort.by("registrationNo"));

        LeaseModel leaseForm = new LeaseModel();
        if (customerId != null) leaseForm.setCustomerId(customerId);
        if (vinId != null) leaseForm.setVinId(vinId);

        CustomerModel selectedCustomer = (customerId != null)
                ? customerRepository.findById(customerId).orElse(null)
                : null;

        Vehicle selectedVehicle = (vinId != null)
                ? vehicleRepository.findById(vinId).orElse(null)
                : null;

        model.addAttribute("customers", customers);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("leaseForm", leaseForm);
        model.addAttribute("selectedCustomer", selectedCustomer);
        model.addAttribute("selectedVehicle", selectedVehicle);

        return "pages/leaseContract";
    }

    @PostMapping("/leaseContract")
    public String createLease(@ModelAttribute("leaseForm") LeaseModel leaseForm) {

        // find the real customer and vehicle using the IDs from the form
        CustomerModel customer = customerRepository.findById(leaseForm.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Vehicle vehicle = vehicleRepository.findById(leaseForm.getVinId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        // attach them to the lease entity
        leaseForm.setCustomer(customer);
        leaseForm.setVehicle(vehicle);

        // now save the lease
        leaseService.save(leaseForm);

        // redirect back to the form (or to a list page if you want)
        return "redirect:/leaseContract";
    }



    @GetMapping("/current")
    public String showCurrentLeases(Model model) {
        var currentLeases = leaseService.getCurrentLeases();
        model.addAttribute("currentLeases", currentLeases);
        model.addAttribute("currentLeasesTotalPrice", leaseService.getCurrentLeasesTotalPrice());
        model.addAttribute("currentLeasesCount", currentLeases.size());
        return "pages/currentleasingcontracts";
    }



    // Create lease håndteres igennem leaseRequst da formen indeholder både data for lease og customer
    // som håndteres samtidig var det nødvendigt at lave en DTO for at undgå concurrency problemer.
//    @PostMapping
//    public String createLeaseAndAddToDB(@ModelAttribute LeaseRequest leaseRequest, Model model) {
//
//        try {
//            leaseService.createAndSaveLease(leaseRequest);
//            model.addAttribute("success", true);
//            model.addAttribute("leaseRequest", new LeaseRequest());
//        }catch (IllegalArgumentException e){
//            model.addAttribute("errorMessage", e.getMessage());
//            model.addAttribute("success", false);
//            //Hvis der indtastet forkert reg nr. slettes alt data i formen ikke
//            model.addAttribute("leaseRequest", leaseRequest);
//        } catch (Exception e) {
//            model.addAttribute("success", false);
//            model.addAttribute("errorMessage", "unexpected error");
//        }
//        return "pages/lease";
//    }


}
