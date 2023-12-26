package org.ci.employeeMngt.controller;

import org.ci.employeeMngt.entity.Employee;
import org.ci.employeeMngt.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @PostMapping("/save")
    public ResponseEntity<Employee> saveEmployee(@RequestBody Employee employee){
        Employee employee1 = employeeService.createEmployee(employee);
        return new ResponseEntity<>(employee1,HttpStatus.CREATED);
    }
}
