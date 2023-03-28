package eu.europa.ec.eurostat.wihp.repository;

import eu.europa.ec.eurostat.wihp.domain.ParserFilter;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ParserFilter entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParserFilterRepository extends JpaRepository<ParserFilter, Long> {}
