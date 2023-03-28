package eu.europa.ec.eurostat.wihp.repository;

import eu.europa.ec.eurostat.wihp.domain.Config;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Config entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConfigRepository extends JpaRepository<Config, Long> {}
