package com.naghamtours.service;

import com.naghamtours.entity.Booking;
import com.naghamtours.entity.Invoice;
import java.util.List;

public interface InvoiceService {
    byte[] generateInvoice(Booking booking);
    void sendInvoiceEmail(Booking booking, byte[] invoicePdf);
    Invoice getInvoiceById(Long id);
    byte[] generatePdfInvoice(Long id);
    List<Invoice> getInvoicesByClientId(Long clientId);
    Invoice saveInvoice(Invoice invoice);
} 