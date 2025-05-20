package com.naghamtours.controller;

import com.naghamtours.entity.Booking;
import com.naghamtours.entity.Client;
import com.naghamtours.entity.Package;
import com.naghamtours.entity.Invoice;
import com.naghamtours.service.BookingService;
import com.naghamtours.service.InvoiceService;
import com.naghamtours.service.PackageService;
import com.naghamtours.service.SecurityService;
import com.naghamtours.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client")
@PreAuthorize("hasRole('CLIENT')")
public class ClientController {
    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private PackageService packageService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/tours")
    public String viewActiveTours(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sort,
            Model model) {
        
        List<Package> tours = packageService.getActivePackages();

        // Apply search filter
        if (search != null && !search.isEmpty()) {
            String searchLower = search.toLowerCase();
            tours = tours.stream()
                    .filter(tour -> tour.getName().toLowerCase().contains(searchLower) ||
                            tour.getDescription().toLowerCase().contains(searchLower) ||
                            tour.getDestination().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }

        // Apply location filter
        if (location != null && !location.isEmpty()) {
            tours = tours.stream()
                    .filter(tour -> tour.getDestination().equalsIgnoreCase(location))
                    .collect(Collectors.toList());
        }

        // Apply sorting
        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "price-asc":
                    tours.sort((t1, t2) -> t1.getPrice().compareTo(t2.getPrice()));
                    break;
                case "price-desc":
                    tours.sort((t1, t2) -> t2.getPrice().compareTo(t1.getPrice()));
                    break;
                case "duration-asc":
                    tours.sort((t1, t2) -> t1.getDuration().compareTo(t2.getDuration()));
                    break;
                case "duration-desc":
                    tours.sort((t1, t2) -> t2.getDuration().compareTo(t1.getDuration()));
                    break;
                case "date-asc":
                    tours.sort((t1, t2) -> t1.getStartDate().compareTo(t2.getStartDate()));
                    break;
                case "date-desc":
                    tours.sort((t1, t2) -> t2.getStartDate().compareTo(t1.getStartDate()));
                    break;
            }
        }

