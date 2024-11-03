package app.vaadin.nps.repository;

import app.vaadin.nps.model.SurveyLink;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SurveyLinkRepository extends JpaRepository<SurveyLink, Long> {
    Optional<SurveyLink> findByToken(String token);

    @Query("SELECT sl FROM SurveyLink sl WHERE sl.token = :token")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SurveyLink> findByTokenWithPessimisticLock(@Param("token") String token);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
        "FROM SurveyLink sl " +
        "LEFT JOIN sl.response r " +
        "WHERE sl.token = :token")
    boolean hasResponse(@Param("token") String token);

}