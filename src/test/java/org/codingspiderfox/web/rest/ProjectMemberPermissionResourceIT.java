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
import org.codingspiderfox.domain.ProjectMemberPermission;
import org.codingspiderfox.domain.enumeration.ProjectMemberPermissionEnum;
import org.codingspiderfox.repository.ProjectMemberPermissionRepository;
import org.codingspiderfox.repository.search.ProjectMemberPermissionSearchRepository;
import org.codingspiderfox.service.criteria.ProjectMemberPermissionCriteria;
import org.codingspiderfox.service.dto.ProjectMemberPermissionDTO;
import org.codingspiderfox.service.mapper.ProjectMemberPermissionMapper;
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
 * Integration tests for the {@link ProjectMemberPermissionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProjectMemberPermissionResourceIT {

    private static final ZonedDateTime DEFAULT_CREATED_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_TIMESTAMP = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ProjectMemberPermissionEnum DEFAULT_PROJECT_MEMBER_PERMISSION = ProjectMemberPermissionEnum.CLOSE_PROJECT;
    private static final ProjectMemberPermissionEnum UPDATED_PROJECT_MEMBER_PERMISSION = ProjectMemberPermissionEnum.CLOSE_BILL;

    private static final String ENTITY_API_URL = "/api/project-member-permissions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/project-member-permissions";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjectMemberPermissionRepository projectMemberPermissionRepository;

    @Autowired
    private ProjectMemberPermissionMapper projectMemberPermissionMapper;

    /**
     * This repository is mocked in the org.codingspiderfox.repository.search test package.
     *
     * @see org.codingspiderfox.repository.search.ProjectMemberPermissionSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProjectMemberPermissionSearchRepository mockProjectMemberPermissionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjectMemberPermissionMockMvc;

    private ProjectMemberPermission projectMemberPermission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectMemberPermission createEntity(EntityManager em) {
        ProjectMemberPermission projectMemberPermission = new ProjectMemberPermission()
            .createdTimestamp(DEFAULT_CREATED_TIMESTAMP)
            .projectMemberPermission(DEFAULT_PROJECT_MEMBER_PERMISSION);
        return projectMemberPermission;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectMemberPermission createUpdatedEntity(EntityManager em) {
        ProjectMemberPermission projectMemberPermission = new ProjectMemberPermission()
            .createdTimestamp(UPDATED_CREATED_TIMESTAMP)
            .projectMemberPermission(UPDATED_PROJECT_MEMBER_PERMISSION);
        return projectMemberPermission;
    }

    @BeforeEach
    public void initTest() {
        projectMemberPermission = createEntity(em);
    }

    @Test
    @Transactional
    void createProjectMemberPermission() throws Exception {
        int databaseSizeBeforeCreate = projectMemberPermissionRepository.findAll().size();
        // Create the ProjectMemberPermission
        ProjectMemberPermissionDTO projectMemberPermissionDTO = projectMemberPermissionMapper.toDto(projectMemberPermission);
        restProjectMemberPermissionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ProjectMemberPermission in the database
        List<ProjectMemberPermission> projectMemberPermissionList = projectMemberPermissionRepository.findAll();
        assertThat(projectMemberPermissionList).hasSize(databaseSizeBeforeCreate + 1);
        ProjectMemberPermission testProjectMemberPermission = projectMemberPermissionList.get(projectMemberPermissionList.size() - 1);
        assertThat(testProjectMemberPermission.getCreatedTimestamp()).isEqualTo(DEFAULT_CREATED_TIMESTAMP);
        assertThat(testProjectMemberPermission.getProjectMemberPermission()).isEqualTo(DEFAULT_PROJECT_MEMBER_PERMISSION);

        // Validate the ProjectMemberPermission in Elasticsearch
        verify(mockProjectMemberPermissionSearchRepository, times(1)).save(testProjectMemberPermission);
    }

    @Test
    @Transactional
    void createProjectMemberPermissionWithExistingId() throws Exception {
        // Create the ProjectMemberPermission with an existing ID
        projectMemberPermission.setId(1L);
        ProjectMemberPermissionDTO projectMemberPermissionDTO = projectMemberPermissionMapper.toDto(projectMemberPermission);

        int databaseSizeBeforeCreate = projectMemberPermissionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectMemberPermissionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberPermission in the database
        List<ProjectMemberPermission> projectMemberPermissionList = projectMemberPermissionRepository.findAll();
        assertThat(projectMemberPermissionList).hasSize(databaseSizeBeforeCreate);

        // Validate the ProjectMemberPermission in Elasticsearch
        verify(mockProjectMemberPermissionSearchRepository, times(0)).save(projectMemberPermission);
    }

    @Test
    @Transactional
    void checkCreatedTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectMemberPermissionRepository.findAll().size();
        // set the field null
        projectMemberPermission.setCreatedTimestamp(null);

        // Create the ProjectMemberPermission, which fails.
        ProjectMemberPermissionDTO projectMemberPermissionDTO = projectMemberPermissionMapper.toDto(projectMemberPermission);

        restProjectMemberPermissionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        List<ProjectMemberPermission> projectMemberPermissionList = projectMemberPermissionRepository.findAll();
        assertThat(projectMemberPermissionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkProjectMemberPermissionIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectMemberPermissionRepository.findAll().size();
        // set the field null
        projectMemberPermission.setProjectMemberPermission(null);

        // Create the ProjectMemberPermission, which fails.
        ProjectMemberPermissionDTO projectMemberPermissionDTO = projectMemberPermissionMapper.toDto(projectMemberPermission);

        restProjectMemberPermissionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        List<ProjectMemberPermission> projectMemberPermissionList = projectMemberPermissionRepository.findAll();
        assertThat(projectMemberPermissionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissions() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        // Get all the projectMemberPermissionList
        restProjectMemberPermissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMemberPermission.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdTimestamp").value(hasItem(sameInstant(DEFAULT_CREATED_TIMESTAMP))))
            .andExpect(jsonPath("$.[*].projectMemberPermission").value(hasItem(DEFAULT_PROJECT_MEMBER_PERMISSION.toString())));
    }

    @Test
    @Transactional
    void getProjectMemberPermission() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        // Get the projectMemberPermission
        restProjectMemberPermissionMockMvc
            .perform(get(ENTITY_API_URL_ID, projectMemberPermission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(projectMemberPermission.getId().intValue()))
            .andExpect(jsonPath("$.createdTimestamp").value(sameInstant(DEFAULT_CREATED_TIMESTAMP)))
            .andExpect(jsonPath("$.projectMemberPermission").value(DEFAULT_PROJECT_MEMBER_PERMISSION.toString()));
    }

    @Test
    @Transactional
    void getProjectMemberPermissionsByIdFiltering() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        Long id = projectMemberPermission.getId();

        defaultProjectMemberPermissionShouldBeFound("id.equals=" + id);
        defaultProjectMemberPermissionShouldNotBeFound("id.notEquals=" + id);

        defaultProjectMemberPermissionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProjectMemberPermissionShouldNotBeFound("id.greaterThan=" + id);

        defaultProjectMemberPermissionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProjectMemberPermissionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionsByCreatedTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        // Get all the projectMemberPermissionList where createdTimestamp equals to DEFAULT_CREATED_TIMESTAMP
        defaultProjectMemberPermissionShouldBeFound("createdTimestamp.equals=" + DEFAULT_CREATED_TIMESTAMP);

        // Get all the projectMemberPermissionList where createdTimestamp equals to UPDATED_CREATED_TIMESTAMP
        defaultProjectMemberPermissionShouldNotBeFound("createdTimestamp.equals=" + UPDATED_CREATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionsByCreatedTimestampIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        // Get all the projectMemberPermissionList where createdTimestamp not equals to DEFAULT_CREATED_TIMESTAMP
        defaultProjectMemberPermissionShouldNotBeFound("createdTimestamp.notEquals=" + DEFAULT_CREATED_TIMESTAMP);

        // Get all the projectMemberPermissionList where createdTimestamp not equals to UPDATED_CREATED_TIMESTAMP
        defaultProjectMemberPermissionShouldBeFound("createdTimestamp.notEquals=" + UPDATED_CREATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionsByCreatedTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        // Get all the projectMemberPermissionList where createdTimestamp in DEFAULT_CREATED_TIMESTAMP or UPDATED_CREATED_TIMESTAMP
        defaultProjectMemberPermissionShouldBeFound("createdTimestamp.in=" + DEFAULT_CREATED_TIMESTAMP + "," + UPDATED_CREATED_TIMESTAMP);

        // Get all the projectMemberPermissionList where createdTimestamp equals to UPDATED_CREATED_TIMESTAMP
        defaultProjectMemberPermissionShouldNotBeFound("createdTimestamp.in=" + UPDATED_CREATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionsByCreatedTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        // Get all the projectMemberPermissionList where createdTimestamp is not null
        defaultProjectMemberPermissionShouldBeFound("createdTimestamp.specified=true");

        // Get all the projectMemberPermissionList where createdTimestamp is null
        defaultProjectMemberPermissionShouldNotBeFound("createdTimestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionsByCreatedTimestampIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        // Get all the projectMemberPermissionList where createdTimestamp is greater than or equal to DEFAULT_CREATED_TIMESTAMP
        defaultProjectMemberPermissionShouldBeFound("createdTimestamp.greaterThanOrEqual=" + DEFAULT_CREATED_TIMESTAMP);

        // Get all the projectMemberPermissionList where createdTimestamp is greater than or equal to UPDATED_CREATED_TIMESTAMP
        defaultProjectMemberPermissionShouldNotBeFound("createdTimestamp.greaterThanOrEqual=" + UPDATED_CREATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionsByCreatedTimestampIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        // Get all the projectMemberPermissionList where createdTimestamp is less than or equal to DEFAULT_CREATED_TIMESTAMP
        defaultProjectMemberPermissionShouldBeFound("createdTimestamp.lessThanOrEqual=" + DEFAULT_CREATED_TIMESTAMP);

        // Get all the projectMemberPermissionList where createdTimestamp is less than or equal to SMALLER_CREATED_TIMESTAMP
        defaultProjectMemberPermissionShouldNotBeFound("createdTimestamp.lessThanOrEqual=" + SMALLER_CREATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionsByCreatedTimestampIsLessThanSomething() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        // Get all the projectMemberPermissionList where createdTimestamp is less than DEFAULT_CREATED_TIMESTAMP
        defaultProjectMemberPermissionShouldNotBeFound("createdTimestamp.lessThan=" + DEFAULT_CREATED_TIMESTAMP);

        // Get all the projectMemberPermissionList where createdTimestamp is less than UPDATED_CREATED_TIMESTAMP
        defaultProjectMemberPermissionShouldBeFound("createdTimestamp.lessThan=" + UPDATED_CREATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionsByCreatedTimestampIsGreaterThanSomething() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        // Get all the projectMemberPermissionList where createdTimestamp is greater than DEFAULT_CREATED_TIMESTAMP
        defaultProjectMemberPermissionShouldNotBeFound("createdTimestamp.greaterThan=" + DEFAULT_CREATED_TIMESTAMP);

        // Get all the projectMemberPermissionList where createdTimestamp is greater than SMALLER_CREATED_TIMESTAMP
        defaultProjectMemberPermissionShouldBeFound("createdTimestamp.greaterThan=" + SMALLER_CREATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionsByProjectMemberPermissionIsEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        // Get all the projectMemberPermissionList where projectMemberPermission equals to DEFAULT_PROJECT_MEMBER_PERMISSION
        defaultProjectMemberPermissionShouldBeFound("projectMemberPermission.equals=" + DEFAULT_PROJECT_MEMBER_PERMISSION);

        // Get all the projectMemberPermissionList where projectMemberPermission equals to UPDATED_PROJECT_MEMBER_PERMISSION
        defaultProjectMemberPermissionShouldNotBeFound("projectMemberPermission.equals=" + UPDATED_PROJECT_MEMBER_PERMISSION);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionsByProjectMemberPermissionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        // Get all the projectMemberPermissionList where projectMemberPermission not equals to DEFAULT_PROJECT_MEMBER_PERMISSION
        defaultProjectMemberPermissionShouldNotBeFound("projectMemberPermission.notEquals=" + DEFAULT_PROJECT_MEMBER_PERMISSION);

        // Get all the projectMemberPermissionList where projectMemberPermission not equals to UPDATED_PROJECT_MEMBER_PERMISSION
        defaultProjectMemberPermissionShouldBeFound("projectMemberPermission.notEquals=" + UPDATED_PROJECT_MEMBER_PERMISSION);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionsByProjectMemberPermissionIsInShouldWork() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        // Get all the projectMemberPermissionList where projectMemberPermission in DEFAULT_PROJECT_MEMBER_PERMISSION or UPDATED_PROJECT_MEMBER_PERMISSION
        defaultProjectMemberPermissionShouldBeFound(
            "projectMemberPermission.in=" + DEFAULT_PROJECT_MEMBER_PERMISSION + "," + UPDATED_PROJECT_MEMBER_PERMISSION
        );

        // Get all the projectMemberPermissionList where projectMemberPermission equals to UPDATED_PROJECT_MEMBER_PERMISSION
        defaultProjectMemberPermissionShouldNotBeFound("projectMemberPermission.in=" + UPDATED_PROJECT_MEMBER_PERMISSION);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionsByProjectMemberPermissionIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        // Get all the projectMemberPermissionList where projectMemberPermission is not null
        defaultProjectMemberPermissionShouldBeFound("projectMemberPermission.specified=true");

        // Get all the projectMemberPermissionList where projectMemberPermission is null
        defaultProjectMemberPermissionShouldNotBeFound("projectMemberPermission.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectMemberPermissionShouldBeFound(String filter) throws Exception {
        restProjectMemberPermissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMemberPermission.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdTimestamp").value(hasItem(sameInstant(DEFAULT_CREATED_TIMESTAMP))))
            .andExpect(jsonPath("$.[*].projectMemberPermission").value(hasItem(DEFAULT_PROJECT_MEMBER_PERMISSION.toString())));

        // Check, that the count call also returns 1
        restProjectMemberPermissionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProjectMemberPermissionShouldNotBeFound(String filter) throws Exception {
        restProjectMemberPermissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProjectMemberPermissionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProjectMemberPermission() throws Exception {
        // Get the projectMemberPermission
        restProjectMemberPermissionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProjectMemberPermission() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        int databaseSizeBeforeUpdate = projectMemberPermissionRepository.findAll().size();

        // Update the projectMemberPermission
        ProjectMemberPermission updatedProjectMemberPermission = projectMemberPermissionRepository
            .findById(projectMemberPermission.getId())
            .get();
        // Disconnect from session so that the updates on updatedProjectMemberPermission are not directly saved in db
        em.detach(updatedProjectMemberPermission);
        updatedProjectMemberPermission
            .createdTimestamp(UPDATED_CREATED_TIMESTAMP)
            .projectMemberPermission(UPDATED_PROJECT_MEMBER_PERMISSION);
        ProjectMemberPermissionDTO projectMemberPermissionDTO = projectMemberPermissionMapper.toDto(updatedProjectMemberPermission);

        restProjectMemberPermissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectMemberPermissionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMemberPermission in the database
        List<ProjectMemberPermission> projectMemberPermissionList = projectMemberPermissionRepository.findAll();
        assertThat(projectMemberPermissionList).hasSize(databaseSizeBeforeUpdate);
        ProjectMemberPermission testProjectMemberPermission = projectMemberPermissionList.get(projectMemberPermissionList.size() - 1);
        assertThat(testProjectMemberPermission.getCreatedTimestamp()).isEqualTo(UPDATED_CREATED_TIMESTAMP);
        assertThat(testProjectMemberPermission.getProjectMemberPermission()).isEqualTo(UPDATED_PROJECT_MEMBER_PERMISSION);

        // Validate the ProjectMemberPermission in Elasticsearch
        verify(mockProjectMemberPermissionSearchRepository).save(testProjectMemberPermission);
    }

    @Test
    @Transactional
    void putNonExistingProjectMemberPermission() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberPermissionRepository.findAll().size();
        projectMemberPermission.setId(count.incrementAndGet());

        // Create the ProjectMemberPermission
        ProjectMemberPermissionDTO projectMemberPermissionDTO = projectMemberPermissionMapper.toDto(projectMemberPermission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMemberPermissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectMemberPermissionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberPermission in the database
        List<ProjectMemberPermission> projectMemberPermissionList = projectMemberPermissionRepository.findAll();
        assertThat(projectMemberPermissionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberPermission in Elasticsearch
        verify(mockProjectMemberPermissionSearchRepository, times(0)).save(projectMemberPermission);
    }

    @Test
    @Transactional
    void putWithIdMismatchProjectMemberPermission() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberPermissionRepository.findAll().size();
        projectMemberPermission.setId(count.incrementAndGet());

        // Create the ProjectMemberPermission
        ProjectMemberPermissionDTO projectMemberPermissionDTO = projectMemberPermissionMapper.toDto(projectMemberPermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberPermissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberPermission in the database
        List<ProjectMemberPermission> projectMemberPermissionList = projectMemberPermissionRepository.findAll();
        assertThat(projectMemberPermissionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberPermission in Elasticsearch
        verify(mockProjectMemberPermissionSearchRepository, times(0)).save(projectMemberPermission);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProjectMemberPermission() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberPermissionRepository.findAll().size();
        projectMemberPermission.setId(count.incrementAndGet());

        // Create the ProjectMemberPermission
        ProjectMemberPermissionDTO projectMemberPermissionDTO = projectMemberPermissionMapper.toDto(projectMemberPermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberPermissionMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectMemberPermission in the database
        List<ProjectMemberPermission> projectMemberPermissionList = projectMemberPermissionRepository.findAll();
        assertThat(projectMemberPermissionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberPermission in Elasticsearch
        verify(mockProjectMemberPermissionSearchRepository, times(0)).save(projectMemberPermission);
    }

    @Test
    @Transactional
    void partialUpdateProjectMemberPermissionWithPatch() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        int databaseSizeBeforeUpdate = projectMemberPermissionRepository.findAll().size();

        // Update the projectMemberPermission using partial update
        ProjectMemberPermission partialUpdatedProjectMemberPermission = new ProjectMemberPermission();
        partialUpdatedProjectMemberPermission.setId(projectMemberPermission.getId());

        partialUpdatedProjectMemberPermission.createdTimestamp(UPDATED_CREATED_TIMESTAMP);

        restProjectMemberPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectMemberPermission.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectMemberPermission))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMemberPermission in the database
        List<ProjectMemberPermission> projectMemberPermissionList = projectMemberPermissionRepository.findAll();
        assertThat(projectMemberPermissionList).hasSize(databaseSizeBeforeUpdate);
        ProjectMemberPermission testProjectMemberPermission = projectMemberPermissionList.get(projectMemberPermissionList.size() - 1);
        assertThat(testProjectMemberPermission.getCreatedTimestamp()).isEqualTo(UPDATED_CREATED_TIMESTAMP);
        assertThat(testProjectMemberPermission.getProjectMemberPermission()).isEqualTo(DEFAULT_PROJECT_MEMBER_PERMISSION);
    }

    @Test
    @Transactional
    void fullUpdateProjectMemberPermissionWithPatch() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        int databaseSizeBeforeUpdate = projectMemberPermissionRepository.findAll().size();

        // Update the projectMemberPermission using partial update
        ProjectMemberPermission partialUpdatedProjectMemberPermission = new ProjectMemberPermission();
        partialUpdatedProjectMemberPermission.setId(projectMemberPermission.getId());

        partialUpdatedProjectMemberPermission
            .createdTimestamp(UPDATED_CREATED_TIMESTAMP)
            .projectMemberPermission(UPDATED_PROJECT_MEMBER_PERMISSION);

        restProjectMemberPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectMemberPermission.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectMemberPermission))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMemberPermission in the database
        List<ProjectMemberPermission> projectMemberPermissionList = projectMemberPermissionRepository.findAll();
        assertThat(projectMemberPermissionList).hasSize(databaseSizeBeforeUpdate);
        ProjectMemberPermission testProjectMemberPermission = projectMemberPermissionList.get(projectMemberPermissionList.size() - 1);
        assertThat(testProjectMemberPermission.getCreatedTimestamp()).isEqualTo(UPDATED_CREATED_TIMESTAMP);
        assertThat(testProjectMemberPermission.getProjectMemberPermission()).isEqualTo(UPDATED_PROJECT_MEMBER_PERMISSION);
    }

    @Test
    @Transactional
    void patchNonExistingProjectMemberPermission() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberPermissionRepository.findAll().size();
        projectMemberPermission.setId(count.incrementAndGet());

        // Create the ProjectMemberPermission
        ProjectMemberPermissionDTO projectMemberPermissionDTO = projectMemberPermissionMapper.toDto(projectMemberPermission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMemberPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, projectMemberPermissionDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberPermission in the database
        List<ProjectMemberPermission> projectMemberPermissionList = projectMemberPermissionRepository.findAll();
        assertThat(projectMemberPermissionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberPermission in Elasticsearch
        verify(mockProjectMemberPermissionSearchRepository, times(0)).save(projectMemberPermission);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProjectMemberPermission() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberPermissionRepository.findAll().size();
        projectMemberPermission.setId(count.incrementAndGet());

        // Create the ProjectMemberPermission
        ProjectMemberPermissionDTO projectMemberPermissionDTO = projectMemberPermissionMapper.toDto(projectMemberPermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberPermission in the database
        List<ProjectMemberPermission> projectMemberPermissionList = projectMemberPermissionRepository.findAll();
        assertThat(projectMemberPermissionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberPermission in Elasticsearch
        verify(mockProjectMemberPermissionSearchRepository, times(0)).save(projectMemberPermission);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProjectMemberPermission() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberPermissionRepository.findAll().size();
        projectMemberPermission.setId(count.incrementAndGet());

        // Create the ProjectMemberPermission
        ProjectMemberPermissionDTO projectMemberPermissionDTO = projectMemberPermissionMapper.toDto(projectMemberPermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectMemberPermission in the database
        List<ProjectMemberPermission> projectMemberPermissionList = projectMemberPermissionRepository.findAll();
        assertThat(projectMemberPermissionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberPermission in Elasticsearch
        verify(mockProjectMemberPermissionSearchRepository, times(0)).save(projectMemberPermission);
    }

    @Test
    @Transactional
    void deleteProjectMemberPermission() throws Exception {
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);

        int databaseSizeBeforeDelete = projectMemberPermissionRepository.findAll().size();

        // Delete the projectMemberPermission
        restProjectMemberPermissionMockMvc
            .perform(delete(ENTITY_API_URL_ID, projectMemberPermission.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProjectMemberPermission> projectMemberPermissionList = projectMemberPermissionRepository.findAll();
        assertThat(projectMemberPermissionList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ProjectMemberPermission in Elasticsearch
        verify(mockProjectMemberPermissionSearchRepository, times(1)).deleteById(projectMemberPermission.getId());
    }

    @Test
    @Transactional
    void searchProjectMemberPermission() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        projectMemberPermissionRepository.saveAndFlush(projectMemberPermission);
        when(mockProjectMemberPermissionSearchRepository.search("id:" + projectMemberPermission.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(projectMemberPermission), PageRequest.of(0, 1), 1));

        // Search the projectMemberPermission
        restProjectMemberPermissionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + projectMemberPermission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMemberPermission.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdTimestamp").value(hasItem(sameInstant(DEFAULT_CREATED_TIMESTAMP))))
            .andExpect(jsonPath("$.[*].projectMemberPermission").value(hasItem(DEFAULT_PROJECT_MEMBER_PERMISSION.toString())));
    }
}
