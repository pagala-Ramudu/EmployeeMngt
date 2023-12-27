package org.ci.employeeMngt.repository;

import org.ci.employeeMngt.entity.EmployeePaySlip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeePaySlipRepository extends JpaRepository<EmployeePaySlip, Long> {
    boolean existsByEmployeeEmpId(Long empId);


}