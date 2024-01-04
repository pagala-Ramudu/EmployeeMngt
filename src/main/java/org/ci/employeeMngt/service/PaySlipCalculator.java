package org.ci.employeeMngt.service;

import org.ci.employeeMngt.entity.Employee;
import org.ci.employeeMngt.entity.EmployeeAttendance;

import java.util.List;

public interface PaySlipCalculator {


    String calculatePaySlip(Long empId);


}