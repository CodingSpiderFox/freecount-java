package org.codingspiderfox.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codingspiderfox.web.rest.TestUtil.sameInstant;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.codingspiderfox.IntegrationTest;
import org.codingspiderfox.domain.Product;
import org.codingspiderfox.domain.Stock;
import org.codingspiderfox.repository.StockRepository;
import org.codingspiderfox.repository.search.StockSearchRepository;
import org.codingspiderfox.service.criteria.StockCriteria;
import org.codingspiderfox.service.dto.StockDTO;
import org.codingspiderfox.service.mapper.StockMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link StockResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockResourceIT {

    private static final ZonedDateTime DEFAULT_ADDED_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ADDED_TIMESTAMP = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_ADDED_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_STORAGE_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_STORAGE_LOCATION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CALCULATED_EXPIRY_TIMESTAMP = ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(0L),
        ZoneOffset.UTC
    );
    private static final ZonedDateTime UPDATED_CALCULATED_EXPIRY_TIMESTAMP = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CALCULATED_EXPIRY_TIMESTAMP = ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(-1L),
        ZoneOffset.UTC
    );

    private static final ZonedDateTime DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP = ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(0L),
        ZoneOffset.UTC
    );
    private static final ZonedDateTime UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_MANUAL_SET_EXPIRY_TIMESTAMP = ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(-1L),
        ZoneOffset.UTC
    );

    private static final String ENTITY_API_URL = "/api/stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/stocks";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockMapper stockMapper;

    /**
     * This repository is mocked in the org.codingspiderfox.repository.search test package.
     *
     * @see org.codingspiderfox.repository.search.StockSearchRepositoryMockConfiguration
     */
    @Autowired
    private StockSearchRepository mockStockSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockMockMvc;

    private Stock stock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stock createEntity(EntityManager em) {
        Stock stock = new Stock()
            .addedTimestamp(DEFAULT_ADDED_TIMESTAMP)
            .storageLocation(DEFAULT_STORAGE_LOCATION)
            .calculatedExpiryTimestamp(DEFAULT_CALCULATED_EXPIRY_TIMESTAMP)
            .manualSetExpiryTimestamp(DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        stock.setProduct(product);
        return stock;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stock createUpdatedEntity(EntityManager em) {
        Stock stock = new Stock()
            .addedTimestamp(UPDATED_ADDED_TIMESTAMP)
            .storageLocation(UPDATED_STORAGE_LOCATION)
            .calculatedExpiryTimestamp(UPDATED_CALCULATED_EXPIRY_TIMESTAMP)
            .manualSetExpiryTimestamp(UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createUpdatedEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        stock.setProduct(product);
        return stock;
    }

    @BeforeEach
    public void initTest() {
        stock = createEntity(em);
    }

    @Test
    @Transactional
    void createStock() throws Exception {
        int databaseSizeBeforeCreate = stockRepository.findAll().size();
        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);
        restStockMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeCreate + 1);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getAddedTimestamp()).isEqualTo(DEFAULT_ADDED_TIMESTAMP);
        assertThat(testStock.getStorageLocation()).isEqualTo(DEFAULT_STORAGE_LOCATION);
        assertThat(testStock.getCalculatedExpiryTimestamp()).isEqualTo(DEFAULT_CALCULATED_EXPIRY_TIMESTAMP);
        assertThat(testStock.getManualSetExpiryTimestamp()).isEqualTo(DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(1)).save(testStock);
    }

    @Test
    @Transactional
    void createStockWithExistingId() throws Exception {
        // Create the Stock with an existing ID
        stock.setId(1L);
        StockDTO stockDTO = stockMapper.toDto(stock);

        int databaseSizeBeforeCreate = stockRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeCreate);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(0)).save(stock);
    }

    @Test
    @Transactional
    void checkAddedTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockRepository.findAll().size();
        // set the field null
        stock.setAddedTimestamp(null);

        // Create the Stock, which fails.
        StockDTO stockDTO = stockMapper.toDto(stock);

        restStockMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockDTO))
            )
            .andExpect(status().isBadRequest());

        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCalculatedExpiryTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockRepository.findAll().size();
        // set the field null
        stock.setCalculatedExpiryTimestamp(null);

        // Create the Stock, which fails.
        StockDTO stockDTO = stockMapper.toDto(stock);

        restStockMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockDTO))
            )
            .andExpect(status().isBadRequest());

        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStocks() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList
        restStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stock.getId().intValue())))
            .andExpect(jsonPath("$.[*].addedTimestamp").value(hasItem(sameInstant(DEFAULT_ADDED_TIMESTAMP))))
            .andExpect(jsonPath("$.[*].storageLocation").value(hasItem(DEFAULT_STORAGE_LOCATION)))
            .andExpect(jsonPath("$.[*].calculatedExpiryTimestamp").value(hasItem(sameInstant(DEFAULT_CALCULATED_EXPIRY_TIMESTAMP))))
            .andExpect(jsonPath("$.[*].manualSetExpiryTimestamp").value(hasItem(sameInstant(DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP))));
    }

    @Test
    @Transactional
    void getStock() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get the stock
        restStockMockMvc
            .perform(get(ENTITY_API_URL_ID, stock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stock.getId().intValue()))
            .andExpect(jsonPath("$.addedTimestamp").value(sameInstant(DEFAULT_ADDED_TIMESTAMP)))
            .andExpect(jsonPath("$.storageLocation").value(DEFAULT_STORAGE_LOCATION))
            .andExpect(jsonPath("$.calculatedExpiryTimestamp").value(sameInstant(DEFAULT_CALCULATED_EXPIRY_TIMESTAMP)))
            .andExpect(jsonPath("$.manualSetExpiryTimestamp").value(sameInstant(DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP)));
    }

    @Test
    @Transactional
    void getStocksByIdFiltering() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        Long id = stock.getId();

        defaultStockShouldBeFound("id.equals=" + id);
        defaultStockShouldNotBeFound("id.notEquals=" + id);

        defaultStockShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStockShouldNotBeFound("id.greaterThan=" + id);

        defaultStockShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStockShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStocksByAddedTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where addedTimestamp equals to DEFAULT_ADDED_TIMESTAMP
        defaultStockShouldBeFound("addedTimestamp.equals=" + DEFAULT_ADDED_TIMESTAMP);

        // Get all the stockList where addedTimestamp equals to UPDATED_ADDED_TIMESTAMP
        defaultStockShouldNotBeFound("addedTimestamp.equals=" + UPDATED_ADDED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByAddedTimestampIsNotEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where addedTimestamp not equals to DEFAULT_ADDED_TIMESTAMP
        defaultStockShouldNotBeFound("addedTimestamp.notEquals=" + DEFAULT_ADDED_TIMESTAMP);

        // Get all the stockList where addedTimestamp not equals to UPDATED_ADDED_TIMESTAMP
        defaultStockShouldBeFound("addedTimestamp.notEquals=" + UPDATED_ADDED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByAddedTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where addedTimestamp in DEFAULT_ADDED_TIMESTAMP or UPDATED_ADDED_TIMESTAMP
        defaultStockShouldBeFound("addedTimestamp.in=" + DEFAULT_ADDED_TIMESTAMP + "," + UPDATED_ADDED_TIMESTAMP);

        // Get all the stockList where addedTimestamp equals to UPDATED_ADDED_TIMESTAMP
        defaultStockShouldNotBeFound("addedTimestamp.in=" + UPDATED_ADDED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByAddedTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where addedTimestamp is not null
        defaultStockShouldBeFound("addedTimestamp.specified=true");

        // Get all the stockList where addedTimestamp is null
        defaultStockShouldNotBeFound("addedTimestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllStocksByAddedTimestampIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where addedTimestamp is greater than or equal to DEFAULT_ADDED_TIMESTAMP
        defaultStockShouldBeFound("addedTimestamp.greaterThanOrEqual=" + DEFAULT_ADDED_TIMESTAMP);

        // Get all the stockList where addedTimestamp is greater than or equal to UPDATED_ADDED_TIMESTAMP
        defaultStockShouldNotBeFound("addedTimestamp.greaterThanOrEqual=" + UPDATED_ADDED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByAddedTimestampIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where addedTimestamp is less than or equal to DEFAULT_ADDED_TIMESTAMP
        defaultStockShouldBeFound("addedTimestamp.lessThanOrEqual=" + DEFAULT_ADDED_TIMESTAMP);

        // Get all the stockList where addedTimestamp is less than or equal to SMALLER_ADDED_TIMESTAMP
        defaultStockShouldNotBeFound("addedTimestamp.lessThanOrEqual=" + SMALLER_ADDED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByAddedTimestampIsLessThanSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where addedTimestamp is less than DEFAULT_ADDED_TIMESTAMP
        defaultStockShouldNotBeFound("addedTimestamp.lessThan=" + DEFAULT_ADDED_TIMESTAMP);

        // Get all the stockList where addedTimestamp is less than UPDATED_ADDED_TIMESTAMP
        defaultStockShouldBeFound("addedTimestamp.lessThan=" + UPDATED_ADDED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByAddedTimestampIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where addedTimestamp is greater than DEFAULT_ADDED_TIMESTAMP
        defaultStockShouldNotBeFound("addedTimestamp.greaterThan=" + DEFAULT_ADDED_TIMESTAMP);

        // Get all the stockList where addedTimestamp is greater than SMALLER_ADDED_TIMESTAMP
        defaultStockShouldBeFound("addedTimestamp.greaterThan=" + SMALLER_ADDED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByStorageLocationIsEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where storageLocation equals to DEFAULT_STORAGE_LOCATION
        defaultStockShouldBeFound("storageLocation.equals=" + DEFAULT_STORAGE_LOCATION);

        // Get all the stockList where storageLocation equals to UPDATED_STORAGE_LOCATION
        defaultStockShouldNotBeFound("storageLocation.equals=" + UPDATED_STORAGE_LOCATION);
    }

    @Test
    @Transactional
    void getAllStocksByStorageLocationIsNotEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where storageLocation not equals to DEFAULT_STORAGE_LOCATION
        defaultStockShouldNotBeFound("storageLocation.notEquals=" + DEFAULT_STORAGE_LOCATION);

        // Get all the stockList where storageLocation not equals to UPDATED_STORAGE_LOCATION
        defaultStockShouldBeFound("storageLocation.notEquals=" + UPDATED_STORAGE_LOCATION);
    }

    @Test
    @Transactional
    void getAllStocksByStorageLocationIsInShouldWork() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where storageLocation in DEFAULT_STORAGE_LOCATION or UPDATED_STORAGE_LOCATION
        defaultStockShouldBeFound("storageLocation.in=" + DEFAULT_STORAGE_LOCATION + "," + UPDATED_STORAGE_LOCATION);

        // Get all the stockList where storageLocation equals to UPDATED_STORAGE_LOCATION
        defaultStockShouldNotBeFound("storageLocation.in=" + UPDATED_STORAGE_LOCATION);
    }

    @Test
    @Transactional
    void getAllStocksByStorageLocationIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where storageLocation is not null
        defaultStockShouldBeFound("storageLocation.specified=true");

        // Get all the stockList where storageLocation is null
        defaultStockShouldNotBeFound("storageLocation.specified=false");
    }

    @Test
    @Transactional
    void getAllStocksByStorageLocationContainsSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where storageLocation contains DEFAULT_STORAGE_LOCATION
        defaultStockShouldBeFound("storageLocation.contains=" + DEFAULT_STORAGE_LOCATION);

        // Get all the stockList where storageLocation contains UPDATED_STORAGE_LOCATION
        defaultStockShouldNotBeFound("storageLocation.contains=" + UPDATED_STORAGE_LOCATION);
    }

    @Test
    @Transactional
    void getAllStocksByStorageLocationNotContainsSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where storageLocation does not contain DEFAULT_STORAGE_LOCATION
        defaultStockShouldNotBeFound("storageLocation.doesNotContain=" + DEFAULT_STORAGE_LOCATION);

        // Get all the stockList where storageLocation does not contain UPDATED_STORAGE_LOCATION
        defaultStockShouldBeFound("storageLocation.doesNotContain=" + UPDATED_STORAGE_LOCATION);
    }

    @Test
    @Transactional
    void getAllStocksByCalculatedExpiryTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where calculatedExpiryTimestamp equals to DEFAULT_CALCULATED_EXPIRY_TIMESTAMP
        defaultStockShouldBeFound("calculatedExpiryTimestamp.equals=" + DEFAULT_CALCULATED_EXPIRY_TIMESTAMP);

        // Get all the stockList where calculatedExpiryTimestamp equals to UPDATED_CALCULATED_EXPIRY_TIMESTAMP
        defaultStockShouldNotBeFound("calculatedExpiryTimestamp.equals=" + UPDATED_CALCULATED_EXPIRY_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByCalculatedExpiryTimestampIsNotEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where calculatedExpiryTimestamp not equals to DEFAULT_CALCULATED_EXPIRY_TIMESTAMP
        defaultStockShouldNotBeFound("calculatedExpiryTimestamp.notEquals=" + DEFAULT_CALCULATED_EXPIRY_TIMESTAMP);

        // Get all the stockList where calculatedExpiryTimestamp not equals to UPDATED_CALCULATED_EXPIRY_TIMESTAMP
        defaultStockShouldBeFound("calculatedExpiryTimestamp.notEquals=" + UPDATED_CALCULATED_EXPIRY_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByCalculatedExpiryTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where calculatedExpiryTimestamp in DEFAULT_CALCULATED_EXPIRY_TIMESTAMP or UPDATED_CALCULATED_EXPIRY_TIMESTAMP
        defaultStockShouldBeFound(
            "calculatedExpiryTimestamp.in=" + DEFAULT_CALCULATED_EXPIRY_TIMESTAMP + "," + UPDATED_CALCULATED_EXPIRY_TIMESTAMP
        );

        // Get all the stockList where calculatedExpiryTimestamp equals to UPDATED_CALCULATED_EXPIRY_TIMESTAMP
        defaultStockShouldNotBeFound("calculatedExpiryTimestamp.in=" + UPDATED_CALCULATED_EXPIRY_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByCalculatedExpiryTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where calculatedExpiryTimestamp is not null
        defaultStockShouldBeFound("calculatedExpiryTimestamp.specified=true");

        // Get all the stockList where calculatedExpiryTimestamp is null
        defaultStockShouldNotBeFound("calculatedExpiryTimestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllStocksByCalculatedExpiryTimestampIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where calculatedExpiryTimestamp is greater than or equal to DEFAULT_CALCULATED_EXPIRY_TIMESTAMP
        defaultStockShouldBeFound("calculatedExpiryTimestamp.greaterThanOrEqual=" + DEFAULT_CALCULATED_EXPIRY_TIMESTAMP);

        // Get all the stockList where calculatedExpiryTimestamp is greater than or equal to UPDATED_CALCULATED_EXPIRY_TIMESTAMP
        defaultStockShouldNotBeFound("calculatedExpiryTimestamp.greaterThanOrEqual=" + UPDATED_CALCULATED_EXPIRY_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByCalculatedExpiryTimestampIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where calculatedExpiryTimestamp is less than or equal to DEFAULT_CALCULATED_EXPIRY_TIMESTAMP
        defaultStockShouldBeFound("calculatedExpiryTimestamp.lessThanOrEqual=" + DEFAULT_CALCULATED_EXPIRY_TIMESTAMP);

        // Get all the stockList where calculatedExpiryTimestamp is less than or equal to SMALLER_CALCULATED_EXPIRY_TIMESTAMP
        defaultStockShouldNotBeFound("calculatedExpiryTimestamp.lessThanOrEqual=" + SMALLER_CALCULATED_EXPIRY_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByCalculatedExpiryTimestampIsLessThanSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where calculatedExpiryTimestamp is less than DEFAULT_CALCULATED_EXPIRY_TIMESTAMP
        defaultStockShouldNotBeFound("calculatedExpiryTimestamp.lessThan=" + DEFAULT_CALCULATED_EXPIRY_TIMESTAMP);

        // Get all the stockList where calculatedExpiryTimestamp is less than UPDATED_CALCULATED_EXPIRY_TIMESTAMP
        defaultStockShouldBeFound("calculatedExpiryTimestamp.lessThan=" + UPDATED_CALCULATED_EXPIRY_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByCalculatedExpiryTimestampIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where calculatedExpiryTimestamp is greater than DEFAULT_CALCULATED_EXPIRY_TIMESTAMP
        defaultStockShouldNotBeFound("calculatedExpiryTimestamp.greaterThan=" + DEFAULT_CALCULATED_EXPIRY_TIMESTAMP);

        // Get all the stockList where calculatedExpiryTimestamp is greater than SMALLER_CALCULATED_EXPIRY_TIMESTAMP
        defaultStockShouldBeFound("calculatedExpiryTimestamp.greaterThan=" + SMALLER_CALCULATED_EXPIRY_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByManualSetExpiryTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where manualSetExpiryTimestamp equals to DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP
        defaultStockShouldBeFound("manualSetExpiryTimestamp.equals=" + DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP);

        // Get all the stockList where manualSetExpiryTimestamp equals to UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP
        defaultStockShouldNotBeFound("manualSetExpiryTimestamp.equals=" + UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByManualSetExpiryTimestampIsNotEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where manualSetExpiryTimestamp not equals to DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP
        defaultStockShouldNotBeFound("manualSetExpiryTimestamp.notEquals=" + DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP);

        // Get all the stockList where manualSetExpiryTimestamp not equals to UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP
        defaultStockShouldBeFound("manualSetExpiryTimestamp.notEquals=" + UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByManualSetExpiryTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where manualSetExpiryTimestamp in DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP or UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP
        defaultStockShouldBeFound(
            "manualSetExpiryTimestamp.in=" + DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP + "," + UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP
        );

        // Get all the stockList where manualSetExpiryTimestamp equals to UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP
        defaultStockShouldNotBeFound("manualSetExpiryTimestamp.in=" + UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByManualSetExpiryTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where manualSetExpiryTimestamp is not null
        defaultStockShouldBeFound("manualSetExpiryTimestamp.specified=true");

        // Get all the stockList where manualSetExpiryTimestamp is null
        defaultStockShouldNotBeFound("manualSetExpiryTimestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllStocksByManualSetExpiryTimestampIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where manualSetExpiryTimestamp is greater than or equal to DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP
        defaultStockShouldBeFound("manualSetExpiryTimestamp.greaterThanOrEqual=" + DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP);

        // Get all the stockList where manualSetExpiryTimestamp is greater than or equal to UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP
        defaultStockShouldNotBeFound("manualSetExpiryTimestamp.greaterThanOrEqual=" + UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByManualSetExpiryTimestampIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where manualSetExpiryTimestamp is less than or equal to DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP
        defaultStockShouldBeFound("manualSetExpiryTimestamp.lessThanOrEqual=" + DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP);

        // Get all the stockList where manualSetExpiryTimestamp is less than or equal to SMALLER_MANUAL_SET_EXPIRY_TIMESTAMP
        defaultStockShouldNotBeFound("manualSetExpiryTimestamp.lessThanOrEqual=" + SMALLER_MANUAL_SET_EXPIRY_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByManualSetExpiryTimestampIsLessThanSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where manualSetExpiryTimestamp is less than DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP
        defaultStockShouldNotBeFound("manualSetExpiryTimestamp.lessThan=" + DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP);

        // Get all the stockList where manualSetExpiryTimestamp is less than UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP
        defaultStockShouldBeFound("manualSetExpiryTimestamp.lessThan=" + UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByManualSetExpiryTimestampIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList where manualSetExpiryTimestamp is greater than DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP
        defaultStockShouldNotBeFound("manualSetExpiryTimestamp.greaterThan=" + DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP);

        // Get all the stockList where manualSetExpiryTimestamp is greater than SMALLER_MANUAL_SET_EXPIRY_TIMESTAMP
        defaultStockShouldBeFound("manualSetExpiryTimestamp.greaterThan=" + SMALLER_MANUAL_SET_EXPIRY_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllStocksByProductIsEqualToSomething() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(product);
        em.flush();
        stock.setProduct(product);
        stockRepository.saveAndFlush(stock);
        Long productId = product.getId();

        // Get all the stockList where product equals to productId
        defaultStockShouldBeFound("productId.equals=" + productId);

        // Get all the stockList where product equals to (productId + 1)
        defaultStockShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStockShouldBeFound(String filter) throws Exception {
        restStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stock.getId().intValue())))
            .andExpect(jsonPath("$.[*].addedTimestamp").value(hasItem(sameInstant(DEFAULT_ADDED_TIMESTAMP))))
            .andExpect(jsonPath("$.[*].storageLocation").value(hasItem(DEFAULT_STORAGE_LOCATION)))
            .andExpect(jsonPath("$.[*].calculatedExpiryTimestamp").value(hasItem(sameInstant(DEFAULT_CALCULATED_EXPIRY_TIMESTAMP))))
            .andExpect(jsonPath("$.[*].manualSetExpiryTimestamp").value(hasItem(sameInstant(DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP))));

        // Check, that the count call also returns 1
        restStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStockShouldNotBeFound(String filter) throws Exception {
        restStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStock() throws Exception {
        // Get the stock
        restStockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewStock() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        int databaseSizeBeforeUpdate = stockRepository.findAll().size();

        // Update the stock
        Stock updatedStock = stockRepository.findById(stock.getId()).get();
        // Disconnect from session so that the updates on updatedStock are not directly saved in db
        em.detach(updatedStock);
        updatedStock
            .addedTimestamp(UPDATED_ADDED_TIMESTAMP)
            .storageLocation(UPDATED_STORAGE_LOCATION)
            .calculatedExpiryTimestamp(UPDATED_CALCULATED_EXPIRY_TIMESTAMP)
            .manualSetExpiryTimestamp(UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP);
        StockDTO stockDTO = stockMapper.toDto(updatedStock);

        restStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockDTO))
            )
            .andExpect(status().isOk());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getAddedTimestamp()).isEqualTo(UPDATED_ADDED_TIMESTAMP);
        assertThat(testStock.getStorageLocation()).isEqualTo(UPDATED_STORAGE_LOCATION);
        assertThat(testStock.getCalculatedExpiryTimestamp()).isEqualTo(UPDATED_CALCULATED_EXPIRY_TIMESTAMP);
        assertThat(testStock.getManualSetExpiryTimestamp()).isEqualTo(UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository).save(testStock);
    }

    @Test
    @Transactional
    void putNonExistingStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(0)).save(stock);
    }

    @Test
    @Transactional
    void putWithIdMismatchStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(0)).save(stock);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(0)).save(stock);
    }

    @Test
    @Transactional
    void partialUpdateStockWithPatch() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        int databaseSizeBeforeUpdate = stockRepository.findAll().size();

        // Update the stock using partial update
        Stock partialUpdatedStock = new Stock();
        partialUpdatedStock.setId(stock.getId());

        partialUpdatedStock.addedTimestamp(UPDATED_ADDED_TIMESTAMP).storageLocation(UPDATED_STORAGE_LOCATION);

        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStock.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStock))
            )
            .andExpect(status().isOk());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getAddedTimestamp()).isEqualTo(UPDATED_ADDED_TIMESTAMP);
        assertThat(testStock.getStorageLocation()).isEqualTo(UPDATED_STORAGE_LOCATION);
        assertThat(testStock.getCalculatedExpiryTimestamp()).isEqualTo(DEFAULT_CALCULATED_EXPIRY_TIMESTAMP);
        assertThat(testStock.getManualSetExpiryTimestamp()).isEqualTo(DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP);
    }

    @Test
    @Transactional
    void fullUpdateStockWithPatch() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        int databaseSizeBeforeUpdate = stockRepository.findAll().size();

        // Update the stock using partial update
        Stock partialUpdatedStock = new Stock();
        partialUpdatedStock.setId(stock.getId());

        partialUpdatedStock
            .addedTimestamp(UPDATED_ADDED_TIMESTAMP)
            .storageLocation(UPDATED_STORAGE_LOCATION)
            .calculatedExpiryTimestamp(UPDATED_CALCULATED_EXPIRY_TIMESTAMP)
            .manualSetExpiryTimestamp(UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP);

        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStock.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStock))
            )
            .andExpect(status().isOk());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getAddedTimestamp()).isEqualTo(UPDATED_ADDED_TIMESTAMP);
        assertThat(testStock.getStorageLocation()).isEqualTo(UPDATED_STORAGE_LOCATION);
        assertThat(testStock.getCalculatedExpiryTimestamp()).isEqualTo(UPDATED_CALCULATED_EXPIRY_TIMESTAMP);
        assertThat(testStock.getManualSetExpiryTimestamp()).isEqualTo(UPDATED_MANUAL_SET_EXPIRY_TIMESTAMP);
    }

    @Test
    @Transactional
    void patchNonExistingStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(0)).save(stock);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(0)).save(stock);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // Create the Stock
        StockDTO stockDTO = stockMapper.toDto(stock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(0)).save(stock);
    }

    @Test
    @Transactional
    void deleteStock() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        int databaseSizeBeforeDelete = stockRepository.findAll().size();

        // Delete the stock
        restStockMockMvc
            .perform(delete(ENTITY_API_URL_ID, stock.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Stock in Elasticsearch
        verify(mockStockSearchRepository, times(1)).deleteById(stock.getId());
    }

    @Test
    @Transactional
    void searchStock() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        stockRepository.saveAndFlush(stock);
        when(mockStockSearchRepository.search("id:" + stock.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(stock), PageRequest.of(0, 1), 1));

        // Search the stock
        restStockMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + stock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stock.getId().intValue())))
            .andExpect(jsonPath("$.[*].addedTimestamp").value(hasItem(sameInstant(DEFAULT_ADDED_TIMESTAMP))))
            .andExpect(jsonPath("$.[*].storageLocation").value(hasItem(DEFAULT_STORAGE_LOCATION)))
            .andExpect(jsonPath("$.[*].calculatedExpiryTimestamp").value(hasItem(sameInstant(DEFAULT_CALCULATED_EXPIRY_TIMESTAMP))))
            .andExpect(jsonPath("$.[*].manualSetExpiryTimestamp").value(hasItem(sameInstant(DEFAULT_MANUAL_SET_EXPIRY_TIMESTAMP))));
    }
}
