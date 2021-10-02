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
import java.util.UUID;
import javax.persistence.EntityManager;
import org.codingspiderfox.IntegrationTest;
import org.codingspiderfox.domain.FinanceAccount;
import org.codingspiderfox.domain.FinanceTransactions;
import org.codingspiderfox.repository.FinanceTransactionsRepository;
import org.codingspiderfox.repository.search.FinanceTransactionsSearchRepository;
import org.codingspiderfox.service.criteria.FinanceTransactionsCriteria;
import org.codingspiderfox.service.dto.FinanceTransactionsDTO;
import org.codingspiderfox.service.mapper.FinanceTransactionsMapper;
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
 * Integration tests for the {@link FinanceTransactionsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FinanceTransactionsResourceIT {

    private static final ZonedDateTime DEFAULT_EXECUTION_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_EXECUTION_TIMESTAMP = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_EXECUTION_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Double DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT = 1D;
    private static final Double UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT = 2D;
    private static final Double SMALLER_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT = 1D - 1D;

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/finance-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/finance-transactions";

    @Autowired
    private FinanceTransactionsRepository financeTransactionsRepository;

    @Autowired
    private FinanceTransactionsMapper financeTransactionsMapper;

    /**
     * This repository is mocked in the org.codingspiderfox.repository.search test package.
     *
     * @see org.codingspiderfox.repository.search.FinanceTransactionsSearchRepositoryMockConfiguration
     */
    @Autowired
    private FinanceTransactionsSearchRepository mockFinanceTransactionsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFinanceTransactionsMockMvc;

    private FinanceTransactions financeTransactions;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FinanceTransactions createEntity(EntityManager em) {
        FinanceTransactions financeTransactions = new FinanceTransactions()
            .executionTimestamp(DEFAULT_EXECUTION_TIMESTAMP)
            .amountAddedToDestinationAccount(DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT)
            .comment(DEFAULT_COMMENT);
        // Add required entity
        FinanceAccount financeAccount;
        if (TestUtil.findAll(em, FinanceAccount.class).isEmpty()) {
            financeAccount = FinanceAccountResourceIT.createEntity(em);
            em.persist(financeAccount);
            em.flush();
        } else {
            financeAccount = TestUtil.findAll(em, FinanceAccount.class).get(0);
        }
        financeTransactions.setDestinationAccount(financeAccount);
        // Add required entity
        financeTransactions.setReferenceAccount(financeAccount);
        return financeTransactions;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FinanceTransactions createUpdatedEntity(EntityManager em) {
        FinanceTransactions financeTransactions = new FinanceTransactions()
            .executionTimestamp(UPDATED_EXECUTION_TIMESTAMP)
            .amountAddedToDestinationAccount(UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT)
            .comment(UPDATED_COMMENT);
        // Add required entity
        FinanceAccount financeAccount;
        if (TestUtil.findAll(em, FinanceAccount.class).isEmpty()) {
            financeAccount = FinanceAccountResourceIT.createUpdatedEntity(em);
            em.persist(financeAccount);
            em.flush();
        } else {
            financeAccount = TestUtil.findAll(em, FinanceAccount.class).get(0);
        }
        financeTransactions.setDestinationAccount(financeAccount);
        // Add required entity
        financeTransactions.setReferenceAccount(financeAccount);
        return financeTransactions;
    }

    @BeforeEach
    public void initTest() {
        financeTransactions = createEntity(em);
    }

    @Test
    @Transactional
    void createFinanceTransactions() throws Exception {
        int databaseSizeBeforeCreate = financeTransactionsRepository.findAll().size();
        // Create the FinanceTransactions
        FinanceTransactionsDTO financeTransactionsDTO = financeTransactionsMapper.toDto(financeTransactions);
        restFinanceTransactionsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeTransactionsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the FinanceTransactions in the database
        List<FinanceTransactions> financeTransactionsList = financeTransactionsRepository.findAll();
        assertThat(financeTransactionsList).hasSize(databaseSizeBeforeCreate + 1);
        FinanceTransactions testFinanceTransactions = financeTransactionsList.get(financeTransactionsList.size() - 1);
        assertThat(testFinanceTransactions.getExecutionTimestamp()).isEqualTo(DEFAULT_EXECUTION_TIMESTAMP);
        assertThat(testFinanceTransactions.getAmountAddedToDestinationAccount()).isEqualTo(DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT);
        assertThat(testFinanceTransactions.getComment()).isEqualTo(DEFAULT_COMMENT);

        // Validate the id for MapsId, the ids must be same
        assertThat(testFinanceTransactions.getId()).isEqualTo(testFinanceTransactions.getDestinationAccount().getId());

        // Validate the FinanceTransactions in Elasticsearch
        verify(mockFinanceTransactionsSearchRepository, times(1)).save(testFinanceTransactions);
    }

    @Test
    @Transactional
    void createFinanceTransactionsWithExistingId() throws Exception {
        // Create the FinanceTransactions with an existing ID
        financeTransactions.setId("existing_id");
        FinanceTransactionsDTO financeTransactionsDTO = financeTransactionsMapper.toDto(financeTransactions);

        int databaseSizeBeforeCreate = financeTransactionsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFinanceTransactionsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeTransactionsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceTransactions in the database
        List<FinanceTransactions> financeTransactionsList = financeTransactionsRepository.findAll();
        assertThat(financeTransactionsList).hasSize(databaseSizeBeforeCreate);

        // Validate the FinanceTransactions in Elasticsearch
        verify(mockFinanceTransactionsSearchRepository, times(0)).save(financeTransactions);
    }

    @Test
    @Transactional
    void updateFinanceTransactionsMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);
        int databaseSizeBeforeCreate = financeTransactionsRepository.findAll().size();

        // Load the financeTransactions
        FinanceTransactions updatedFinanceTransactions = financeTransactionsRepository.findById(financeTransactions.getId()).get();
        assertThat(updatedFinanceTransactions).isNotNull();
        // Disconnect from session so that the updates on updatedFinanceTransactions are not directly saved in db
        em.detach(updatedFinanceTransactions);

        // Update the FinanceAccount with new association value
        updatedFinanceTransactions.setDestinationAccount(new FinanceAccount());
        FinanceTransactionsDTO updatedFinanceTransactionsDTO = financeTransactionsMapper.toDto(updatedFinanceTransactions);
        assertThat(updatedFinanceTransactionsDTO).isNotNull();

        // Update the entity
        restFinanceTransactionsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFinanceTransactionsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFinanceTransactionsDTO))
            )
            .andExpect(status().isOk());

        // Validate the FinanceTransactions in the database
        List<FinanceTransactions> financeTransactionsList = financeTransactionsRepository.findAll();
        assertThat(financeTransactionsList).hasSize(databaseSizeBeforeCreate);
        FinanceTransactions testFinanceTransactions = financeTransactionsList.get(financeTransactionsList.size() - 1);

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testFinanceTransactions.getId()).isEqualTo(testFinanceTransactions.getFinanceAccount().getId());

        // Validate the FinanceTransactions in Elasticsearch
        verify(mockFinanceTransactionsSearchRepository).save(financeTransactions);
    }

    @Test
    @Transactional
    void checkExecutionTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = financeTransactionsRepository.findAll().size();
        // set the field null
        financeTransactions.setExecutionTimestamp(null);

        // Create the FinanceTransactions, which fails.
        FinanceTransactionsDTO financeTransactionsDTO = financeTransactionsMapper.toDto(financeTransactions);

        restFinanceTransactionsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeTransactionsDTO))
            )
            .andExpect(status().isBadRequest());

        List<FinanceTransactions> financeTransactionsList = financeTransactionsRepository.findAll();
        assertThat(financeTransactionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountAddedToDestinationAccountIsRequired() throws Exception {
        int databaseSizeBeforeTest = financeTransactionsRepository.findAll().size();
        // set the field null
        financeTransactions.setAmountAddedToDestinationAccount(null);

        // Create the FinanceTransactions, which fails.
        FinanceTransactionsDTO financeTransactionsDTO = financeTransactionsMapper.toDto(financeTransactions);

        restFinanceTransactionsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeTransactionsDTO))
            )
            .andExpect(status().isBadRequest());

        List<FinanceTransactions> financeTransactionsList = financeTransactionsRepository.findAll();
        assertThat(financeTransactionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFinanceTransactions() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList
        restFinanceTransactionsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(financeTransactions.getId())))
            .andExpect(jsonPath("$.[*].executionTimestamp").value(hasItem(sameInstant(DEFAULT_EXECUTION_TIMESTAMP))))
            .andExpect(
                jsonPath("$.[*].amountAddedToDestinationAccount").value(hasItem(DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT.doubleValue()))
            )
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)));
    }

    @Test
    @Transactional
    void getFinanceTransactions() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get the financeTransactions
        restFinanceTransactionsMockMvc
            .perform(get(ENTITY_API_URL_ID, financeTransactions.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(financeTransactions.getId()))
            .andExpect(jsonPath("$.executionTimestamp").value(sameInstant(DEFAULT_EXECUTION_TIMESTAMP)))
            .andExpect(jsonPath("$.amountAddedToDestinationAccount").value(DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT.doubleValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT));
    }

    @Test
    @Transactional
    void getFinanceTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        String id = financeTransactions.getId();

        defaultFinanceTransactionsShouldBeFound("id.equals=" + id);
        defaultFinanceTransactionsShouldNotBeFound("id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByExecutionTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where executionTimestamp equals to DEFAULT_EXECUTION_TIMESTAMP
        defaultFinanceTransactionsShouldBeFound("executionTimestamp.equals=" + DEFAULT_EXECUTION_TIMESTAMP);

        // Get all the financeTransactionsList where executionTimestamp equals to UPDATED_EXECUTION_TIMESTAMP
        defaultFinanceTransactionsShouldNotBeFound("executionTimestamp.equals=" + UPDATED_EXECUTION_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByExecutionTimestampIsNotEqualToSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where executionTimestamp not equals to DEFAULT_EXECUTION_TIMESTAMP
        defaultFinanceTransactionsShouldNotBeFound("executionTimestamp.notEquals=" + DEFAULT_EXECUTION_TIMESTAMP);

        // Get all the financeTransactionsList where executionTimestamp not equals to UPDATED_EXECUTION_TIMESTAMP
        defaultFinanceTransactionsShouldBeFound("executionTimestamp.notEquals=" + UPDATED_EXECUTION_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByExecutionTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where executionTimestamp in DEFAULT_EXECUTION_TIMESTAMP or UPDATED_EXECUTION_TIMESTAMP
        defaultFinanceTransactionsShouldBeFound("executionTimestamp.in=" + DEFAULT_EXECUTION_TIMESTAMP + "," + UPDATED_EXECUTION_TIMESTAMP);

        // Get all the financeTransactionsList where executionTimestamp equals to UPDATED_EXECUTION_TIMESTAMP
        defaultFinanceTransactionsShouldNotBeFound("executionTimestamp.in=" + UPDATED_EXECUTION_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByExecutionTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where executionTimestamp is not null
        defaultFinanceTransactionsShouldBeFound("executionTimestamp.specified=true");

        // Get all the financeTransactionsList where executionTimestamp is null
        defaultFinanceTransactionsShouldNotBeFound("executionTimestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByExecutionTimestampIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where executionTimestamp is greater than or equal to DEFAULT_EXECUTION_TIMESTAMP
        defaultFinanceTransactionsShouldBeFound("executionTimestamp.greaterThanOrEqual=" + DEFAULT_EXECUTION_TIMESTAMP);

        // Get all the financeTransactionsList where executionTimestamp is greater than or equal to UPDATED_EXECUTION_TIMESTAMP
        defaultFinanceTransactionsShouldNotBeFound("executionTimestamp.greaterThanOrEqual=" + UPDATED_EXECUTION_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByExecutionTimestampIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where executionTimestamp is less than or equal to DEFAULT_EXECUTION_TIMESTAMP
        defaultFinanceTransactionsShouldBeFound("executionTimestamp.lessThanOrEqual=" + DEFAULT_EXECUTION_TIMESTAMP);

        // Get all the financeTransactionsList where executionTimestamp is less than or equal to SMALLER_EXECUTION_TIMESTAMP
        defaultFinanceTransactionsShouldNotBeFound("executionTimestamp.lessThanOrEqual=" + SMALLER_EXECUTION_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByExecutionTimestampIsLessThanSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where executionTimestamp is less than DEFAULT_EXECUTION_TIMESTAMP
        defaultFinanceTransactionsShouldNotBeFound("executionTimestamp.lessThan=" + DEFAULT_EXECUTION_TIMESTAMP);

        // Get all the financeTransactionsList where executionTimestamp is less than UPDATED_EXECUTION_TIMESTAMP
        defaultFinanceTransactionsShouldBeFound("executionTimestamp.lessThan=" + UPDATED_EXECUTION_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByExecutionTimestampIsGreaterThanSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where executionTimestamp is greater than DEFAULT_EXECUTION_TIMESTAMP
        defaultFinanceTransactionsShouldNotBeFound("executionTimestamp.greaterThan=" + DEFAULT_EXECUTION_TIMESTAMP);

        // Get all the financeTransactionsList where executionTimestamp is greater than SMALLER_EXECUTION_TIMESTAMP
        defaultFinanceTransactionsShouldBeFound("executionTimestamp.greaterThan=" + SMALLER_EXECUTION_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByAmountAddedToDestinationAccountIsEqualToSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where amountAddedToDestinationAccount equals to DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        defaultFinanceTransactionsShouldBeFound("amountAddedToDestinationAccount.equals=" + DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT);

        // Get all the financeTransactionsList where amountAddedToDestinationAccount equals to UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        defaultFinanceTransactionsShouldNotBeFound("amountAddedToDestinationAccount.equals=" + UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByAmountAddedToDestinationAccountIsNotEqualToSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where amountAddedToDestinationAccount not equals to DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        defaultFinanceTransactionsShouldNotBeFound(
            "amountAddedToDestinationAccount.notEquals=" + DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        );

        // Get all the financeTransactionsList where amountAddedToDestinationAccount not equals to UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        defaultFinanceTransactionsShouldBeFound("amountAddedToDestinationAccount.notEquals=" + UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByAmountAddedToDestinationAccountIsInShouldWork() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where amountAddedToDestinationAccount in DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT or UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        defaultFinanceTransactionsShouldBeFound(
            "amountAddedToDestinationAccount.in=" +
            DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT +
            "," +
            UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        );

        // Get all the financeTransactionsList where amountAddedToDestinationAccount equals to UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        defaultFinanceTransactionsShouldNotBeFound("amountAddedToDestinationAccount.in=" + UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByAmountAddedToDestinationAccountIsNullOrNotNull() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where amountAddedToDestinationAccount is not null
        defaultFinanceTransactionsShouldBeFound("amountAddedToDestinationAccount.specified=true");

        // Get all the financeTransactionsList where amountAddedToDestinationAccount is null
        defaultFinanceTransactionsShouldNotBeFound("amountAddedToDestinationAccount.specified=false");
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByAmountAddedToDestinationAccountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where amountAddedToDestinationAccount is greater than or equal to DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        defaultFinanceTransactionsShouldBeFound(
            "amountAddedToDestinationAccount.greaterThanOrEqual=" + DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        );

        // Get all the financeTransactionsList where amountAddedToDestinationAccount is greater than or equal to UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        defaultFinanceTransactionsShouldNotBeFound(
            "amountAddedToDestinationAccount.greaterThanOrEqual=" + UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        );
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByAmountAddedToDestinationAccountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where amountAddedToDestinationAccount is less than or equal to DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        defaultFinanceTransactionsShouldBeFound(
            "amountAddedToDestinationAccount.lessThanOrEqual=" + DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        );

        // Get all the financeTransactionsList where amountAddedToDestinationAccount is less than or equal to SMALLER_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        defaultFinanceTransactionsShouldNotBeFound(
            "amountAddedToDestinationAccount.lessThanOrEqual=" + SMALLER_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        );
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByAmountAddedToDestinationAccountIsLessThanSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where amountAddedToDestinationAccount is less than DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        defaultFinanceTransactionsShouldNotBeFound(
            "amountAddedToDestinationAccount.lessThan=" + DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        );

        // Get all the financeTransactionsList where amountAddedToDestinationAccount is less than UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        defaultFinanceTransactionsShouldBeFound("amountAddedToDestinationAccount.lessThan=" + UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByAmountAddedToDestinationAccountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where amountAddedToDestinationAccount is greater than DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        defaultFinanceTransactionsShouldNotBeFound(
            "amountAddedToDestinationAccount.greaterThan=" + DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        );

        // Get all the financeTransactionsList where amountAddedToDestinationAccount is greater than SMALLER_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        defaultFinanceTransactionsShouldBeFound(
            "amountAddedToDestinationAccount.greaterThan=" + SMALLER_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT
        );
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where comment equals to DEFAULT_COMMENT
        defaultFinanceTransactionsShouldBeFound("comment.equals=" + DEFAULT_COMMENT);

        // Get all the financeTransactionsList where comment equals to UPDATED_COMMENT
        defaultFinanceTransactionsShouldNotBeFound("comment.equals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByCommentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where comment not equals to DEFAULT_COMMENT
        defaultFinanceTransactionsShouldNotBeFound("comment.notEquals=" + DEFAULT_COMMENT);

        // Get all the financeTransactionsList where comment not equals to UPDATED_COMMENT
        defaultFinanceTransactionsShouldBeFound("comment.notEquals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByCommentIsInShouldWork() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where comment in DEFAULT_COMMENT or UPDATED_COMMENT
        defaultFinanceTransactionsShouldBeFound("comment.in=" + DEFAULT_COMMENT + "," + UPDATED_COMMENT);

        // Get all the financeTransactionsList where comment equals to UPDATED_COMMENT
        defaultFinanceTransactionsShouldNotBeFound("comment.in=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByCommentIsNullOrNotNull() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where comment is not null
        defaultFinanceTransactionsShouldBeFound("comment.specified=true");

        // Get all the financeTransactionsList where comment is null
        defaultFinanceTransactionsShouldNotBeFound("comment.specified=false");
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByCommentContainsSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where comment contains DEFAULT_COMMENT
        defaultFinanceTransactionsShouldBeFound("comment.contains=" + DEFAULT_COMMENT);

        // Get all the financeTransactionsList where comment contains UPDATED_COMMENT
        defaultFinanceTransactionsShouldNotBeFound("comment.contains=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByCommentNotContainsSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        // Get all the financeTransactionsList where comment does not contain DEFAULT_COMMENT
        defaultFinanceTransactionsShouldNotBeFound("comment.doesNotContain=" + DEFAULT_COMMENT);

        // Get all the financeTransactionsList where comment does not contain UPDATED_COMMENT
        defaultFinanceTransactionsShouldBeFound("comment.doesNotContain=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByDestinationAccountIsEqualToSomething() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);
        FinanceAccount destinationAccount;
        if (TestUtil.findAll(em, FinanceAccount.class).isEmpty()) {
            destinationAccount = FinanceAccountResourceIT.createEntity(em);
            em.persist(destinationAccount);
            em.flush();
        } else {
            destinationAccount = TestUtil.findAll(em, FinanceAccount.class).get(0);
        }
        em.persist(destinationAccount);
        em.flush();
        financeTransactions.setDestinationAccount(destinationAccount);
        financeTransactionsRepository.saveAndFlush(financeTransactions);
        String destinationAccountId = destinationAccount.getId();

        // Get all the financeTransactionsList where destinationAccount equals to destinationAccountId
        defaultFinanceTransactionsShouldBeFound("destinationAccountId.equals=" + destinationAccountId);

        // Get all the financeTransactionsList where destinationAccount equals to "invalid-id"
        defaultFinanceTransactionsShouldNotBeFound("destinationAccountId.equals=" + "invalid-id");
    }

    @Test
    @Transactional
    void getAllFinanceTransactionsByReferenceAccountIsEqualToSomething() throws Exception {
        // Get already existing entity
        FinanceAccount referenceAccount = financeTransactions.getReferenceAccount();
        financeTransactionsRepository.saveAndFlush(financeTransactions);
        String referenceAccountId = referenceAccount.getId();

        // Get all the financeTransactionsList where referenceAccount equals to referenceAccountId
        defaultFinanceTransactionsShouldBeFound("referenceAccountId.equals=" + referenceAccountId);

        // Get all the financeTransactionsList where referenceAccount equals to "invalid-id"
        defaultFinanceTransactionsShouldNotBeFound("referenceAccountId.equals=" + "invalid-id");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFinanceTransactionsShouldBeFound(String filter) throws Exception {
        restFinanceTransactionsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(financeTransactions.getId())))
            .andExpect(jsonPath("$.[*].executionTimestamp").value(hasItem(sameInstant(DEFAULT_EXECUTION_TIMESTAMP))))
            .andExpect(
                jsonPath("$.[*].amountAddedToDestinationAccount").value(hasItem(DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT.doubleValue()))
            )
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)));

        // Check, that the count call also returns 1
        restFinanceTransactionsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFinanceTransactionsShouldNotBeFound(String filter) throws Exception {
        restFinanceTransactionsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFinanceTransactionsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFinanceTransactions() throws Exception {
        // Get the financeTransactions
        restFinanceTransactionsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFinanceTransactions() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        int databaseSizeBeforeUpdate = financeTransactionsRepository.findAll().size();

        // Update the financeTransactions
        FinanceTransactions updatedFinanceTransactions = financeTransactionsRepository.findById(financeTransactions.getId()).get();
        // Disconnect from session so that the updates on updatedFinanceTransactions are not directly saved in db
        em.detach(updatedFinanceTransactions);
        updatedFinanceTransactions
            .executionTimestamp(UPDATED_EXECUTION_TIMESTAMP)
            .amountAddedToDestinationAccount(UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT)
            .comment(UPDATED_COMMENT);
        FinanceTransactionsDTO financeTransactionsDTO = financeTransactionsMapper.toDto(updatedFinanceTransactions);

        restFinanceTransactionsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, financeTransactionsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeTransactionsDTO))
            )
            .andExpect(status().isOk());

        // Validate the FinanceTransactions in the database
        List<FinanceTransactions> financeTransactionsList = financeTransactionsRepository.findAll();
        assertThat(financeTransactionsList).hasSize(databaseSizeBeforeUpdate);
        FinanceTransactions testFinanceTransactions = financeTransactionsList.get(financeTransactionsList.size() - 1);
        assertThat(testFinanceTransactions.getExecutionTimestamp()).isEqualTo(UPDATED_EXECUTION_TIMESTAMP);
        assertThat(testFinanceTransactions.getAmountAddedToDestinationAccount()).isEqualTo(UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT);
        assertThat(testFinanceTransactions.getComment()).isEqualTo(UPDATED_COMMENT);

        // Validate the FinanceTransactions in Elasticsearch
        verify(mockFinanceTransactionsSearchRepository).save(testFinanceTransactions);
    }

    @Test
    @Transactional
    void putNonExistingFinanceTransactions() throws Exception {
        int databaseSizeBeforeUpdate = financeTransactionsRepository.findAll().size();
        financeTransactions.setId(UUID.randomUUID().toString());

        // Create the FinanceTransactions
        FinanceTransactionsDTO financeTransactionsDTO = financeTransactionsMapper.toDto(financeTransactions);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFinanceTransactionsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, financeTransactionsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeTransactionsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceTransactions in the database
        List<FinanceTransactions> financeTransactionsList = financeTransactionsRepository.findAll();
        assertThat(financeTransactionsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FinanceTransactions in Elasticsearch
        verify(mockFinanceTransactionsSearchRepository, times(0)).save(financeTransactions);
    }

    @Test
    @Transactional
    void putWithIdMismatchFinanceTransactions() throws Exception {
        int databaseSizeBeforeUpdate = financeTransactionsRepository.findAll().size();
        financeTransactions.setId(UUID.randomUUID().toString());

        // Create the FinanceTransactions
        FinanceTransactionsDTO financeTransactionsDTO = financeTransactionsMapper.toDto(financeTransactions);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinanceTransactionsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeTransactionsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceTransactions in the database
        List<FinanceTransactions> financeTransactionsList = financeTransactionsRepository.findAll();
        assertThat(financeTransactionsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FinanceTransactions in Elasticsearch
        verify(mockFinanceTransactionsSearchRepository, times(0)).save(financeTransactions);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFinanceTransactions() throws Exception {
        int databaseSizeBeforeUpdate = financeTransactionsRepository.findAll().size();
        financeTransactions.setId(UUID.randomUUID().toString());

        // Create the FinanceTransactions
        FinanceTransactionsDTO financeTransactionsDTO = financeTransactionsMapper.toDto(financeTransactions);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinanceTransactionsMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeTransactionsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FinanceTransactions in the database
        List<FinanceTransactions> financeTransactionsList = financeTransactionsRepository.findAll();
        assertThat(financeTransactionsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FinanceTransactions in Elasticsearch
        verify(mockFinanceTransactionsSearchRepository, times(0)).save(financeTransactions);
    }

    @Test
    @Transactional
    void partialUpdateFinanceTransactionsWithPatch() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        int databaseSizeBeforeUpdate = financeTransactionsRepository.findAll().size();

        // Update the financeTransactions using partial update
        FinanceTransactions partialUpdatedFinanceTransactions = new FinanceTransactions();
        partialUpdatedFinanceTransactions.setId(financeTransactions.getId());

        partialUpdatedFinanceTransactions.amountAddedToDestinationAccount(UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT);

        restFinanceTransactionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFinanceTransactions.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFinanceTransactions))
            )
            .andExpect(status().isOk());

        // Validate the FinanceTransactions in the database
        List<FinanceTransactions> financeTransactionsList = financeTransactionsRepository.findAll();
        assertThat(financeTransactionsList).hasSize(databaseSizeBeforeUpdate);
        FinanceTransactions testFinanceTransactions = financeTransactionsList.get(financeTransactionsList.size() - 1);
        assertThat(testFinanceTransactions.getExecutionTimestamp()).isEqualTo(DEFAULT_EXECUTION_TIMESTAMP);
        assertThat(testFinanceTransactions.getAmountAddedToDestinationAccount()).isEqualTo(UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT);
        assertThat(testFinanceTransactions.getComment()).isEqualTo(DEFAULT_COMMENT);
    }

    @Test
    @Transactional
    void fullUpdateFinanceTransactionsWithPatch() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        int databaseSizeBeforeUpdate = financeTransactionsRepository.findAll().size();

        // Update the financeTransactions using partial update
        FinanceTransactions partialUpdatedFinanceTransactions = new FinanceTransactions();
        partialUpdatedFinanceTransactions.setId(financeTransactions.getId());

        partialUpdatedFinanceTransactions
            .executionTimestamp(UPDATED_EXECUTION_TIMESTAMP)
            .amountAddedToDestinationAccount(UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT)
            .comment(UPDATED_COMMENT);

        restFinanceTransactionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFinanceTransactions.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFinanceTransactions))
            )
            .andExpect(status().isOk());

        // Validate the FinanceTransactions in the database
        List<FinanceTransactions> financeTransactionsList = financeTransactionsRepository.findAll();
        assertThat(financeTransactionsList).hasSize(databaseSizeBeforeUpdate);
        FinanceTransactions testFinanceTransactions = financeTransactionsList.get(financeTransactionsList.size() - 1);
        assertThat(testFinanceTransactions.getExecutionTimestamp()).isEqualTo(UPDATED_EXECUTION_TIMESTAMP);
        assertThat(testFinanceTransactions.getAmountAddedToDestinationAccount()).isEqualTo(UPDATED_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT);
        assertThat(testFinanceTransactions.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void patchNonExistingFinanceTransactions() throws Exception {
        int databaseSizeBeforeUpdate = financeTransactionsRepository.findAll().size();
        financeTransactions.setId(UUID.randomUUID().toString());

        // Create the FinanceTransactions
        FinanceTransactionsDTO financeTransactionsDTO = financeTransactionsMapper.toDto(financeTransactions);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFinanceTransactionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, financeTransactionsDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(financeTransactionsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceTransactions in the database
        List<FinanceTransactions> financeTransactionsList = financeTransactionsRepository.findAll();
        assertThat(financeTransactionsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FinanceTransactions in Elasticsearch
        verify(mockFinanceTransactionsSearchRepository, times(0)).save(financeTransactions);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFinanceTransactions() throws Exception {
        int databaseSizeBeforeUpdate = financeTransactionsRepository.findAll().size();
        financeTransactions.setId(UUID.randomUUID().toString());

        // Create the FinanceTransactions
        FinanceTransactionsDTO financeTransactionsDTO = financeTransactionsMapper.toDto(financeTransactions);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinanceTransactionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(financeTransactionsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceTransactions in the database
        List<FinanceTransactions> financeTransactionsList = financeTransactionsRepository.findAll();
        assertThat(financeTransactionsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FinanceTransactions in Elasticsearch
        verify(mockFinanceTransactionsSearchRepository, times(0)).save(financeTransactions);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFinanceTransactions() throws Exception {
        int databaseSizeBeforeUpdate = financeTransactionsRepository.findAll().size();
        financeTransactions.setId(UUID.randomUUID().toString());

        // Create the FinanceTransactions
        FinanceTransactionsDTO financeTransactionsDTO = financeTransactionsMapper.toDto(financeTransactions);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinanceTransactionsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(financeTransactionsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FinanceTransactions in the database
        List<FinanceTransactions> financeTransactionsList = financeTransactionsRepository.findAll();
        assertThat(financeTransactionsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FinanceTransactions in Elasticsearch
        verify(mockFinanceTransactionsSearchRepository, times(0)).save(financeTransactions);
    }

    @Test
    @Transactional
    void deleteFinanceTransactions() throws Exception {
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);

        int databaseSizeBeforeDelete = financeTransactionsRepository.findAll().size();

        // Delete the financeTransactions
        restFinanceTransactionsMockMvc
            .perform(delete(ENTITY_API_URL_ID, financeTransactions.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FinanceTransactions> financeTransactionsList = financeTransactionsRepository.findAll();
        assertThat(financeTransactionsList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the FinanceTransactions in Elasticsearch
        verify(mockFinanceTransactionsSearchRepository, times(1)).deleteById(financeTransactions.getId());
    }

    @Test
    @Transactional
    void searchFinanceTransactions() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        financeTransactionsRepository.saveAndFlush(financeTransactions);
        when(mockFinanceTransactionsSearchRepository.search("id:" + financeTransactions.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(financeTransactions), PageRequest.of(0, 1), 1));

        // Search the financeTransactions
        restFinanceTransactionsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + financeTransactions.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(financeTransactions.getId())))
            .andExpect(jsonPath("$.[*].executionTimestamp").value(hasItem(sameInstant(DEFAULT_EXECUTION_TIMESTAMP))))
            .andExpect(
                jsonPath("$.[*].amountAddedToDestinationAccount").value(hasItem(DEFAULT_AMOUNT_ADDED_TO_DESTINATION_ACCOUNT.doubleValue()))
            )
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)));
    }
}
