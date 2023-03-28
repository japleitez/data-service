package eu.europa.ec.eurostat.wihp.repository;

import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Acquisition entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AcquisitionRepository extends JpaRepository<Acquisition, Long> {
    @Query("select acquisition from Acquisition acquisition  where acquisition.workflowId =:workflowId")
    Optional<Acquisition> findOneByWorkflowId(@Param("workflowId") java.util.UUID workflowId);

    @Query("select acquisition from Acquisition acquisition  where acquisition.id =:id")
    Optional<Acquisition> findOne(@Param("id") Long id);
}
