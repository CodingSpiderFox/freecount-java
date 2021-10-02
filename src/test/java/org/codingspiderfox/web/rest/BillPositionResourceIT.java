package org.codingspiderfox.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.codingspiderfox.IntegrationTest;
import org.codingspiderfox.domain.Bill;
import org.codingspiderfox.domain.BillPosition;
import org.codingspiderfox.domain.Product;
import org.codingspiderfox.repository.BillPositionRepository;
import org.codingspiderfox.repository.search.BillPositionSearchRepository;
import org.codingspiderfox.service.dto.BillPositionDTO;
import org.codingspiderfox.service.mapper.BillPositionMapper;
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
 * Integration tests for the {@link BillPositionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BillPositionResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Double DEFAULT_COST = 1D;
    private static final Double UPDATED_COST = 2D;

    private static final Integer DEFAULT_ORDER = 1;
    private static final Integer UPDATED_ORDER = 2;

    private static final String ENTITY_API_URL = "/api/bill-positions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/bill-positions";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BillPositionRepository billPositionRepository;

    @Autowired
    private BillPositionMapper billPositionMapper;

    /**
     * This repository is mocked in the org.codingspiderfox.repository.search test package.
     *
     * @see org.codingspiderfox.repository.search.BillPositionSearchRepositoryMockConfiguration
     */
    @Autowired
    private BillPositionSearchRepository mockBillPositionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBillPositionMockMvc;

    private BillPosition billPosition;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BillPosition createEntity(EntityManager em) {
        BillPosition billPosition = new BillPosition().title(DEFAULT_TITLE).cost(DEFAULT_COST).order(DEFAULT_ORDER);
        // Add required entity
        Bill bill;
        if (TestUtil.findAll(em, Bill.class).isEmpty()) {
            bill = BillResourceIT.createEntity(em);
            em.persist(bill);
            em.flush();
        } else {
            bill = TestUtil.findAll(em, Bill.class).get(0);
        }
        billPosition.setBill(bill);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        billPosition.setProduct(product);
        return billPosition;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BillPosition createUpdatedEntity(EntityManager em) {
        BillPosition billPosition = new BillPosition().title(UPDATED_TITLE).cost(UPDATED_COST).order(UPDATED_ORDER);
        // Add required entity
        Bill bill;
        if (TestUtil.findAll(em, Bill.class).isEmpty()) {
            bill = BillResourceIT.createUpdatedEntity(em);
            em.persist(bill);
            em.flush();
        } else {
            bill = TestUtil.findAll(em, Bill.class).get(0);
        }
        billPosition.setBill(bill);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createUpdatedEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        billPosition.setProduct(product);
        return billPosition;
    }

    @BeforeEach
    public void initTest() {
        billPosition = createEntity(em);
    }

    @Test
    @Transactional
    void createBillPosition() throws Exception {
        int databaseSizeBeforeCreate = billPositionRepository.findAll().size();
        // Create the BillPosition
        BillPositionDTO billPositionDTO = billPositionMapper.toDto(billPosition);
        restBillPositionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(billPositionDTO))
            )
            .andExpect(status().isCreated());

        // Validate the BillPosition in the database
        List<BillPosition> billPositionList = billPositionRepository.findAll();
        assertThat(billPositionList).hasSize(databaseSizeBeforeCreate + 1);
        BillPosition testBillPosition = billPositionList.get(billPositionList.size() - 1);
        assertThat(testBillPosition.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBillPosition.getCost()).isEqualTo(DEFAULT_COST);
        assertThat(testBillPosition.getOrder()).isEqualTo(DEFAULT_ORDER);

        // Validate the id for MapsId, the ids must be same
        assertThat(testBillPosition.getId()).isEqualTo(testBillPosition.getProduct().getId());

        // Validate the BillPosition in Elasticsearch
        verify(mockBillPositionSearchRepository, times(1)).save(testBillPosition);
    }

    @Test
    @Transactional
    void createBillPositionWithExistingId() throws Exception {
        // Create the BillPosition with an existing ID
        billPosition.setId(1L);
        BillPositionDTO billPositionDTO = billPositionMapper.toDto(billPosition);

        int databaseSizeBeforeCreate = billPositionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBillPositionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(billPositionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BillPosition in the database
        List<BillPosition> billPositionList = billPositionRepository.findAll();
        assertThat(billPositionList).hasSize(databaseSizeBeforeCreate);

        // Validate the BillPosition in Elasticsearch
        verify(mockBillPositionSearchRepository, times(0)).save(billPosition);
    }

    @Test
    @Transactional
    void updateBillPositionMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        billPositionRepository.saveAndFlush(billPosition);
        int databaseSizeBeforeCreate = billPositionRepository.findAll().size();

        // Load the billPosition
        BillPosition updatedBillPosition = billPositionRepository.findById(billPosition.getId()).get();
        assertThat(updatedBillPosition).isNotNull();
        // Disconnect from session so that the updates on updatedBillPosition are not directly saved in db
        em.detach(updatedBillPosition);

        Product product = new Product();
        // Update the Product with new association value
        updatedBillPosition.setProduct(product);
        BillPositionDTO updatedBillPositionDTO = billPositionMapper.toDto(updatedBillPosition);
        assertThat(updatedBillPositionDTO).isNotNull();

        // Update the entity
        restBillPositionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBillPositionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBillPositionDTO))
            )
            .andExpect(status().isOk());

        // Validate the BillPosition in the database
        List<BillPosition> billPositionList = billPositionRepository.findAll();
        assertThat(billPositionList).hasSize(databaseSizeBeforeCreate);
        BillPosition testBillPosition = billPositionList.get(billPositionList.size() - 1);

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testBillPosition.getId()).isEqualTo(testBillPosition.getProduct().getId());

        // Validate the BillPosition in Elasticsearch
        verify(mockBillPositionSearchRepository).save(billPosition);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = billPositionRepository.findAll().size();
        // set the field null
        billPosition.setTitle(null);

        // Create the BillPosition, which fails.
        BillPositionDTO billPositionDTO = billPositionMapper.toDto(billPosition);

        restBillPositionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(billPositionDTO))
            )
            .andExpect(status().isBadRequest());

        List<BillPosition> billPositionList = billPositionRepository.findAll();
        assertThat(billPositionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCostIsRequired() throws Exception {
        int databaseSizeBeforeTest = billPositionRepository.findAll().size();
        // set the field null
        billPosition.setCost(null);

        // Create the BillPosition, which fails.
        BillPositionDTO billPositionDTO = billPositionMapper.toDto(billPosition);

        restBillPositionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(billPositionDTO))
            )
            .andExpect(status().isBadRequest());

        List<BillPosition> billPositionList = billPositionRepository.findAll();
        assertThat(billPositionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrderIsRequired() throws Exception {
        int databaseSizeBeforeTest = billPositionRepository.findAll().size();
        // set the field null
        billPosition.setOrder(null);

        // Create the BillPosition, which fails.
        BillPositionDTO billPositionDTO = billPositionMapper.toDto(billPosition);

        restBillPositionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(billPositionDTO))
            )
            .andExpect(status().isBadRequest());

        List<BillPosition> billPositionList = billPositionRepository.findAll();
        assertThat(billPositionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBillPositions() throws Exception {
        // Initialize the database
        billPositionRepository.saveAndFlush(billPosition);

        // Get all the billPositionList
        restBillPositionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(billPosition.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].cost").value(hasItem(DEFAULT_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)));
    }

    @Test
    @Transactional
    void getBillPosition() throws Exception {
        // Initialize the database
        billPositionRepository.saveAndFlush(billPosition);

        // Get the billPosition
        restBillPositionMockMvc
            .perform(get(ENTITY_API_URL_ID, billPosition.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(billPosition.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.cost").value(DEFAULT_COST.doubleValue()))
            .andExpect(jsonPath("$.order").value(DEFAULT_ORDER));
    }

    @Test
    @Transactional
    void getNonExistingBillPosition() throws Exception {
        // Get the billPosition
        restBillPositionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBillPosition() throws Exception {
        // Initialize the database
        billPositionRepository.saveAndFlush(billPosition);

        int databaseSizeBeforeUpdate = billPositionRepository.findAll().size();

        // Update the billPosition
        BillPosition updatedBillPosition = billPositionRepository.findById(billPosition.getId()).get();
        // Disconnect from session so that the updates on updatedBillPosition are not directly saved in db
        em.detach(updatedBillPosition);
        updatedBillPosition.title(UPDATED_TITLE).cost(UPDATED_COST).order(UPDATED_ORDER);
        BillPositionDTO billPositionDTO = billPositionMapper.toDto(updatedBillPosition);

        restBillPositionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, billPositionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(billPositionDTO))
            )
            .andExpect(status().isOk());

        // Validate the BillPosition in the database
        List<BillPosition> billPositionList = billPositionRepository.findAll();
        assertThat(billPositionList).hasSize(databaseSizeBeforeUpdate);
        BillPosition testBillPosition = billPositionList.get(billPositionList.size() - 1);
        assertThat(testBillPosition.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBillPosition.getCost()).isEqualTo(UPDATED_COST);
        assertThat(testBillPosition.getOrder()).isEqualTo(UPDATED_ORDER);

        // Validate the BillPosition in Elasticsearch
        verify(mockBillPositionSearchRepository).save(testBillPosition);
    }

    @Test
    @Transactional
    void putNonExistingBillPosition() throws Exception {
        int databaseSizeBeforeUpdate = billPositionRepository.findAll().size();
        billPosition.setId(count.incrementAndGet());

        // Create the BillPosition
        BillPositionDTO billPositionDTO = billPositionMapper.toDto(billPosition);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBillPositionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, billPositionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(billPositionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BillPosition in the database
        List<BillPosition> billPositionList = billPositionRepository.findAll();
        assertThat(billPositionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the BillPosition in Elasticsearch
        verify(mockBillPositionSearchRepository, times(0)).save(billPosition);
    }

    @Test
    @Transactional
    void putWithIdMismatchBillPosition() throws Exception {
        int databaseSizeBeforeUpdate = billPositionRepository.findAll().size();
        billPosition.setId(count.incrementAndGet());

        // Create the BillPosition
        BillPositionDTO billPositionDTO = billPositionMapper.toDto(billPosition);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBillPositionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(billPositionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BillPosition in the database
        List<BillPosition> billPositionList = billPositionRepository.findAll();
        assertThat(billPositionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the BillPosition in Elasticsearch
        verify(mockBillPositionSearchRepository, times(0)).save(billPosition);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBillPosition() throws Exception {
        int databaseSizeBeforeUpdate = billPositionRepository.findAll().size();
        billPosition.setId(count.incrementAndGet());

        // Create the BillPosition
        BillPositionDTO billPositionDTO = billPositionMapper.toDto(billPosition);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBillPositionMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(billPositionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BillPosition in the database
        List<BillPosition> billPositionList = billPositionRepository.findAll();
        assertThat(billPositionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the BillPosition in Elasticsearch
        verify(mockBillPositionSearchRepository, times(0)).save(billPosition);
    }

    @Test
    @Transactional
    void partialUpdateBillPositionWithPatch() throws Exception {
        // Initialize the database
        billPositionRepository.saveAndFlush(billPosition);

        int databaseSizeBeforeUpdate = billPositionRepository.findAll().size();

        // Update the billPosition using partial update
        BillPosition partialUpdatedBillPosition = new BillPosition();
        partialUpdatedBillPosition.setId(billPosition.getId());

        partialUpdatedBillPosition.order(UPDATED_ORDER);

        restBillPositionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBillPosition.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBillPosition))
            )
            .andExpect(status().isOk());

        // Validate the BillPosition in the database
        List<BillPosition> billPositionList = billPositionRepository.findAll();
        assertThat(billPositionList).hasSize(databaseSizeBeforeUpdate);
        BillPosition testBillPosition = billPositionList.get(billPositionList.size() - 1);
        assertThat(testBillPosition.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBillPosition.getCost()).isEqualTo(DEFAULT_COST);
        assertThat(testBillPosition.getOrder()).isEqualTo(UPDATED_ORDER);
    }

    @Test
    @Transactional
    void fullUpdateBillPositionWithPatch() throws Exception {
        // Initialize the database
        billPositionRepository.saveAndFlush(billPosition);

        int databaseSizeBeforeUpdate = billPositionRepository.findAll().size();

        // Update the billPosition using partial update
        BillPosition partialUpdatedBillPosition = new BillPosition();
        partialUpdatedBillPosition.setId(billPosition.getId());

        partialUpdatedBillPosition.title(UPDATED_TITLE).cost(UPDATED_COST).order(UPDATED_ORDER);

        restBillPositionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBillPosition.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBillPosition))
            )
            .andExpect(status().isOk());

        // Validate the BillPosition in the database
        List<BillPosition> billPositionList = billPositionRepository.findAll();
        assertThat(billPositionList).hasSize(databaseSizeBeforeUpdate);
        BillPosition testBillPosition = billPositionList.get(billPositionList.size() - 1);
        assertThat(testBillPosition.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBillPosition.getCost()).isEqualTo(UPDATED_COST);
        assertThat(testBillPosition.getOrder()).isEqualTo(UPDATED_ORDER);
    }

    @Test
    @Transactional
    void patchNonExistingBillPosition() throws Exception {
        int databaseSizeBeforeUpdate = billPositionRepository.findAll().size();
        billPosition.setId(count.incrementAndGet());

        // Create the BillPosition
        BillPositionDTO billPositionDTO = billPositionMapper.toDto(billPosition);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBillPositionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, billPositionDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(billPositionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BillPosition in the database
        List<BillPosition> billPositionList = billPositionRepository.findAll();
        assertThat(billPositionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the BillPosition in Elasticsearch
        verify(mockBillPositionSearchRepository, times(0)).save(billPosition);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBillPosition() throws Exception {
        int databaseSizeBeforeUpdate = billPositionRepository.findAll().size();
        billPosition.setId(count.incrementAndGet());

        // Create the BillPosition
        BillPositionDTO billPositionDTO = billPositionMapper.toDto(billPosition);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBillPositionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(billPositionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BillPosition in the database
        List<BillPosition> billPositionList = billPositionRepository.findAll();
        assertThat(billPositionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the BillPosition in Elasticsearch
        verify(mockBillPositionSearchRepository, times(0)).save(billPosition);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBillPosition() throws Exception {
        int databaseSizeBeforeUpdate = billPositionRepository.findAll().size();
        billPosition.setId(count.incrementAndGet());

        // Create the BillPosition
        BillPositionDTO billPositionDTO = billPositionMapper.toDto(billPosition);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBillPositionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(billPositionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BillPosition in the database
        List<BillPosition> billPositionList = billPositionRepository.findAll();
        assertThat(billPositionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the BillPosition in Elasticsearch
        verify(mockBillPositionSearchRepository, times(0)).save(billPosition);
    }

    @Test
    @Transactional
    void deleteBillPosition() throws Exception {
        // Initialize the database
        billPositionRepository.saveAndFlush(billPosition);

        int databaseSizeBeforeDelete = billPositionRepository.findAll().size();

        // Delete the billPosition
        restBillPositionMockMvc
            .perform(delete(ENTITY_API_URL_ID, billPosition.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BillPosition> billPositionList = billPositionRepository.findAll();
        assertThat(billPositionList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the BillPosition in Elasticsearch
        verify(mockBillPositionSearchRepository, times(1)).deleteById(billPosition.getId());
    }

    @Test
    @Transactional
    void searchBillPosition() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        billPositionRepository.saveAndFlush(billPosition);
        when(mockBillPositionSearchRepository.search("id:" + billPosition.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(billPosition), PageRequest.of(0, 1), 1));

        // Search the billPosition
        restBillPositionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + billPosition.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(billPosition.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].cost").value(hasItem(DEFAULT_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)));
    }
}
