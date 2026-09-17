package com.prismo.backend.service;

import com.prismo.backend.dto.InquiryRequest;
import com.prismo.backend.model.*;
import com.prismo.backend.repository.InquiryRepository;
import com.prismo.backend.repository.ProjectRepository;
import com.prismo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public Inquiry createInquiry(InquiryRequest request) {
        Inquiry inquiry = Inquiry.builder()
                .customerName(request.getCustomerName())
                .email(request.getCustomerEmail())
                .phone(request.getCustomerPhone())
                .projectType(request.getProjectType())
                .location(request.getLocation())
                .description(request.getInitialNotes())
                .status(InquiryStatus.NEW)
                .build();
                
        // Auto-assign to a Project Manager, or CEO if no PM is available
        List<User> pms = userRepository.findByRole(Role.PROJECT_MANAGER);
        if (pms != null && !pms.isEmpty()) {
            inquiry.setAssignedTo(pms.get(0));
        } else {
            List<User> ceos = userRepository.findByRole(Role.CEO);
            if (ceos != null && !ceos.isEmpty()) {
                inquiry.setAssignedTo(ceos.get(0));
            }
        }

        return inquiryRepository.save(inquiry);
    }

    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }

    public List<Inquiry> getInquiriesByCustomerEmail(String email) {
        return inquiryRepository.findByEmail(email);
    }

    public Inquiry getInquiryById(Long id) {
        return inquiryRepository.findById(id).orElseThrow(() -> new RuntimeException("Inquiry not found"));
    }

    public Inquiry updateInquiry(Long id, InquiryRequest request) {
        Inquiry inquiry = getInquiryById(id);
        
        if (request.getStatus() != null) {
            inquiry.setStatus(InquiryStatus.valueOf(request.getStatus()));
        }
        
        // TODO: Refactor to use new Proposal and ConsultationNote entities
        
        return inquiryRepository.save(inquiry);
    }

    @Transactional
    public Inquiry acceptProposal(Long id) {
        Inquiry inquiry = getInquiryById(id);
        inquiry.setStatus(InquiryStatus.ACCEPTED);
        
        // TODO: Refactor conversion logic
        return inquiryRepository.save(inquiry);
    }
}
