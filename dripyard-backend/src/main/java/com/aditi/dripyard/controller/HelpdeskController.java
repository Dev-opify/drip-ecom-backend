package com.aditi.dripyard.controller;

import com.aditi.dripyard.model.HelpdeskTicket;
import com.aditi.dripyard.service.HelpdeskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/helpdesk")
public class HelpdeskController {
    private final HelpdeskService helpdeskService;

    // Submit a new helpdesk/contact request
    @PostMapping("/submit")
    public ResponseEntity<HelpdeskTicket> submitTicket(@RequestBody HelpdeskTicket ticket) {
        HelpdeskTicket saved = helpdeskService.submitTicket(ticket);
        return ResponseEntity.ok(saved);
    }

    // Get all tickets (admin)
    @GetMapping("/tickets")
    public ResponseEntity<List<HelpdeskTicket>> getAllTickets() {
        return ResponseEntity.ok(helpdeskService.getAllTickets());
    }

    // Get a specific ticket (admin)
    @GetMapping("/tickets/{id}")
    public ResponseEntity<HelpdeskTicket> getTicket(@PathVariable Long id) {
        return ResponseEntity.ok(helpdeskService.getTicket(id));
    }
}
