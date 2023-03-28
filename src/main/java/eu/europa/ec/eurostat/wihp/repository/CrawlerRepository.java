package eu.europa.ec.eurostat.wihp.repository;

import eu.europa.ec.eurostat.wihp.domain.Crawler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the Crawler entity.
 */
@Repository
public interface CrawlerRepository extends JpaRepository<Crawler, Long> {
    @Query(
        value = "select distinct crawler from Crawler crawler left join fetch crawler.sources",
        countQuery = "select count(distinct crawler) from Crawler crawler"
    )
    Page<Crawler> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct crawler from Crawler crawler left join fetch crawler.sources")
    List<Crawler> findAllWithEagerRelationships();

    @Query("select crawler from Crawler crawler left join fetch crawler.sources where crawler.id =:id")
    Optional<Crawler> findOneWithEagerRelationships(@Param("id") Long id);

    @Query("select crawler from Crawler crawler left join fetch crawler.sources where crawler.name =:name")
    Optional<Crawler> findOneWithEagerRelationships(@Param("name") String name);

    Optional<Crawler> findOneByName(@Param("name") String name);
}

