package org.ci.employeeMngt.service;

import org.ci.employeeMngt.entity.Employee;
import org.ci.employeeMngt.entity.EmployeeAttendance;

import java.io.ByteArrayInputStream;
import java.util.concurrent.CompletableFuture;

public interface EmployeeService {
    CompletableFuture<String> generatePaySlip(Long empId, String calculationType);


    public Employee saveEmployeeWithAttendances(Employee employee);


    public EmployeeAttendance addEmployeeAttendance(Long empId, EmployeeAttendance employeeAttendance);

    public void removeEmployeeAttendance(Long empId, Long attendanceId);

    public Employee createEmployee(Employee employee);

    public Employee getEmployeeById(Long empId);

    //public CompletableFuture<String> generatePaySlip(Long empId);

    // method to generatePaySlipPdf
    public ByteArrayInputStream generatePaySlipReport(Long paySlipId);

    // public void generatePaySlips();

}