        // Get unique locations for the filter dropdown
        List<String> locations = tours.stream()
                .map(Package::getDestination)
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("tours", tours);
        model.addAttribute("locations", locations);
        model.addAttribute("currentSort", sort);
        return "client/tours";
    }

    @GetMapping("/tours/{id}")
    public String viewTourDetails(@PathVariable Long id, Model model) {
        Package tour = packageService.getPackageById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        model.addAttribute("tour", tour);
        return "client/tour-details";
    }

    @PostMapping("/tours/{id}/book")
    public String bookTour(@PathVariable Long id, @RequestParam Integer participants, RedirectAttributes redirectAttributes) {
        try {
            Client client = securityService.getCurrentClient();
            if (client == null) {
                throw new RuntimeException("Client not found");
            }

            Package tour = packageService.getPackageById(id)
                    .orElseThrow(() -> new RuntimeException("Tour not found"));

            if (!bookingService.isBookingPossible(id, participants)) {
                throw new RuntimeException("Number of participants exceeds maximum allowed");
            }

            Booking booking = new Booking();
            booking.setClient(client);
            booking.setPackageEntity(tour);
            booking.setBookingDate(LocalDateTime.now());
            booking.setParticipants(participants);
            booking.setStatus(Booking.BookingStatus.PENDING);

            // Calculate total amount
            Double packagePrice = tour.getPrice();
            if (packagePrice == null || participants == null) {
                throw new IllegalArgumentException("Package price and number of participants must not be null");
            }
            booking.setTotalAmount(java.math.BigDecimal.valueOf(packagePrice * participants));

            // Save the booking first
            booking = bookingService.createBooking(booking);
            
            // Redirect to payment page
            return "redirect:/client/payment/" + booking.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to book tour: " + e.getMessage());
            return "redirect:/client/tours/" + id;
        }
    }

    @GetMapping("/bookings")
    public String viewMyBookings(Model model, HttpServletRequest request) {
        Client client = securityService.getCurrentClient();
        if (client == null) {
            return "redirect:/login";
        }
        List<Booking> bookings = bookingService.getBookingsByUserId(client.getId());
        model.addAttribute("bookings", bookings);
        
        // Add CSRF token
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        if (token != null) {
            model.addAttribute("_csrf", token);
        }
        
        return "client/bookings";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Client client = securityService.getCurrentClient();
            if (client == null) {
                throw new RuntimeException("Client not found");
            }

            Booking booking = bookingService.getBookingById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            // Verify that the booking belongs to the current client
            if (!booking.getClient().getId().equals(client.getId())) {
                throw new RuntimeException("You are not authorized to cancel this booking");
            }

            // Check if the booking can be cancelled
            if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
                throw new RuntimeException("This booking is already cancelled");
            }
            if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
                throw new RuntimeException("Cannot cancel a completed booking");
            }

            bookingService.cancelBooking(id);
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel booking: " + e.getMessage());
        }
        return "redirect:/client/bookings";
    }

    @GetMapping("/payment/{bookingId}")
    public String showPaymentPage(@PathVariable Long bookingId, Model model) {
        try {
            Booking booking = bookingService.getBookingById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
            
            // Verify that the booking belongs to the current client
            Client currentClient = securityService.getCurrentClient();
            if (!booking.getClient().getId().equals(currentClient.getId())) {
                throw new RuntimeException("You are not authorized to view this booking");
            }
            
            // Verify that the booking is in PENDING status
            if (booking.getStatus() != Booking.BookingStatus.PENDING) {
                throw new RuntimeException("This booking cannot be paid for. Current status: " + booking.getStatus());
            }
            
            model.addAttribute("booking", booking);
            return "client/payment";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/client/bookings";
        }
    }

    @PostMapping("/process-payment")
    public String processPayment(@RequestParam Long bookingId, @RequestParam String paymentMethod, Model model) {
        try {
            Booking booking = bookingService.getBookingById(bookingId).orElseThrow(() -> 
                new RuntimeException("Booking not found"));

            // Set payment method
            booking.setPaymentMethod(Booking.PaymentMethod.valueOf(paymentMethod));
            
            if (paymentMethod.equals("VISA")) {
                // For visa payments, redirect to payment gateway
                return "redirect:/client/payment/visa/" + bookingId;
            } else {
                // For cash payments, set status to pending and redirect to confirmation page
                booking.setPaymentStatus(Booking.PaymentStatus.PENDING);
                bookingService.updateBookingStatus(bookingId, booking.getStatus());
                
                model.addAttribute("booking", booking);
                model.addAttribute("message", "Your booking has been received. Please visit our office to complete the payment.");
                return "client/payment-confirmation";
            }
        } catch (Exception e) {
            logger.error("Error processing payment: ", e);
            model.addAttribute("error", "Error processing payment. Please try again.");
            return "error";
        }
    }

    @GetMapping("/payment/visa/{bookingId}")
    public String visaPaymentPage(@PathVariable Long bookingId, Model model, HttpServletRequest request) {
        try {
            Booking booking = bookingService.getBookingById(bookingId).orElseThrow(() -> 
                new RuntimeException("Booking not found"));

            // Add CSRF token
            CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
            if (token != null) {
                model.addAttribute("_csrf", token);
            }

            model.addAttribute("booking", booking);
            return "client/visa-payment";
        } catch (Exception e) {
            logger.error("Error loading visa payment page: ", e);
            model.addAttribute("error", "Error loading payment page. Please try again.");
            return "error";
        }
    }

    @PostMapping("/payment/visa/process")
    public String processVisaPayment(@RequestParam Long bookingId, RedirectAttributes redirectAttributes) {
        try {
            // Get the booking with all required entities loaded
            Booking booking = bookingService.getBookingById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            // Create and save invoice
            Invoice invoice = new Invoice();
            invoice.setBooking(booking);
            invoice.setClient(booking.getClient());
            invoice.setAmount(booking.getTotalAmount());
            invoice.setDate(LocalDateTime.now());
            invoice.setStatus("PAID");
            invoice.setDescription("Payment for booking #" + booking.getId());
            invoice = invoiceService.saveInvoice(invoice);

            // Update booking with invoice
            booking.setInvoice(invoice);
            booking.setPaymentStatus(Booking.PaymentStatus.PAID);
            booking.setStatus(Booking.BookingStatus.CONFIRMED);
            bookingService.updateBookingStatus(bookingId, Booking.BookingStatus.CONFIRMED);

            // Generate PDF invoice
            byte[] invoicePdf = invoiceService.generateInvoice(booking);
            if (invoicePdf != null) {
                // Send confirmation email with invoice
                invoiceService.sendInvoiceEmail(booking, invoicePdf);
            }

            redirectAttributes.addFlashAttribute("success", "Payment processed successfully! A confirmation email has been sent.");
            return "redirect:/client/bookings";
        } catch (Exception e) {
            logger.error("Error processing payment: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error processing payment: " + e.getMessage());
            return "redirect:/client/payment/visa/" + bookingId;
        }
    }
} 