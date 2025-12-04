package com.example.demo;
/*
import com.example.demo.DTO.LeaseRequest;
import com.example.demo.model.Vehicle;
import com.example.demo.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// TODO NEEDS CLEANUP
// TODO NEEDS CLEANUP
// TODO NEEDS CLEANUP
// TODO NEEDS CLEANUP

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CreateLeaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {

        Vehicle vehicle = new Vehicle();
        vehicle.setRegistrationNo("AB12345");
        vehicle.setBrand("Toyota");
        vehicle.setModel("Camry");

        vehicleRepository.save(vehicle);
    }

    @Test
    void createLease_RightRegistration() throws Exception {
        mockMvc.perform(post("/lease")
                        .param("vehicle.registrationNo", "AB12345")
                        .param("customer.name", "John Doe")
                        .param("lease.startDate", "2025-01-01")
                        .param("lease.endDate", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/lease"))
                .andExpect(model().attribute("success", true))
                .andExpect(model().attributeExists("leaseRequest"));
    }

    @Test
    void createLease_WrongRegistration() throws Exception {
        mockMvc.perform(post("/lease")
                        .param("vehicle.registrationNo", "WRONG123")
                        .param("customer.name", "Jane Doe")
                        .param("lease.startDate", "2025-01-01")
                        .param("lease.endDate", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/lease"))
                .andExpect(model().attribute("success", false))
                .andExpect(model().attribute("errorMessage", "Vehicle not found"))
                .andExpect(model().attributeExists("leaseRequest"));
    }
}
*/