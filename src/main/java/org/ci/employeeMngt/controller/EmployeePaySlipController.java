package org.ci.employeeMngt.controller;


import org.ci.employeeMngt.service.EmployeePaySlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payslip")
public class EmployeePaySlipController {

    @Autowired
    private EmployeePaySlipService paySlipService;

    @PostMapping("/generate")
    public void generatePaySlip(
            @RequestParam("empId") Long empId) {
        paySlipService.generatePaySlip(empId);
    }
}
