package org.codingspiderfox.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.codingspiderfox.IntegrationTest;
import org.codingspiderfox.domain.FinanceAccount;
import org.codingspiderfox.domain.User;
import org.codingspiderfox.repository.FinanceAccountRepository;
import org.codingspiderfox.repository.search.FinanceAccountSearchRepository;
import org.codingspiderfox.service.criteria.FinanceAccountCriteria;
import org.codingspiderfox.service.dto.FinanceAccountDTO;
import org.codingspiderfox.service.mapper.FinanceAccountMapper;
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
 * Integration tests for the {@link FinanceAccountResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FinanceAccountResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/finance-accounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/finance-accounts";

    @Autowired
    private FinanceAccountRepository financeAccountRepository;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    /**
     * This repository is mocked in the org.codingspiderfox.repository.search test package.
     *
     * @see org.codingspiderfox.repository.search.FinanceAccountSearchRepositoryMockConfiguration
     */
    @Autowired
    private FinanceAccountSearchRepository mockFinanceAccountSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFinanceAccountMockMvc;

    private FinanceAccount financeAccount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FinanceAccount createEntity(EntityManager em) {
        FinanceAccount financeAccount = new FinanceAccount().title(DEFAULT_TITLE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        financeAccount.setOwner(user);
        return financeAccount;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FinanceAccount createUpdatedEntity(EntityManager em) {
        FinanceAccount financeAccount = new FinanceAccount().title(UPDATED_TITLE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        financeAccount.setOwner(user);
        return financeAccount;
    }

    @BeforeEach
    public void initTest() {
        financeAccount = createEntity(em);
    }

    @Test
    @Transactional
    void createFinanceAccount() throws Exception {
        int databaseSizeBeforeCreate = financeAccountRepository.findAll().size();
        // Create the FinanceAccount
        FinanceAccountDTO financeAccountDTO = financeAccountMapper.toDto(financeAccount);
        restFinanceAccountMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeAccountDTO))
            )
            .andExpect(status().isCreated());

        // Validate the FinanceAccount in the database
        List<FinanceAccount> financeAccountList = financeAccountRepository.findAll();
        assertThat(financeAccountList).hasSize(databaseSizeBeforeCreate + 1);
        FinanceAccount testFinanceAccount = financeAccountList.get(financeAccountList.size() - 1);
        assertThat(testFinanceAccount.getTitle()).isEqualTo(DEFAULT_TITLE);

        // Validate the id for MapsId, the ids must be same
        assertThat(testFinanceAccount.getId()).isEqualTo(testFinanceAccount.getUser().getId());

        // Validate the FinanceAccount in Elasticsearch
        verify(mockFinanceAccountSearchRepository, times(1)).save(testFinanceAccount);
    }

    @Test
    @Transactional
    void createFinanceAccountWithExistingId() throws Exception {
        // Create the FinanceAccount with an existing ID
        financeAccount.setId("existing_id");
        FinanceAccountDTO financeAccountDTO = financeAccountMapper.toDto(financeAccount);

        int databaseSizeBeforeCreate = financeAccountRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFinanceAccountMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceAccount in the database
        List<FinanceAccount> financeAccountList = financeAccountRepository.findAll();
        assertThat(financeAccountList).hasSize(databaseSizeBeforeCreate);

        // Validate the FinanceAccount in Elasticsearch
        verify(mockFinanceAccountSearchRepository, times(0)).save(financeAccount);
    }

    @Test
    @Transactional
    void updateFinanceAccountMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        financeAccountRepository.saveAndFlush(financeAccount);
        int databaseSizeBeforeCreate = financeAccountRepository.findAll().size();

        // Add a new parent entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();

        // Load the financeAccount
        FinanceAccount updatedFinanceAccount = financeAccountRepository.findById(financeAccount.getId()).get();
        assertThat(updatedFinanceAccount).isNotNull();
        // Disconnect from session so that the updates on updatedFinanceAccount are not directly saved in db
        em.detach(updatedFinanceAccount);

        // Update the User with new association value
        updatedFinanceAccount.setUser(user);
        FinanceAccountDTO updatedFinanceAccountDTO = financeAccountMapper.toDto(updatedFinanceAccount);
        assertThat(updatedFinanceAccountDTO).isNotNull();

        // Update the entity
        restFinanceAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFinanceAccountDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFinanceAccountDTO))
            )
            .andExpect(status().isOk());

        // Validate the FinanceAccount in the database
        List<FinanceAccount> financeAccountList = financeAccountRepository.findAll();
        assertThat(financeAccountList).hasSize(databaseSizeBeforeCreate);
        FinanceAccount testFinanceAccount = financeAccountList.get(financeAccountList.size() - 1);

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testFinanceAccount.getId()).isEqualTo(testFinanceAccount.getUser().getId());

        // Validate the FinanceAccount in Elasticsearch
        verify(mockFinanceAccountSearchRepository).save(financeAccount);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = financeAccountRepository.findAll().size();
        // set the field null
        financeAccount.setTitle(null);

        // Create the FinanceAccount, which fails.
        FinanceAccountDTO financeAccountDTO = financeAccountMapper.toDto(financeAccount);

        restFinanceAccountMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeAccountDTO))
            )
            .andExpect(status().isBadRequest());

        List<FinanceAccount> financeAccountList = financeAccountRepository.findAll();
        assertThat(financeAccountList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFinanceAccounts() throws Exception {
        // Initialize the database
        financeAccountRepository.saveAndFlush(financeAccount);

        // Get all the financeAccountList
        restFinanceAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(financeAccount.getId())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)));
    }

    @Test
    @Transactional
    void getFinanceAccount() throws Exception {
        // Initialize the database
        financeAccountRepository.saveAndFlush(financeAccount);

        // Get the financeAccount
        restFinanceAccountMockMvc
            .perform(get(ENTITY_API_URL_ID, financeAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(financeAccount.getId()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE));
    }

    @Test
    @Transactional
    void getFinanceAccountsByIdFiltering() throws Exception {
        // Initialize the database
        financeAccountRepository.saveAndFlush(financeAccount);

        String id = financeAccount.getId();

        defaultFinanceAccountShouldBeFound("id.equals=" + id);
        defaultFinanceAccountShouldNotBeFound("id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllFinanceAccountsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        financeAccountRepository.saveAndFlush(financeAccount);

        // Get all the financeAccountList where title equals to DEFAULT_TITLE
        defaultFinanceAccountShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the financeAccountList where title equals to UPDATED_TITLE
        defaultFinanceAccountShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllFinanceAccountsByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        financeAccountRepository.saveAndFlush(financeAccount);

        // Get all the financeAccountList where title not equals to DEFAULT_TITLE
        defaultFinanceAccountShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the financeAccountList where title not equals to UPDATED_TITLE
        defaultFinanceAccountShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllFinanceAccountsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        financeAccountRepository.saveAndFlush(financeAccount);

        // Get all the financeAccountList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultFinanceAccountShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the financeAccountList where title equals to UPDATED_TITLE
        defaultFinanceAccountShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllFinanceAccountsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        financeAccountRepository.saveAndFlush(financeAccount);

        // Get all the financeAccountList where title is not null
        defaultFinanceAccountShouldBeFound("title.specified=true");

        // Get all the financeAccountList where title is null
        defaultFinanceAccountShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllFinanceAccountsByTitleContainsSomething() throws Exception {
        // Initialize the database
        financeAccountRepository.saveAndFlush(financeAccount);

        // Get all the financeAccountList where title contains DEFAULT_TITLE
        defaultFinanceAccountShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the financeAccountList where title contains UPDATED_TITLE
        defaultFinanceAccountShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllFinanceAccountsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        financeAccountRepository.saveAndFlush(financeAccount);

        // Get all the financeAccountList where title does not contain DEFAULT_TITLE
        defaultFinanceAccountShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the financeAccountList where title does not contain UPDATED_TITLE
        defaultFinanceAccountShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllFinanceAccountsByOwnerIsEqualToSomething() throws Exception {
        // Get already existing entity
        User owner = financeAccount.getOwner();
        financeAccountRepository.saveAndFlush(financeAccount);
        String ownerId = owner.getId();

        // Get all the financeAccountList where owner equals to ownerId
        defaultFinanceAccountShouldBeFound("ownerId.equals=" + ownerId);

        // Get all the financeAccountList where owner equals to "invalid-id"
        defaultFinanceAccountShouldNotBeFound("ownerId.equals=" + "invalid-id");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFinanceAccountShouldBeFound(String filter) throws Exception {
        restFinanceAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(financeAccount.getId())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)));

        // Check, that the count call also returns 1
        restFinanceAccountMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFinanceAccountShouldNotBeFound(String filter) throws Exception {
        restFinanceAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFinanceAccountMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFinanceAccount() throws Exception {
        // Get the financeAccount
        restFinanceAccountMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFinanceAccount() throws Exception {
        // Initialize the database
        financeAccountRepository.saveAndFlush(financeAccount);

        int databaseSizeBeforeUpdate = financeAccountRepository.findAll().size();

        // Update the financeAccount
        FinanceAccount updatedFinanceAccount = financeAccountRepository.findById(financeAccount.getId()).get();
        // Disconnect from session so that the updates on updatedFinanceAccount are not directly saved in db
        em.detach(updatedFinanceAccount);
        updatedFinanceAccount.title(UPDATED_TITLE);
        FinanceAccountDTO financeAccountDTO = financeAccountMapper.toDto(updatedFinanceAccount);

        restFinanceAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, financeAccountDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeAccountDTO))
            )
            .andExpect(status().isOk());

        // Validate the FinanceAccount in the database
        List<FinanceAccount> financeAccountList = financeAccountRepository.findAll();
        assertThat(financeAccountList).hasSize(databaseSizeBeforeUpdate);
        FinanceAccount testFinanceAccount = financeAccountList.get(financeAccountList.size() - 1);
        assertThat(testFinanceAccount.getTitle()).isEqualTo(UPDATED_TITLE);

        // Validate the FinanceAccount in Elasticsearch
        verify(mockFinanceAccountSearchRepository).save(testFinanceAccount);
    }

    @Test
    @Transactional
    void putNonExistingFinanceAccount() throws Exception {
        int databaseSizeBeforeUpdate = financeAccountRepository.findAll().size();
        financeAccount.setId(UUID.randomUUID().toString());

        // Create the FinanceAccount
        FinanceAccountDTO financeAccountDTO = financeAccountMapper.toDto(financeAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFinanceAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, financeAccountDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceAccount in the database
        List<FinanceAccount> financeAccountList = financeAccountRepository.findAll();
        assertThat(financeAccountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FinanceAccount in Elasticsearch
        verify(mockFinanceAccountSearchRepository, times(0)).save(financeAccount);
    }

    @Test
    @Transactional
    void putWithIdMismatchFinanceAccount() throws Exception {
        int databaseSizeBeforeUpdate = financeAccountRepository.findAll().size();
        financeAccount.setId(UUID.randomUUID().toString());

        // Create the FinanceAccount
        FinanceAccountDTO financeAccountDTO = financeAccountMapper.toDto(financeAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinanceAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceAccount in the database
        List<FinanceAccount> financeAccountList = financeAccountRepository.findAll();
        assertThat(financeAccountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FinanceAccount in Elasticsearch
        verify(mockFinanceAccountSearchRepository, times(0)).save(financeAccount);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFinanceAccount() throws Exception {
        int databaseSizeBeforeUpdate = financeAccountRepository.findAll().size();
        financeAccount.setId(UUID.randomUUID().toString());

        // Create the FinanceAccount
        FinanceAccountDTO financeAccountDTO = financeAccountMapper.toDto(financeAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinanceAccountMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeAccountDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FinanceAccount in the database
        List<FinanceAccount> financeAccountList = financeAccountRepository.findAll();
        assertThat(financeAccountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FinanceAccount in Elasticsearch
        verify(mockFinanceAccountSearchRepository, times(0)).save(financeAccount);
    }

    @Test
    @Transactional
    void partialUpdateFinanceAccountWithPatch() throws Exception {
        // Initialize the database
        financeAccountRepository.saveAndFlush(financeAccount);

        int databaseSizeBeforeUpdate = financeAccountRepository.findAll().size();

        // Update the financeAccount using partial update
        FinanceAccount partialUpdatedFinanceAccount = new FinanceAccount();
        partialUpdatedFinanceAccount.setId(financeAccount.getId());

        restFinanceAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFinanceAccount.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFinanceAccount))
            )
            .andExpect(status().isOk());

        // Validate the FinanceAccount in the database
        List<FinanceAccount> financeAccountList = financeAccountRepository.findAll();
        assertThat(financeAccountList).hasSize(databaseSizeBeforeUpdate);
        FinanceAccount testFinanceAccount = financeAccountList.get(financeAccountList.size() - 1);
        assertThat(testFinanceAccount.getTitle()).isEqualTo(DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void fullUpdateFinanceAccountWithPatch() throws Exception {
        // Initialize the database
        financeAccountRepository.saveAndFlush(financeAccount);

        int databaseSizeBeforeUpdate = financeAccountRepository.findAll().size();

        // Update the financeAccount using partial update
        FinanceAccount partialUpdatedFinanceAccount = new FinanceAccount();
        partialUpdatedFinanceAccount.setId(financeAccount.getId());

        partialUpdatedFinanceAccount.title(UPDATED_TITLE);

        restFinanceAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFinanceAccount.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFinanceAccount))
            )
            .andExpect(status().isOk());

        // Validate the FinanceAccount in the database
        List<FinanceAccount> financeAccountList = financeAccountRepository.findAll();
        assertThat(financeAccountList).hasSize(databaseSizeBeforeUpdate);
        FinanceAccount testFinanceAccount = financeAccountList.get(financeAccountList.size() - 1);
        assertThat(testFinanceAccount.getTitle()).isEqualTo(UPDATED_TITLE);
    }

    @Test
    @Transactional
    void patchNonExistingFinanceAccount() throws Exception {
        int databaseSizeBeforeUpdate = financeAccountRepository.findAll().size();
        financeAccount.setId(UUID.randomUUID().toString());

        // Create the FinanceAccount
        FinanceAccountDTO financeAccountDTO = financeAccountMapper.toDto(financeAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFinanceAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, financeAccountDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(financeAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceAccount in the database
        List<FinanceAccount> financeAccountList = financeAccountRepository.findAll();
        assertThat(financeAccountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FinanceAccount in Elasticsearch
        verify(mockFinanceAccountSearchRepository, times(0)).save(financeAccount);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFinanceAccount() throws Exception {
        int databaseSizeBeforeUpdate = financeAccountRepository.findAll().size();
        financeAccount.setId(UUID.randomUUID().toString());

        // Create the FinanceAccount
        FinanceAccountDTO financeAccountDTO = financeAccountMapper.toDto(financeAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinanceAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(financeAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceAccount in the database
        List<FinanceAccount> financeAccountList = financeAccountRepository.findAll();
        assertThat(financeAccountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FinanceAccount in Elasticsearch
        verify(mockFinanceAccountSearchRepository, times(0)).save(financeAccount);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFinanceAccount() throws Exception {
        int databaseSizeBeforeUpdate = financeAccountRepository.findAll().size();
        financeAccount.setId(UUID.randomUUID().toString());

        // Create the FinanceAccount
        FinanceAccountDTO financeAccountDTO = financeAccountMapper.toDto(financeAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinanceAccountMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(financeAccountDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FinanceAccount in the database
        List<FinanceAccount> financeAccountList = financeAccountRepository.findAll();
        assertThat(financeAccountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FinanceAccount in Elasticsearch
        verify(mockFinanceAccountSearchRepository, times(0)).save(financeAccount);
    }

    @Test
    @Transactional
    void deleteFinanceAccount() throws Exception {
        // Initialize the database
        financeAccountRepository.saveAndFlush(financeAccount);

        int databaseSizeBeforeDelete = financeAccountRepository.findAll().size();

        // Delete the financeAccount
        restFinanceAccountMockMvc
            .perform(delete(ENTITY_API_URL_ID, financeAccount.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FinanceAccount> financeAccountList = financeAccountRepository.findAll();
        assertThat(financeAccountList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the FinanceAccount in Elasticsearch
        verify(mockFinanceAccountSearchRepository, times(1)).deleteById(financeAccount.getId());
    }

    @Test
    @Transactional
    void searchFinanceAccount() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        financeAccountRepository.saveAndFlush(financeAccount);
        when(mockFinanceAccountSearchRepository.search("id:" + financeAccount.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(financeAccount), PageRequest.of(0, 1), 1));

        // Search the financeAccount
        restFinanceAccountMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + financeAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(financeAccount.getId())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)));
    }
}
