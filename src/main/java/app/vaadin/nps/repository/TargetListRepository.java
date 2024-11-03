package app.vaadin.nps.repository;

import app.vaadin.nps.model.TargetList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TargetListRepository extends JpaRepository<TargetList, Long> {

}