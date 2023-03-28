package eu.europa.ec.eurostat.wihp.repository;

import eu.europa.ec.eurostat.wihp.domain.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ActionLogs entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {}
