package org.ci.employeeMngt.controller;


import org.ci.employeeMngt.service.EmployeePaySlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/payslip")
public class EmployeePaySlipController {

    @Autowired
    private EmployeePaySlipService paySlipService;

    @PostMapping("/generate")
    public ResponseEntity<String> generatePaySlip(@RequestParam("empId") Long empId) {
        try {
            // Call the asynchronous method and get the CompletableFuture
            CompletableFuture<String> future = paySlipService.generatePaySlip(empId);

            // Block and get the result when the future completes
            String result = future.get();

            // Check if the future completed exceptionally
            if (future.isCompletedExceptionally()) {
                // Handle exception if needed
                return new ResponseEntity<>("Failed to generate pay slip", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Return success response with the pay slip information
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (InterruptedException | ExecutionException e) {
            // Handle exceptions if needed
            return new ResponseEntity<>("Failed to generate pay slip", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
