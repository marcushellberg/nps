package app.vaadin.nps.service;

import app.vaadin.nps.model.Survey;
import app.vaadin.nps.model.SurveyLink;
import app.vaadin.nps.model.TargetList;
import app.vaadin.nps.repository.SurveyLinkRepository;
import app.vaadin.nps.repository.SurveyRepository;
import app.vaadin.nps.repository.TargetListRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SurveyService {

    @Value("${application.baseUrl}")
    private String baseUrl;
    private final SurveyRepository surveyRepository;
    private final TargetListRepository targetListRepository;
    private final SurveyLinkRepository surveyLinkRepository;
    private final JavaMailSender emailSender;
    private final Logger log = LoggerFactory.getLogger(SurveyService.class);

    public SurveyService(SurveyRepository surveyRepository,
                         TargetListRepository targetListRepository,
                         SurveyLinkRepository surveyLinkRepository,
                         JavaMailSender emailSender) {
        this.surveyRepository = surveyRepository;
        this.targetListRepository = targetListRepository;
        this.surveyLinkRepository = surveyLinkRepository;
        this.emailSender = emailSender;
    }

    @Transactional(readOnly = true)
    public List<SurveyDTO> findAll() {
        return surveyRepository.findAll().stream()
            .map(SurveyDTO::fromEntity)
            .toList();
    }

    public SurveyDTO createSurvey(SurveyFormDTO newSurveyDTO) {
        Survey survey = newSurveyDTO.toEntity();

        TargetList targetList = targetListRepository.findById(newSurveyDTO.targetListId())
            .orElseThrow(() -> new EntityNotFoundException("Target list not found"));
        survey.setTargetList(targetList);

        Survey saved = surveyRepository.save(survey);
        return SurveyDTO.fromEntity(saved);
    }

    public void deleteSurvey(Long id) {
        Survey survey = surveyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Survey not found"));

        if (survey.getSendDate() != null) {
            throw new IllegalStateException("Cannot delete a survey that has been sent");
        }

        surveyRepository.delete(survey);
    }

    public SurveyDTO updateSurvey(Long id, SurveyFormDTO surveyDTO) {

        Survey existingSurvey = surveyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Survey not found"));

        if (existingSurvey.getSendDate() != null) {
            throw new IllegalStateException("Cannot update a survey that has been sent");
        }

        existingSurvey.setName(surveyDTO.name());
        existingSurvey.setQuestion(surveyDTO.question());
        existingSurvey.setEmailSubject(surveyDTO.emailSubject());
        existingSurvey.setEmailBody(surveyDTO.emailBody());

        Survey saved = surveyRepository.save(existingSurvey);
        return SurveyDTO.fromEntity(saved);
    }

    public SurveyDTO sendSurvey(Long id) {
        Survey survey = surveyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Survey not found"));

        if (survey.getSendDate() != null) {
            throw new IllegalStateException("Survey has already been sent");
        }

        if (survey.getTargetList() == null) {
            throw new IllegalStateException("Survey has no target list");
        }

        List<SurveyLink> surveyLinks = survey.getTargetList().getEmails().stream()
            .map(targetEmail -> {
                SurveyLink link = new SurveyLink();
                link.setSurvey(survey);
                link.setEmailAddress(targetEmail.getEmailAddress());
                return surveyLinkRepository.save(link);
            })
            .toList();

        surveyLinks.parallelStream().forEach(link -> {
            try {
                sendSurveyEmail(survey, link);
            } catch (Exception e) {
                log.error("Failed to send survey email to: " + link.getEmailAddress(), e);
            }
        });

        survey.setSendDate(LocalDateTime.now());
        survey.setTotalSent(surveyLinks.size());
        Survey savedSurvey = surveyRepository.save(survey);

        return SurveyDTO.fromEntity(savedSurvey);
    }

    private void sendSurveyEmail(Survey survey, SurveyLink link) {
        String surveyUrl = baseUrl + "/a/" + link.getToken();
        String emailTemplate = survey.getEmailBody();
        String emailBody = emailTemplate.contains("$URL") ? emailTemplate.replace("$URL", surveyUrl) : emailTemplate + "\n\n" + surveyUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(link.getEmailAddress());
        message.setSubject(survey.getEmailSubject());
        message.setText(emailBody);

        emailSender.send(message);
    }

    @Transactional(readOnly = true)
    public Optional<SurveyDTO> findById(Long id) {
        return surveyRepository.findById(id)
            .map(SurveyDTO::fromEntity);
    }
}