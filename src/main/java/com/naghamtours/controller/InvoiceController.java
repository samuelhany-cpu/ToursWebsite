package com.naghamtours.controller;

import com.naghamtours.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {
        model.addAttribute("invoice", invoiceService.getInvoiceById(id));
        return "invoice/view";
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> downloadInvoice(@PathVariable Long id) {
        byte[] pdfBytes = invoiceService.generatePdfInvoice(id);
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=invoice_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(resource);
    }

    @GetMapping("/client/{clientId}")
    public String viewClientInvoices(@PathVariable Long clientId, Model model) {
        model.addAttribute("invoices", invoiceService.getInvoicesByClientId(clientId));
        return "invoice/list";
    }
} 