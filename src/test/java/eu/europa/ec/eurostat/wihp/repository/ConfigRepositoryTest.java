package eu.europa.ec.eurostat.wihp.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.domain.Config;
import eu.europa.ec.eurostat.wihp.web.rest.AcquisitionResourceIT;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
public class ConfigRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ConfigRepository configRepository;

    private static final String DEFAULT_NAME = "NAME";
    private static final String DEFAULT_FILE = "FILE";

    private static Config createConfig() {
        return new Config().name(DEFAULT_NAME).file(DEFAULT_FILE);
    }

    @Test
    @Transactional
    public void shouldNotSave_whenCrawlerIsNull() {
        Config config = createConfig();
        assertThrows(
            Exception.class,
            () -> {
                entityManager.persist(config);
                entityManager.flush();
            }
        );
    }

    @Test
    @Transactional
    public void shouldSave_whenAcquisitionIsNotNull() {
        int databaseSizeBeforeCreate = configRepository.findAll().size();
        Config config = createConfig();
        Acquisition acquisition = AcquisitionResourceIT.createEntity(entityManager);
        entityManager.persist(acquisition);
        entityManager.flush();
        config.setAcquisition(acquisition);
        configRepository.save(config);
        List<Config> configList = configRepository.findAll();
        assertThat(configList).hasSize(databaseSizeBeforeCreate + 1);
    }
}
