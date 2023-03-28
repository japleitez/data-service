package eu.europa.ec.eurostat.wihp.repository;

import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.Source;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the Source entity.
 */

@Repository
public interface SourceRepository extends JpaRepository<Source, Long> {

    List<Source> findSourceByNameIn(@Param("names")List<String> inventoryIdList);

    Optional<Source> findSourceById(@Param("id")Long id);

    Page<Source> findSourcesByCrawlers(@Param("crawlers") Crawler crawler, Pageable pageable );

    boolean existsSourceByCrawlers(@Param("crawlers")Crawler crawler);
}
