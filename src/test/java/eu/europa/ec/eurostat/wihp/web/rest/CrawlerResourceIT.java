package eu.europa.ec.eurostat.wihp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.ParserFilter;
import eu.europa.ec.eurostat.wihp.domain.Source;
import eu.europa.ec.eurostat.wihp.repository.CrawlerRepository;
import eu.europa.ec.eurostat.wihp.service.validation.ParserFilterValidatorTest;
import eu.europa.ec.eurostat.wihp.util.JsonModelUtils;
import eu.europa.ec.eurostat.wihp.util.JsonNodeUtils;
import eu.europa.ec.eurostat.wihp.web.rest.model.CopyCrawlerRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CrawlerResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class CrawlerResourceIT {

    private static final String DEFAULT_NAME = "Ho1__";
    private static final String UPDATED_NAME = "Xm0__";
    private static final String COPIED_NAME = "Copied-Crawler-Name";
    private static final String INVALID_NAME = "Invalid-Name-$$$";

    public static final Integer DEFAULT_FETCH_INTERVAL = -1;
    private static final Integer UPDATED_FETCH_INTERVAL = 0;

    private static final Integer DEFAULT_FETCH_INTERVAL_WHEN_ERROR = -1;
    private static final Integer UPDATED_FETCH_INTERVAL_WHEN_ERROR = 0;

    private static final Integer DEFAULT_FETCH_INTERVAL_WHEN_FETCH_ERROR = -1;
    private static final Integer UPDATED_FETCH_INTERVAL_WHEN_FETCH_ERROR = 0;

    private static final Boolean DEFAULT_EXTRACTOR_NO_TEXT = false;
    private static final Boolean UPDATED_EXTRACTOR_NO_TEXT = true;

    private static final String DEFAULT_EXTRACTOR_TEXT_INCLUDE_PATTERN = "AAAAAAAAAA";
    private static final String UPDATED_EXTRACTOR_TEXT_INCLUDE_PATTERN = "BBBBBBBBBB";

    private static final String DEFAULT_EXTRACTOR_TEXT_EXCLUDE_TAGS = "AAAAAAAAAA";
    private static final String UPDATED_EXTRACTOR_TEXT_EXCLUDE_TAGS = "BBBBBBBBBB";

    private static final Integer DEFAULT_HTTP_CONTENT_LIMIT = -1;
    private static final Integer UPDATED_HTTP_CONTENT_LIMIT = 0;

    private static final Boolean DEFAULT_EMIT_OUT_LINKS = false;
    private static final Boolean UPDATED_EMIT_OUT_LINKS = true;

    private static final Boolean DEFAULT_DYNAMIC = false;
    private static final Boolean UPDATED_DYNAMIC = true;

    private static final Integer DEFAULT_MAX_EMIT_OUT_LINKS_PER_PAGE = -1;
    private static final Integer UPDATED_MAX_EMIT_OUT_LINKS_PER_PAGE = 0;

    private static final String ENTITY_API_URL = "/api/crawlers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_API_URL_ID_COPY = ENTITY_API_URL + "/%d/copy";
    private static final String IMPORT_API_URL = ENTITY_API_URL + "/import";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CrawlerRepository crawlerRepository;

    @Mock
    private CrawlerRepository crawlerRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCrawlerMockMvc;

    private Crawler crawler;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Crawler createEntity(EntityManager em) {
        Crawler crawler = createCrawlerEntity();
        // Add required entity
        Source source;
        if (TestUtil.findAll(em, Source.class).isEmpty()) {
            source = SourceResourceIT.createEntity(em);
            em.persist(source);
            em.flush();
        } else {
            source = TestUtil.findAll(em, Source.class).get(0);
        }
        crawler.getSources().add(source);
        return crawler;
    }

    public static Crawler createCrawlerEntity() {
        return new Crawler()
            .name(DEFAULT_NAME)
            .fetchInterval(DEFAULT_FETCH_INTERVAL)
            .fetchIntervalWhenError(DEFAULT_FETCH_INTERVAL_WHEN_ERROR)
            .fetchIntervalWhenFetchError(DEFAULT_FETCH_INTERVAL_WHEN_FETCH_ERROR)
            .extractorNoText(DEFAULT_EXTRACTOR_NO_TEXT)
            .extractorTextIncludePattern(DEFAULT_EXTRACTOR_TEXT_INCLUDE_PATTERN)
            .extractorTextExcludeTags(DEFAULT_EXTRACTOR_TEXT_EXCLUDE_TAGS)
            .httpContentLimit(DEFAULT_HTTP_CONTENT_LIMIT)
            .emitOutLinks(DEFAULT_EMIT_OUT_LINKS)
            .dynamic(DEFAULT_DYNAMIC)
            .maxEmitOutLinksPerPage(DEFAULT_MAX_EMIT_OUT_LINKS_PER_PAGE);
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Crawler createUpdatedEntity(EntityManager em) {
        Crawler crawler = new Crawler()
            .name(UPDATED_NAME)
            .fetchInterval(UPDATED_FETCH_INTERVAL)
            .fetchIntervalWhenError(UPDATED_FETCH_INTERVAL_WHEN_ERROR)
            .fetchIntervalWhenFetchError(UPDATED_FETCH_INTERVAL_WHEN_FETCH_ERROR)
            .extractorNoText(UPDATED_EXTRACTOR_NO_TEXT)
            .extractorTextIncludePattern(UPDATED_EXTRACTOR_TEXT_INCLUDE_PATTERN)
            .extractorTextExcludeTags(UPDATED_EXTRACTOR_TEXT_EXCLUDE_TAGS)
            .httpContentLimit(UPDATED_HTTP_CONTENT_LIMIT)
            .emitOutLinks(UPDATED_EMIT_OUT_LINKS)
            .dynamic(UPDATED_EMIT_OUT_LINKS)
            .maxEmitOutLinksPerPage(UPDATED_MAX_EMIT_OUT_LINKS_PER_PAGE);
        // Add required entity
        Source source;
        if (TestUtil.findAll(em, Source.class).isEmpty()) {
            source = SourceResourceIT.createUpdatedEntity(em);
            em.persist(source);
            em.flush();
        } else {
            source = TestUtil.findAll(em, Source.class).get(0);
        }
        crawler.getSources().add(source);
        return crawler;
    }

    @BeforeEach
    public void initTest() {
        crawler = createEntity(em);
    }

    @Test
    @Transactional
    void createCrawler() throws Exception {
        int databaseSizeBeforeCreate = crawlerRepository.findAll().size();
        // Create the Crawler
        restCrawlerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
                    .headers(getAuthorizationHeader())
            )
            .andExpect(status().isCreated());

        // Validate the Crawler in the database
        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeCreate + 1);
        Crawler testCrawler = crawlerList.get(crawlerList.size() - 1);
        assertThat(testCrawler.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCrawler.getFetchInterval()).isEqualTo(DEFAULT_FETCH_INTERVAL);
        assertThat(testCrawler.getFetchIntervalWhenError()).isEqualTo(DEFAULT_FETCH_INTERVAL_WHEN_ERROR);
        assertThat(testCrawler.getFetchIntervalWhenFetchError()).isEqualTo(DEFAULT_FETCH_INTERVAL_WHEN_FETCH_ERROR);
        assertThat(testCrawler.getExtractorNoText()).isEqualTo(DEFAULT_EXTRACTOR_NO_TEXT);
        assertThat(testCrawler.getExtractorTextIncludePattern()).isEqualTo(DEFAULT_EXTRACTOR_TEXT_INCLUDE_PATTERN);
        assertThat(testCrawler.getExtractorTextExcludeTags()).isEqualTo(DEFAULT_EXTRACTOR_TEXT_EXCLUDE_TAGS);
        assertThat(testCrawler.getHttpContentLimit()).isEqualTo(DEFAULT_HTTP_CONTENT_LIMIT);
        assertThat(testCrawler.getEmitOutLinks()).isEqualTo(DEFAULT_EMIT_OUT_LINKS);
        assertThat(testCrawler.getDynamic()).isEqualTo(DEFAULT_DYNAMIC);
        assertThat(testCrawler.getMaxEmitOutLinksPerPage()).isEqualTo(DEFAULT_MAX_EMIT_OUT_LINKS_PER_PAGE);
    }

    @Test
    @Transactional
    void shallReturnUnprocessableEntity_whenAddingParserFilters() throws Exception {
        ParserFilter validRegexURLFilter = JsonNodeUtils.getObject(ParserFilterValidatorTest.VALID_REGEX_URL_FILTER, ParserFilter.class);
        crawler.getParserFilters().add(validRegexURLFilter);
        // Create the Crawler
        restCrawlerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
                    .headers(getAuthorizationHeader())
            )
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Transactional
    void shallReturnUnprocessableEntity_whenRegexURLFilterIsNotValid() throws Exception {
        ParserFilter invalidRegexURLFilter = JsonNodeUtils.getObject(
            ParserFilterValidatorTest.INVALID_REGEX_URL_FILTER,
            ParserFilter.class
        );
        crawler.getParserFilters().add(invalidRegexURLFilter);
        // Create the Crawler
        restCrawlerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Transactional
    void createCrawlerWithExistingId() throws Exception {
        // Create the Crawler with an existing ID
        crawler.setId(1L);

        int databaseSizeBeforeCreate = crawlerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCrawlerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isBadRequest());

        // Validate the Crawler in the database
        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = crawlerRepository.findAll().size();
        // set the field null
        crawler.setName(null);

        // Create the Crawler, which fails.

        restCrawlerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isUnprocessableEntity());

        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFetchIntervalIsRequired() throws Exception {
        int databaseSizeBeforeTest = crawlerRepository.findAll().size();
        // set the field null
        crawler.setFetchInterval(null);

        // Create the Crawler, which fails.

        restCrawlerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isUnprocessableEntity());

        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFetchIntervalWhenErrorIsRequired() throws Exception {
        int databaseSizeBeforeTest = crawlerRepository.findAll().size();
        // set the field null
        crawler.setFetchIntervalWhenError(null);

        // Create the Crawler, which fails.

        restCrawlerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isUnprocessableEntity());

        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFetchIntervalWhenFetchErrorIsRequired() throws Exception {
        int databaseSizeBeforeTest = crawlerRepository.findAll().size();
        // set the field null
        crawler.setFetchIntervalWhenFetchError(null);

        // Create the Crawler, which fails.

        restCrawlerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isUnprocessableEntity());

        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExtractorNoTextIsRequired() throws Exception {
        int databaseSizeBeforeTest = crawlerRepository.findAll().size();
        // set the field null
        crawler.setExtractorNoText(null);

        // Create the Crawler, which fails.

        restCrawlerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isUnprocessableEntity());

        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHttpContentLimitIsRequired() throws Exception {
        int databaseSizeBeforeTest = crawlerRepository.findAll().size();
        // set the field null
        crawler.setHttpContentLimit(null);

        // Create the Crawler, which fails.

        restCrawlerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isUnprocessableEntity());

        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmitOutLinksIsRequired() throws Exception {
        int databaseSizeBeforeTest = crawlerRepository.findAll().size();
        // set the field null
        crawler.setEmitOutLinks(null);

        // Create the Crawler, which fails.

        restCrawlerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isUnprocessableEntity());

        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDynamicIsRequired() throws Exception {
        int databaseSizeBeforeTest = crawlerRepository.findAll().size();
        // set the field null
        crawler.setDynamic(null);

        // Create the Crawler, which fails.

        restCrawlerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isUnprocessableEntity());

        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMaxEmitOutLinksPerPageIsRequired() throws Exception {
        int databaseSizeBeforeTest = crawlerRepository.findAll().size();
        // set the field null
        crawler.setMaxEmitOutLinksPerPage(null);

        // Create the Crawler, which fails.

        restCrawlerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isUnprocessableEntity());

        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCrawlers() throws Exception {
        // Initialize the database
        crawlerRepository.saveAndFlush(crawler);

        // Get all the crawlerList
        restCrawlerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(crawler.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].fetchInterval").value(hasItem(DEFAULT_FETCH_INTERVAL)))
            .andExpect(jsonPath("$.[*].fetchIntervalWhenError").value(hasItem(DEFAULT_FETCH_INTERVAL_WHEN_ERROR)))
            .andExpect(jsonPath("$.[*].fetchIntervalWhenFetchError").value(hasItem(DEFAULT_FETCH_INTERVAL_WHEN_FETCH_ERROR)))
            .andExpect(jsonPath("$.[*].extractorNoText").value(hasItem(DEFAULT_EXTRACTOR_NO_TEXT.booleanValue())))
            .andExpect(jsonPath("$.[*].extractorTextIncludePattern").value(hasItem(DEFAULT_EXTRACTOR_TEXT_INCLUDE_PATTERN)))
            .andExpect(jsonPath("$.[*].extractorTextExcludeTags").value(hasItem(DEFAULT_EXTRACTOR_TEXT_EXCLUDE_TAGS)))
            .andExpect(jsonPath("$.[*].httpContentLimit").value(hasItem(DEFAULT_HTTP_CONTENT_LIMIT)))
            .andExpect(jsonPath("$.[*].emitOutLinks").value(hasItem(DEFAULT_EMIT_OUT_LINKS.booleanValue())))
            .andExpect(jsonPath("$.[*].dynamic").value(hasItem(DEFAULT_DYNAMIC.booleanValue())))
            .andExpect(jsonPath("$.[*].maxEmitOutLinksPerPage").value(hasItem(DEFAULT_MAX_EMIT_OUT_LINKS_PER_PAGE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCrawlersWithEagerRelationshipsIsEnabled() throws Exception {
        when(crawlerRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCrawlerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(crawlerRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCrawlersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(crawlerRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCrawlerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(crawlerRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getCrawler() throws Exception {
        // Initialize the database
        crawlerRepository.saveAndFlush(crawler);

        // Get the crawler
        restCrawlerMockMvc
            .perform(get(ENTITY_API_URL_ID, crawler.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(crawler.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.fetchInterval").value(DEFAULT_FETCH_INTERVAL))
            .andExpect(jsonPath("$.fetchIntervalWhenError").value(DEFAULT_FETCH_INTERVAL_WHEN_ERROR))
            .andExpect(jsonPath("$.fetchIntervalWhenFetchError").value(DEFAULT_FETCH_INTERVAL_WHEN_FETCH_ERROR))
            .andExpect(jsonPath("$.extractorNoText").value(DEFAULT_EXTRACTOR_NO_TEXT.booleanValue()))
            .andExpect(jsonPath("$.extractorTextIncludePattern").value(DEFAULT_EXTRACTOR_TEXT_INCLUDE_PATTERN))
            .andExpect(jsonPath("$.extractorTextExcludeTags").value(DEFAULT_EXTRACTOR_TEXT_EXCLUDE_TAGS))
            .andExpect(jsonPath("$.httpContentLimit").value(DEFAULT_HTTP_CONTENT_LIMIT))
            .andExpect(jsonPath("$.emitOutLinks").value(DEFAULT_EMIT_OUT_LINKS.booleanValue()))
            .andExpect(jsonPath("$.dynamic").value(DEFAULT_DYNAMIC.booleanValue()))
            .andExpect(jsonPath("$.maxEmitOutLinksPerPage").value(DEFAULT_MAX_EMIT_OUT_LINKS_PER_PAGE));
    }

    @Test
    @Transactional
    void getNonExistingCrawler() throws Exception {
        // Get the crawler
        restCrawlerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putEmptySourceCrawler_thenSourceTableNotUpdated() throws Exception {
        // Initialize the database
        crawlerRepository.saveAndFlush(crawler);

        int databaseSizeBeforeUpdate = crawlerRepository.findAll().size();

        // Update the crawler
        Crawler updatedCrawler = crawlerRepository.findById(crawler.getId()).get();
        Set<Source> oldSources = updatedCrawler.getSources();
        // Disconnect from session so that the updates on updatedCrawler are not directly saved in db
        em.detach(updatedCrawler);
        updatedCrawler
            .name(UPDATED_NAME)
            .fetchInterval(UPDATED_FETCH_INTERVAL)
            .fetchIntervalWhenError(UPDATED_FETCH_INTERVAL_WHEN_ERROR)
            .fetchIntervalWhenFetchError(UPDATED_FETCH_INTERVAL_WHEN_FETCH_ERROR)
            .extractorNoText(UPDATED_EXTRACTOR_NO_TEXT)
            .extractorTextIncludePattern(UPDATED_EXTRACTOR_TEXT_INCLUDE_PATTERN)
            .extractorTextExcludeTags(UPDATED_EXTRACTOR_TEXT_EXCLUDE_TAGS)
            .httpContentLimit(UPDATED_HTTP_CONTENT_LIMIT)
            .emitOutLinks(UPDATED_EMIT_OUT_LINKS)
            .dynamic(UPDATED_DYNAMIC)
            .maxEmitOutLinksPerPage(UPDATED_MAX_EMIT_OUT_LINKS_PER_PAGE);

        updatedCrawler.getSources().removeAll(oldSources);

        restCrawlerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCrawler.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCrawler))
                    .headers(getAuthorizationHeader())
            )
            .andExpect(status().isOk());

        // Validate the Crawler in the database
        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeUpdate);
        Crawler testCrawler = crawlerList.get(crawlerList.size() - 1);
        assertThat(testCrawler.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCrawler.getFetchInterval()).isEqualTo(UPDATED_FETCH_INTERVAL);
        assertThat(testCrawler.getFetchIntervalWhenError()).isEqualTo(UPDATED_FETCH_INTERVAL_WHEN_ERROR);
        assertThat(testCrawler.getFetchIntervalWhenFetchError()).isEqualTo(UPDATED_FETCH_INTERVAL_WHEN_FETCH_ERROR);
        assertThat(testCrawler.getExtractorNoText()).isEqualTo(UPDATED_EXTRACTOR_NO_TEXT);
        assertThat(testCrawler.getExtractorTextIncludePattern()).isEqualTo(UPDATED_EXTRACTOR_TEXT_INCLUDE_PATTERN);
        assertThat(testCrawler.getExtractorTextExcludeTags()).isEqualTo(UPDATED_EXTRACTOR_TEXT_EXCLUDE_TAGS);
        assertThat(testCrawler.getHttpContentLimit()).isEqualTo(UPDATED_HTTP_CONTENT_LIMIT);
        assertThat(testCrawler.getEmitOutLinks()).isEqualTo(UPDATED_EMIT_OUT_LINKS);
        assertThat(testCrawler.getDynamic()).isEqualTo(UPDATED_DYNAMIC);
        assertThat(testCrawler.getMaxEmitOutLinksPerPage()).isEqualTo(UPDATED_MAX_EMIT_OUT_LINKS_PER_PAGE);
        // finally, we do the main check
        Assertions.assertEquals(1, testCrawler.getSources().size());
    }

    @Test
    @Transactional
    void putNewCrawler() throws Exception {
        // Initialize the database
        crawlerRepository.saveAndFlush(crawler);

        int databaseSizeBeforeUpdate = crawlerRepository.findAll().size();

        // Update the crawler
        Crawler updatedCrawler = crawlerRepository.findById(crawler.getId()).get();
        // Disconnect from session so that the updates on updatedCrawler are not directly saved in db
        em.detach(updatedCrawler);
        updatedCrawler
            .name(UPDATED_NAME)
            .fetchInterval(UPDATED_FETCH_INTERVAL)
            .fetchIntervalWhenError(UPDATED_FETCH_INTERVAL_WHEN_ERROR)
            .fetchIntervalWhenFetchError(UPDATED_FETCH_INTERVAL_WHEN_FETCH_ERROR)
            .extractorNoText(UPDATED_EXTRACTOR_NO_TEXT)
            .extractorTextIncludePattern(UPDATED_EXTRACTOR_TEXT_INCLUDE_PATTERN)
            .extractorTextExcludeTags(UPDATED_EXTRACTOR_TEXT_EXCLUDE_TAGS)
            .httpContentLimit(UPDATED_HTTP_CONTENT_LIMIT)
            .emitOutLinks(UPDATED_EMIT_OUT_LINKS)
            .dynamic(UPDATED_DYNAMIC)
            .maxEmitOutLinksPerPage(UPDATED_MAX_EMIT_OUT_LINKS_PER_PAGE);

        restCrawlerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCrawler.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCrawler))
                    .headers(getAuthorizationHeader())
            )
            .andExpect(status().isOk());

        // Validate the Crawler in the database
        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeUpdate);
        Crawler testCrawler = crawlerList.get(crawlerList.size() - 1);
        assertThat(testCrawler.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCrawler.getFetchInterval()).isEqualTo(UPDATED_FETCH_INTERVAL);
        assertThat(testCrawler.getFetchIntervalWhenError()).isEqualTo(UPDATED_FETCH_INTERVAL_WHEN_ERROR);
        assertThat(testCrawler.getFetchIntervalWhenFetchError()).isEqualTo(UPDATED_FETCH_INTERVAL_WHEN_FETCH_ERROR);
        assertThat(testCrawler.getExtractorNoText()).isEqualTo(UPDATED_EXTRACTOR_NO_TEXT);
        assertThat(testCrawler.getExtractorTextIncludePattern()).isEqualTo(UPDATED_EXTRACTOR_TEXT_INCLUDE_PATTERN);
        assertThat(testCrawler.getExtractorTextExcludeTags()).isEqualTo(UPDATED_EXTRACTOR_TEXT_EXCLUDE_TAGS);
        assertThat(testCrawler.getHttpContentLimit()).isEqualTo(UPDATED_HTTP_CONTENT_LIMIT);
        assertThat(testCrawler.getEmitOutLinks()).isEqualTo(UPDATED_EMIT_OUT_LINKS);
        assertThat(testCrawler.getDynamic()).isEqualTo(UPDATED_DYNAMIC);
        assertThat(testCrawler.getMaxEmitOutLinksPerPage()).isEqualTo(UPDATED_MAX_EMIT_OUT_LINKS_PER_PAGE);
    }

    @Test
    @Transactional
    void putNonExistingCrawler() throws Exception {
        int databaseSizeBeforeUpdate = crawlerRepository.findAll().size();
        crawler.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCrawlerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, crawler.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isBadRequest());

        // Validate the Crawler in the database
        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCrawler() throws Exception {
        int databaseSizeBeforeUpdate = crawlerRepository.findAll().size();
        crawler.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCrawlerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isBadRequest());

        // Validate the Crawler in the database
        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCrawler() throws Exception {
        int databaseSizeBeforeUpdate = crawlerRepository.findAll().size();
        crawler.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCrawlerMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Crawler in the database
        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCrawlerWithPatch() throws Exception {
        // Initialize the database
        crawlerRepository.saveAndFlush(crawler);

        int databaseSizeBeforeUpdate = crawlerRepository.findAll().size();

        // Update the crawler using partial update
        Crawler partialUpdatedCrawler = new Crawler();
        partialUpdatedCrawler.setId(crawler.getId());

        partialUpdatedCrawler
            .name(UPDATED_NAME)
            .fetchIntervalWhenError(UPDATED_FETCH_INTERVAL_WHEN_ERROR)
            .fetchIntervalWhenFetchError(UPDATED_FETCH_INTERVAL_WHEN_FETCH_ERROR)
            .httpContentLimit(UPDATED_HTTP_CONTENT_LIMIT);

        restCrawlerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCrawler.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCrawler))
                    .headers(getAuthorizationHeader())
            )
            .andExpect(status().isOk());

        // Validate the Crawler in the database
        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeUpdate);
        Crawler testCrawler = crawlerList.get(crawlerList.size() - 1);
        assertThat(testCrawler.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCrawler.getFetchInterval()).isEqualTo(DEFAULT_FETCH_INTERVAL);
        assertThat(testCrawler.getFetchIntervalWhenError()).isEqualTo(UPDATED_FETCH_INTERVAL_WHEN_ERROR);
        assertThat(testCrawler.getFetchIntervalWhenFetchError()).isEqualTo(UPDATED_FETCH_INTERVAL_WHEN_FETCH_ERROR);
        assertThat(testCrawler.getExtractorNoText()).isEqualTo(DEFAULT_EXTRACTOR_NO_TEXT);
        assertThat(testCrawler.getExtractorTextIncludePattern()).isEqualTo(DEFAULT_EXTRACTOR_TEXT_INCLUDE_PATTERN);
        assertThat(testCrawler.getExtractorTextExcludeTags()).isEqualTo(DEFAULT_EXTRACTOR_TEXT_EXCLUDE_TAGS);
        assertThat(testCrawler.getHttpContentLimit()).isEqualTo(UPDATED_HTTP_CONTENT_LIMIT);
        assertThat(testCrawler.getEmitOutLinks()).isEqualTo(DEFAULT_EMIT_OUT_LINKS);
        assertThat(testCrawler.getDynamic()).isEqualTo(DEFAULT_DYNAMIC);
        assertThat(testCrawler.getMaxEmitOutLinksPerPage()).isEqualTo(DEFAULT_MAX_EMIT_OUT_LINKS_PER_PAGE);
    }

    @Test
    @Transactional
    void fullUpdateCrawlerWithPatch() throws Exception {
        // Initialize the database
        crawlerRepository.saveAndFlush(crawler);

        int databaseSizeBeforeUpdate = crawlerRepository.findAll().size();

        // Update the crawler using partial update
        Crawler partialUpdatedCrawler = createPartialUpdatedCrawler();
        partialUpdatedCrawler.setId(crawler.getId());
        partialUpdatedCrawler.sources(crawler.getSources());

        restCrawlerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCrawler.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCrawler))
                    .headers(getAuthorizationHeader())
            )
            .andExpect(status().isOk());

        // Validate the Crawler in the database
        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeUpdate);
        Crawler testCrawler = crawlerList.get(crawlerList.size() - 1);
        assertThat(testCrawler.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCrawler.getFetchInterval()).isEqualTo(UPDATED_FETCH_INTERVAL);
        assertThat(testCrawler.getFetchIntervalWhenError()).isEqualTo(UPDATED_FETCH_INTERVAL_WHEN_ERROR);
        assertThat(testCrawler.getFetchIntervalWhenFetchError()).isEqualTo(UPDATED_FETCH_INTERVAL_WHEN_FETCH_ERROR);
        assertThat(testCrawler.getExtractorNoText()).isEqualTo(UPDATED_EXTRACTOR_NO_TEXT);
        assertThat(testCrawler.getExtractorTextIncludePattern()).isEqualTo(UPDATED_EXTRACTOR_TEXT_INCLUDE_PATTERN);
        assertThat(testCrawler.getExtractorTextExcludeTags()).isEqualTo(UPDATED_EXTRACTOR_TEXT_EXCLUDE_TAGS);
        assertThat(testCrawler.getHttpContentLimit()).isEqualTo(UPDATED_HTTP_CONTENT_LIMIT);
        assertThat(testCrawler.getEmitOutLinks()).isEqualTo(UPDATED_EMIT_OUT_LINKS);
        assertThat(testCrawler.getDynamic()).isEqualTo(UPDATED_DYNAMIC);
        assertThat(testCrawler.getMaxEmitOutLinksPerPage()).isEqualTo(UPDATED_MAX_EMIT_OUT_LINKS_PER_PAGE);
    }

    @Test
    @Transactional
    void patchNonExistingCrawler() throws Exception {
        int databaseSizeBeforeUpdate = crawlerRepository.findAll().size();
        crawler.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCrawlerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, crawler.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isBadRequest());

        // Validate the Crawler in the database
        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCrawler() throws Exception {
        int databaseSizeBeforeUpdate = crawlerRepository.findAll().size();
        crawler.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCrawlerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isBadRequest());

        // Validate the Crawler in the database
        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCrawler() throws Exception {
        int databaseSizeBeforeUpdate = crawlerRepository.findAll().size();
        crawler.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCrawlerMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Crawler in the database
        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCrawler() throws Exception {
        // Initialize the database
        crawlerRepository.saveAndFlush(crawler);

        int databaseSizeBeforeDelete = crawlerRepository.findAll().size();

        // Delete the crawler
        restCrawlerMockMvc
            .perform(delete(ENTITY_API_URL_ID, crawler.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void whenExtractorTextIncludePatternIsLarge_thenCrawlerIsValid() throws Exception {
        int databaseSizeBeforeCreate = crawlerRepository.findAll().size();
        // set pattern with a value larger than 255
        crawler.setExtractorTextIncludePattern("a".repeat(1000));
        // Create the Crawler
        restCrawlerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
                    .headers(getAuthorizationHeader())
            )
            .andExpect(status().isCreated());

        // Validate the Crawler in the database
        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    public void whenExtractorTextExcludeTagsIsLarge_thenCrawlerIsValid() throws Exception {
        int databaseSizeBeforeCreate = crawlerRepository.findAll().size();
        // set tag with a value larger than 255
        crawler.setExtractorTextExcludeTags("a".repeat(1000));
        // Create the Crawler
        restCrawlerMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(crawler))
                    .headers(getAuthorizationHeader())
            )
            .andExpect(status().isCreated());

        // Validate the Crawler in the database
        List<Crawler> crawlerList = crawlerRepository.findAll();
        assertThat(crawlerList).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    public void whenCrawlerIsUpdated_thenSourceRemainsUnModified() throws Exception {
        // Initialize the database
        crawlerRepository.saveAndFlush(crawler);

        // create a copy of the source before update
        Source source = crawler.getSources().stream().findFirst().get();
        Source sourceBefore = new Source().id(source.getId()).name(source.getName()).url(source.getUrl());

        // Update the crawler using partial update
        Crawler partialUpdatedCrawler = createPartialUpdatedCrawler();
        partialUpdatedCrawler.setId(crawler.getId());
        partialUpdatedCrawler.addSource(source);

        restCrawlerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCrawler.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCrawler))
                    .headers(getAuthorizationHeader())
            )
            .andExpect(status().isOk());

        // Validate the Crawler in the database
        List<Crawler> crawlerList = crawlerRepository.findAll();
        Crawler testCrawler = crawlerList.get(crawlerList.size() - 1);
        Source sourceAfter = testCrawler.getSources().stream().findFirst().get();

        assertThat(sourceAfter.getName()).isEqualTo(sourceBefore.getName());
        assertThat(sourceAfter.getUrl()).isEqualTo(sourceBefore.getUrl());
    }

    @Test
    @Transactional
    public void whenCrawlerIsDeleted_thenSourceRemainsUnModified() throws Exception {
        // initialize the database
        crawlerRepository.saveAndFlush(crawler);

        // create a copy of the source before deleting the associated crawler
        Source source = crawler.getSources().stream().findFirst().get();
        Source sourceBefore = new Source().id(source.getId()).name(source.getName()).url(source.getUrl());
        sourceBefore.setId(source.getId());

        // delete the crawler
        restCrawlerMockMvc
            .perform(delete(ENTITY_API_URL_ID, crawler.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // retrieve source to verify is has not changed
        Source sourceAfter = TestUtil.findAll(em, Source.class).get(0);
        assertThat(sourceAfter.getName()).isEqualTo(sourceBefore.getName());
        assertThat(sourceAfter.getUrl()).isEqualTo(sourceBefore.getUrl());
    }

    private Crawler createPartialUpdatedCrawler() {
        Crawler partialUpdatedCrawler = new Crawler();
        partialUpdatedCrawler
            .name(UPDATED_NAME)
            .fetchInterval(UPDATED_FETCH_INTERVAL)
            .fetchIntervalWhenError(UPDATED_FETCH_INTERVAL_WHEN_ERROR)
            .fetchIntervalWhenFetchError(UPDATED_FETCH_INTERVAL_WHEN_FETCH_ERROR)
            .extractorNoText(UPDATED_EXTRACTOR_NO_TEXT)
            .extractorTextIncludePattern(UPDATED_EXTRACTOR_TEXT_INCLUDE_PATTERN)
            .extractorTextExcludeTags(UPDATED_EXTRACTOR_TEXT_EXCLUDE_TAGS)
            .httpContentLimit(UPDATED_HTTP_CONTENT_LIMIT)
            .emitOutLinks(UPDATED_EMIT_OUT_LINKS)
            .dynamic(UPDATED_DYNAMIC)
            .maxEmitOutLinksPerPage(UPDATED_MAX_EMIT_OUT_LINKS_PER_PAGE);
        return partialUpdatedCrawler;
    }

    @Test
    @Transactional
    void copyCrawler() throws Exception {
        // initialize the database
        Crawler original = crawlerRepository.saveAndFlush(crawler);

        // copy crawler
        CopyCrawlerRequest request = new CopyCrawlerRequest();
        request.setName(COPIED_NAME);

        String url = String.format(ENTITY_API_URL_ID_COPY, original.getId());
        restCrawlerMockMvc
            .perform(post(url).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(request)))
            .andExpect(status().isCreated());

        // Validate the Crawler in the database

        List<Crawler> crawlerList = crawlerRepository.findAll();

        Crawler copy = crawlerList.get(crawlerList.size() - 1);
        assertThat(copy.getName()).isEqualTo(COPIED_NAME);
        assertThat(copy.getFetchInterval()).isEqualTo(original.getFetchInterval());
        assertThat(copy.getFetchIntervalWhenError()).isEqualTo(original.getFetchIntervalWhenError());
        assertThat(copy.getFetchIntervalWhenFetchError()).isEqualTo(original.getFetchIntervalWhenFetchError());
        assertThat(copy.getExtractorNoText()).isEqualTo(original.getExtractorNoText());
        assertThat(copy.getExtractorTextIncludePattern()).isEqualTo(original.getExtractorTextIncludePattern());
        assertThat(copy.getExtractorTextExcludeTags()).isEqualTo(original.getExtractorTextExcludeTags());
        assertThat(copy.getHttpContentLimit()).isEqualTo(original.getHttpContentLimit());
        assertThat(copy.getEmitOutLinks()).isEqualTo(original.getEmitOutLinks());
        assertThat(copy.getMaxEmitOutLinksPerPage()).isEqualTo(original.getMaxEmitOutLinksPerPage());
        assertThat(copy.getDynamic()).isEqualTo(original.getDynamic());
        assertThat(copy.getSources().size()).isEqualTo(original.getSources().size());
        assertThat(copy.getParserFilters().size()).isEqualTo(original.getParserFilters().size());
    }

    @Test
    @Transactional
    void copyCrawlerWithInValidName_thenNewCopyIsNotProcess() throws Exception {
        // initialize the database
        Crawler original = crawlerRepository.saveAndFlush(crawler);

        // request copy with invalid name
        CopyCrawlerRequest request = new CopyCrawlerRequest();
        request.setName(INVALID_NAME);

        String url = String.format(ENTITY_API_URL_ID_COPY, original.getId());
        restCrawlerMockMvc
            .perform(post(url).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(request)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Transactional
    void importCrawler_withoutViolation() throws Exception {
        final int sourcesSize = 100;
        MockMultipartFile file = JsonModelUtils.getMockMultipartFileForCrawler("fileToUpload0", sourcesSize, false);
        restCrawlerMockMvc.perform(multipart(IMPORT_API_URL).file(file)).andExpect(status().isCreated());
    }

    private HttpHeaders getAuthorizationHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "token");
        return headers;
    }
}
