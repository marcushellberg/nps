package app.vaadin.nps.service;

import app.vaadin.nps.model.TargetEmail;
import app.vaadin.nps.model.TargetList;
import app.vaadin.nps.repository.TargetListRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TargetListService {
    private final TargetListRepository targetListRepository;
    private final Logger log = LoggerFactory.getLogger(TargetListService.class);

    public TargetListService(TargetListRepository targetListRepository) {
        this.targetListRepository = targetListRepository;
    }

    @Transactional(readOnly = true)
    public List<TargetListDTO> findAll() {
        return targetListRepository.findAll().stream()
                .map(TargetListDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public TargetListDTO create(TargetListFormDTO formDTO, InputStream csvData) {
        TargetList targetList = formDTO.toEntity();
        Set<TargetEmail> emails = parseEmailsFromCsv(csvData, targetList);
        targetList.setEmails(emails);

        TargetList saved = targetListRepository.save(targetList);
        return TargetListDTO.fromEntity(saved);
    }

    public TargetListDTO update(Long id, TargetListFormDTO formDTO, InputStream csvData) {
        if (!id.equals(formDTO.id())) {
            throw new IllegalArgumentException("ID mismatch");
        }

        TargetList existingList = targetListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Target list not found"));

        existingList.setName(formDTO.name());

        // Replace all emails
        existingList.getEmails().clear();
        Set<TargetEmail> newEmails = parseEmailsFromCsv(csvData, existingList);
        existingList.getEmails().addAll(newEmails);

        TargetList saved = targetListRepository.save(existingList);
        return TargetListDTO.fromEntity(saved);
    }

    public void delete(Long id) {
        TargetList targetList = targetListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Target list not found"));

        if (!targetList.getSurveys().isEmpty()) {
            throw new IllegalStateException("Cannot delete target list that is being used by surveys");
        }

        targetListRepository.delete(targetList);
    }

    private Set<TargetEmail> parseEmailsFromCsv(InputStream csvData, TargetList targetList) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(csvData));
            return reader.lines()
                    .map(String::trim)
                    .filter(line -> line.contains("@"))
                    .map(email -> {
                        TargetEmail targetEmail = new TargetEmail();
                        targetEmail.setEmailAddress(email);
                        targetEmail.setTargetList(targetList);
                        return targetEmail;
                    })
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse CSV file: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Optional<TargetListDTO> findById(Long id) {
        return targetListRepository.findById(id)
                .map(TargetListDTO::fromEntity);
    }
}