package eu.europa.ec.eurostat.wihp.repository;

import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.web.rest.AcquisitionResourceIT;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
public class AcquisitionRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    public void shouldNotSave_whenCrawlerIsNull() {
        Acquisition acquisition = AcquisitionResourceIT.generateAcquisition(UUID.randomUUID());
        assertThrows(
            Exception.class,
            () -> {
                entityManager.persist(acquisition);
                entityManager.flush();
            }
        );
    }
}
