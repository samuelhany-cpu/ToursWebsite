package com.naghamtours.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.naghamtours.entity.Booking;
import com.naghamtours.entity.Invoice;
import com.naghamtours.repository.InvoiceRepository;
import com.naghamtours.service.EmailService;
import com.naghamtours.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public byte[] generateInvoice(Booking booking) {
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter.getInstance(document, out);
            document.open();

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Tour Booking Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add booking details
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Add cells
            addCell(table, "Invoice Number", "INV-" + booking.getId());
            addCell(table, "Booking Date", booking.getBookingDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            addCell(table, "Tour Name", booking.getPackageEntity().getName());
            addCell(table, "Destination", booking.getPackageEntity().getDestination());
            addCell(table, "Number of Participants", String.valueOf(booking.getParticipants()));
            addCell(table, "Price per Person", "$" + booking.getPackageEntity().getPrice());
            addCell(table, "Total Amount", "$" + booking.getTotalAmount());

            document.add(table);

            // Add customer details
            Paragraph customerTitle = new Paragraph("Customer Details", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            customerTitle.setSpacingBefore(20);
            customerTitle.setSpacingAfter(10);
            document.add(customerTitle);

            PdfPTable customerTable = new PdfPTable(2);
            customerTable.setWidthPercentage(100);
            addCell(customerTable, "Name", booking.getClient().getFirstName() + " " + booking.getClient().getLastName());
            addCell(customerTable, "Email", booking.getClient().getClientEmail());
            addCell(customerTable, "Phone", booking.getClient().getPhone());

            document.add(customerTable);

            // Add footer
            Paragraph footer = new Paragraph("Thank you for choosing NaghamTours!", FontFactory.getFont(FontFactory.HELVETICA, 10));
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20);
            document.add(footer);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate invoice: " + e.getMessage(), e);
        }
    }

    private void addCell(PdfPTable table, String label, String value) {
        PdfPCell cell1 = new PdfPCell(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        PdfPCell cell2 = new PdfPCell(new Phrase(value));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        table.addCell(cell2);
    }

    @Override
    public void sendInvoiceEmail(Booking booking, byte[] invoicePdf) {
        String subject = "Your Tour Booking Invoice - " + booking.getPackageEntity().getName();
        String body = String.format("""
            Dear %s %s,
            
            Thank you for booking with NaghamTours! Please find attached your invoice for the following booking:
            
            Tour: %s
            Destination: %s
            Number of Participants: %d
            Total Amount: $%s
            
            If you have any questions, please don't hesitate to contact us.
            
            Best regards,
            NaghamTours Team
            """,
            booking.getClient().getFirstName(),
            booking.getClient().getLastName(),
            booking.getPackageEntity().getName(),
            booking.getPackageEntity().getDestination(),
            booking.getParticipants(),
            booking.getTotalAmount()
        );

        emailService.sendEmail(
            booking.getClient().getClientEmail(),
            subject,
            body,
            "invoice.pdf",
            invoicePdf
        );
    }

    @Override
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
    }

    @Override
    public byte[] generatePdfInvoice(Long id) {
        Invoice invoice = getInvoiceById(id);
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter.getInstance(document, out);
            document.open();

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Invoice #" + invoice.getId(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add invoice details
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            addCell(table, "Date", invoice.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            addCell(table, "Amount", "$" + invoice.getAmount());
            addCell(table, "Status", invoice.getStatus());
            addCell(table, "Description", invoice.getDescription());

            document.add(table);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF invoice: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Invoice> getInvoicesByClientId(Long clientId) {
        return invoiceRepository.findByClientId(clientId);
    }
} 