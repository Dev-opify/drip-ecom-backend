package com.aditi.dripyard.service;

import com.aditi.dripyard.model.HelpdeskTicket;
import com.aditi.dripyard.repository.HelpdeskTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HelpdeskService {
    private final HelpdeskTicketRepository repository;

    public HelpdeskTicket submitTicket(HelpdeskTicket ticket) {
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setStatus("OPEN");
        return repository.save(ticket);
    }

    public List<HelpdeskTicket> getAllTickets() {
        return repository.findAll();
    }

    public HelpdeskTicket getTicket(Long id) {
        return repository.findById(id).orElse(null);
    }
}
