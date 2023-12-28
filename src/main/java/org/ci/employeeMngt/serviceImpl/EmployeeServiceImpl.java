package org.ci.employeeMngt.serviceImpl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.ci.employeeMngt.entity.Employee;
import org.ci.employeeMngt.entity.EmployeeAttendance;
import org.ci.employeeMngt.entity.EmployeePaySlip;
import org.ci.employeeMngt.exception1.APIErrorCode;
import org.ci.employeeMngt.exception1.InspireException;
import org.ci.employeeMngt.repository.EmployeeAttendanceRepository;
import org.ci.employeeMngt.repository.EmployeePaySlipRepository;
import org.ci.employeeMngt.repository.EmployeeRepository;
import org.ci.employeeMngt.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeAttendanceRepository employeeAttendanceRepository;

    @Autowired
    private EmployeeAttendanceRepository attendanceRepository;


    @Autowired
    private EmployeePaySlipRepository paySlipRepository;

    //save employee method
    @Override
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    //method to get employee by Id
    @Override
    public Employee getEmployeeById(Long empId) {
        return employeeRepository.findById(empId).orElse(null);
    }

    //save employee with attandance
    public Employee saveEmployeeWithAttendances(Employee employee) {
        for (EmployeeAttendance attendance : employee.getEmployeeAttendances()) {
            attendance.setEmployee(employee);
        }
        return employeeRepository.save(employee);
    }

    //add employeeAttendance
    public EmployeeAttendance addEmployeeAttendance(Long empId, EmployeeAttendance employeeAttendance) {
        Employee employee = getEmployeeById(empId);
        if (employee != null) {

            employeeAttendance.setEmployee(employee);
            return employeeAttendanceRepository.save(employeeAttendance);
        }
        return null;
    }

    //remove employee attendance
    @Override
    public void removeEmployeeAttendance(Long empId, Long attendanceId) {
        Employee employee = getEmployeeById(empId);
        if (employee != null) {
            employee.getEmployeeAttendances().removeIf(attendance -> attendance.getEaId().equals(attendanceId));
        }
    }

    //generate payslip to employee
    @Override
    @Async
    @Transactional
    public CompletableFuture<String> generatePaySlip(Long empId) {
        try {
            long EMPLOYEE_DAILY_SALARY = 1000;

            // Check if a paySlip already exists for the employee
            if (paySlipRepository.existsByEmployeeEmpId(empId)) {
                throw new RuntimeException("Pay slip already generated for empId: " + empId);
            }

            // Fetch employee attendance records
            List<EmployeeAttendance> attendanceList = attendanceRepository.findByEmployee_EmpId(empId);

            if (attendanceList.isEmpty()) {
                throw new InspireException(APIErrorCode.RESOURCE_NOT_FOUND);
            }

            // Calculate total present days
            int totalPresentDays = 0;
            for (EmployeeAttendance attendance : attendanceList) {
                if ("Present".equalsIgnoreCase(attendance.getEaStatus())) {
                    totalPresentDays++;
                }
            }

            // Calculate total salary
            double totalSalary = totalPresentDays * EMPLOYEE_DAILY_SALARY;

            // Fetch the employee
            Employee employee = employeeRepository.findById(empId)
                    .orElseThrow(() -> new RuntimeException(""));

            // Save the paySlip record
            EmployeePaySlip paySlip = new EmployeePaySlip();
            paySlip.setEmployee(employee);
            paySlip.setEpDaysPresent(totalPresentDays);
            paySlip.setEpDailySalary(EMPLOYEE_DAILY_SALARY);
            paySlip.setEpTotalSalary(totalSalary);
            paySlip.setCreatedAt(LocalDate.now());
            paySlipRepository.save(paySlip);

            // Return a CompletableFuture with the payslip information as a String
            return CompletableFuture.completedFuture("Pay slip generated successfully for empId: " + empId);
        } catch (Exception e) {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    //scheduler to generate payslip
    @Scheduled(cron = "0 25 16 * * ?")
    public void generatePaySlips() {

        // Fetch all employee IDs
        List<Long> employeeIds = employeeAttendanceRepository.findAllEmployeeIds();

        // Generate payslip for each employee
        for (Long empId : employeeIds) {
            generatePaySlip(empId);

        }
    }


    /**
     * method to generatePaySlipPdf
     */

    public ByteArrayInputStream generatePaySlipReport(Long paySlipId) {
        try {
            Optional<EmployeePaySlip> paySlipOptional = paySlipRepository.findById(paySlipId);
            if (!paySlipOptional.isPresent()) {
                throw new InspireException(APIErrorCode.RESOURCE_NOT_FOUND);
            }

            EmployeePaySlip paySlip = paySlipOptional.get();
            return generatePaySlipPdf(paySlip);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate pay slip report", e);
        }
    }

    private ByteArrayInputStream generatePaySlipPdf(EmployeePaySlip paySlip) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
        Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

        PdfWriter.getInstance(document, out);
        document.open();

        // Add content to the PDF document
        addPaySlipContent(document, paySlip, titleFont, regularFont);

        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addPaySlipContent(Document document, EmployeePaySlip paySlip, Font titleFont, Font regularFont)
            throws DocumentException {
        // Title
        Paragraph title = new Paragraph("Pay Slip Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Add space
        document.add(Chunk.NEWLINE);

        // Employee details
        addParagraph(document, "Employee Name: " + paySlip.getEmployee().getEmpName(), regularFont);
        addParagraph(document, "Days Present: " + paySlip.getEpDaysPresent(), regularFont);
        addParagraph(document, "Daily Salary: " + paySlip.getEpDailySalary(), regularFont);
        addParagraph(document, "Total Salary: " + paySlip.getEpTotalSalary(), regularFont);

        // Add more fields as needed

        // Add space
        document.add(Chunk.NEWLINE);

        // Add a table for additional information (e.g., Created Date)
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        addCell(table, "Created Date", Element.ALIGN_LEFT, regularFont);
        addCell(table, paySlip.getCreatedAt().toString(), Element.ALIGN_RIGHT, regularFont);

        document.add(table);
    }

    private void addParagraph(Document document, String text, Font font) throws DocumentException {
        Paragraph paragraph = new Paragraph(text, font);
        document.add(paragraph);
    }

    private void addCell(PdfPTable table, String text, int alignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

}






