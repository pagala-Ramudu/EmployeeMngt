package org.ci.employeeMngt.controller;

import org.ci.employeeMngt.entity.Employee;
import org.ci.employeeMngt.entity.EmployeeAttendance;

import org.ci.employeeMngt.serviceImpl.EmployeeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired
    private EmployeeServiceImpl employeeService;


    //save employee with attandance
    @PostMapping("/save")
    public ResponseEntity<Employee> saveEmployee(@RequestBody Employee employee) {
        Employee employee1 = employeeService.createEmployee(employee);
        return new ResponseEntity<>(employee1, HttpStatus.CREATED);
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

}
