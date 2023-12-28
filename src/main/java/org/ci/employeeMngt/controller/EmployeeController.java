package org.ci.employeeMngt.controller;

import org.ci.employeeMngt.entity.Employee;
import org.ci.employeeMngt.entity.EmployeeAttendance;

import org.ci.employeeMngt.entity.EmployeePaySlip;
import org.ci.employeeMngt.exception1.InspireException;
import org.ci.employeeMngt.repository.EmployeePaySlipRepository;
import org.ci.employeeMngt.serviceImpl.EmployeeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired
    private EmployeeServiceImpl employeeService;
    @Autowired
    private EmployeePaySlipRepository employeePaySlipRepository;


    //save employee with attandance
    @PostMapping("/save")
    public ResponseEntity<Employee> saveEmployee(@RequestBody Employee employee) {
        Employee employee1 = employeeService.createEmployee(employee);
        return new ResponseEntity<>(employee1, HttpStatus.CREATED);
    }

    //add employee with attendances
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeService.saveEmployeeWithAttendances(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    //add epmoyeeAttandance
    @PostMapping("/{empId}/attendances")
    public ResponseEntity<EmployeeAttendance> addEmployeeAttendance(
            @PathVariable Long empId,
            @RequestBody EmployeeAttendance employeeAttendance) {
        EmployeeAttendance addedAttendance = employeeService.addEmployeeAttendance(empId, employeeAttendance);
        return (addedAttendance != null) ?
                new ResponseEntity<>(addedAttendance, HttpStatus.CREATED) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //delete employee attandance using empId and attendanceId
    @DeleteMapping("/{empId}/attendances/{attendanceId}")
    public ResponseEntity<Void> removeEmployeeAttendance(
            @PathVariable Long empId,
            @PathVariable Long attendanceId) {
        employeeService.removeEmployeeAttendance(empId, attendanceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //generate employee payslip
    @PostMapping("/generate")
    public ResponseEntity<String> generatePaySlip(@RequestParam("empId") Long empId) {
        try {
            // Call the asynchronous method and get the CompletableFuture
            CompletableFuture<String> future = employeeService.generatePaySlip(empId);

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
            return new ResponseEntity<>("Failed to generate pay slip", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //method to generatePaySlipPdf
    @GetMapping(value = "/generate/{paySlipId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> generatePaySlipPdf(@PathVariable Long paySlipId) {
        try {
            ByteArrayInputStream bis = employeeService.generatePaySlipReport(paySlipId);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=payslip_report.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));
        } catch (InspireException e) {
            // Handle custom exception here
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Example: Return 404 for RESOURCE_NOT_FOUND
        } 
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

}
