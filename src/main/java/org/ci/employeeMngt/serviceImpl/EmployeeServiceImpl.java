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
import org.ci.employeeMngt.service.PaySlipCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeePaySlipRepository paySlipRepository;

    @Autowired
    private PaySlipCalculator dailyPaySlipCalculator;

    @Autowired
    private PaySlipCalculator monthlyPaySlipCalculator;

    @Autowired
    private EmployeeAttendanceRepository employeeAttendanceRepository;


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
    @Override
    public Employee saveEmployeeWithAttendances(Employee employee) {
        for (EmployeeAttendance attendance : employee.getEmployeeAttendances()) {
            attendance.setEmployee(employee);
        }
        return employeeRepository.save(employee);
    }

    //add employeeAttendance
    @Override
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

    /**
     * method to generatePaySlipPdf
     */

    @Override
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

//    /*
//     * scheduler to generate payslip
//     */
//
//    @Override
//    @Scheduled(cron = "0 39 12 * * ?")
//    public void generatePaySlips() {
//
//        // Fetch all employee IDs
//        List<Long> employeeIds = employeeAttendanceRepository.findAllEmployeeIds();
//
//        // Generate payslip for each employee
//        for (Long empId : employeeIds) {
//            generatePaySlip(empId);
//        }
//    }

    //generatePaySlip monthly or dialy
    @Override
    public CompletableFuture<String> generatePaySlip(Long empId, String calculationType) {
        try {
            switch (calculationType.toLowerCase()) {
                case "daily":
                    return CompletableFuture.completedFuture(dailyPaySlipCalculator.calculatePaySlip(empId));
                case "monthly":
                    return CompletableFuture.completedFuture(monthlyPaySlipCalculator.calculatePaySlip(empId));
                default:
                    throw new RuntimeException("Invalid calculation type");
            }
        } catch (Exception e) {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }
}
