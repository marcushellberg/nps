package app.vaadin.nps.service;

import app.vaadin.nps.model.Response;
import app.vaadin.nps.model.SurveyLink;
import app.vaadin.nps.repository.ResponseRepository;
import app.vaadin.nps.repository.SurveyLinkRepository;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@BrowserCallable
@AnonymousAllowed
@Service
@Transactional
public class ResponseService {
    private final SurveyLinkRepository surveyLinkRepository;
    private final ResponseRepository responseRepository;
    private final Logger log = LoggerFactory.getLogger(ResponseService.class);

    public ResponseService(SurveyLinkRepository surveyLinkRepository,
                           ResponseRepository responseRepository) {
        this.surveyLinkRepository = surveyLinkRepository;
        this.responseRepository = responseRepository;
    }

    /**
     * Gets the survey question for a given response token.
     *
     * @return the survey question and whether it's already been answered
     * @throws EntityNotFoundException if token is invalid
     */
    @Transactional(readOnly = true)
    public SurveyQuestionDTO getSurveyQuestion(String token) {
        SurveyLink surveyLink = surveyLinkRepository.findByToken(token)
            .orElseThrow(() -> new EntityNotFoundException("Invalid survey link"));

        return SurveyQuestionDTO.fromSurveyLink(surveyLink);
    }

    /**
     * Saves a new survey response.
     *
     * @throws EntityNotFoundException  if token is invalid
     * @throws IllegalStateException    if survey has already been answered
     * @throws IllegalArgumentException if score is invalid
     */
    public void saveResponse(ResponseDTO responseDTO) {
        if (responseDTO.score() < 0 || responseDTO.score() > 10) {
            throw new IllegalArgumentException("Score must be between 0 and 10");
        }

        SurveyLink surveyLink = surveyLinkRepository.findByToken(responseDTO.token())
            .orElseThrow(() -> new EntityNotFoundException("Invalid survey link"));

        // Check if already answered using pessimistic locking to prevent race conditions
        surveyLinkRepository.findByTokenWithPessimisticLock(responseDTO.token())
            .ifPresent(link -> {
                if (link.getResponse() != null) {
                    throw new IllegalStateException("Survey has already been answered");
                }
            });

        Response response = new Response();
        response.setSurveyLink(surveyLink);
        response.setScore(responseDTO.score());
        response.setSubmissionDate(LocalDateTime.now());

        responseRepository.save(response);
        log.info("Saved response for survey link: {}", surveyLink.getToken());
    }
}