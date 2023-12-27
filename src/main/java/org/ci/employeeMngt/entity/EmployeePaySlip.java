package org.ci.employeeMngt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Month;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "EMPLOYEE_PAYSLIP_TABLE")
public class EmployeePaySlip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EP_ID")
    private Long epId;

    @Column(name = "EP_PRESENT_DAYS")
    private int epDaysPresent;

    @Column(name = "CREATED_DATE")
    private LocalDate CreatedAt;

    @Column(name = "EP_DIALY_SALARY")
    private double epDailySalary;

    @Column(name = "EP_TOTAL_SALARY")
    private double epTotalSalary;

    @OneToOne
    @JoinColumn(name = "EMP_ID", referencedColumnName = "EMP_ID")
    private Employee employee;


}
