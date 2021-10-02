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
import org.codingspiderfox.domain.ProjectMemberRole;
import org.codingspiderfox.domain.enumeration.ProjectMemberRoleEnum;
import org.codingspiderfox.repository.ProjectMemberRoleRepository;
import org.codingspiderfox.repository.search.ProjectMemberRoleSearchRepository;
import org.codingspiderfox.service.criteria.ProjectMemberRoleCriteria;
import org.codingspiderfox.service.dto.ProjectMemberRoleDTO;
import org.codingspiderfox.service.mapper.ProjectMemberRoleMapper;
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
 * Integration tests for the {@link ProjectMemberRoleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProjectMemberRoleResourceIT {

    private static final ZonedDateTime DEFAULT_CREATED_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_TIMESTAMP = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ProjectMemberRoleEnum DEFAULT_PROJECT_MEMBER_ROLE = ProjectMemberRoleEnum.Y;
    private static final ProjectMemberRoleEnum UPDATED_PROJECT_MEMBER_ROLE = ProjectMemberRoleEnum.Y;

    private static final String ENTITY_API_URL = "/api/project-member-roles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/project-member-roles";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjectMemberRoleRepository projectMemberRoleRepository;

    @Autowired
    private ProjectMemberRoleMapper projectMemberRoleMapper;

    /**
     * This repository is mocked in the org.codingspiderfox.repository.search test package.
     *
     * @see org.codingspiderfox.repository.search.ProjectMemberRoleSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProjectMemberRoleSearchRepository mockProjectMemberRoleSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjectMemberRoleMockMvc;

    private ProjectMemberRole projectMemberRole;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectMemberRole createEntity(EntityManager em) {
        ProjectMemberRole projectMemberRole = new ProjectMemberRole()
            .createdTimestamp(DEFAULT_CREATED_TIMESTAMP)
            .projectMemberRole(DEFAULT_PROJECT_MEMBER_ROLE);
        return projectMemberRole;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectMemberRole createUpdatedEntity(EntityManager em) {
        ProjectMemberRole projectMemberRole = new ProjectMemberRole()
            .createdTimestamp(UPDATED_CREATED_TIMESTAMP)
            .projectMemberRole(UPDATED_PROJECT_MEMBER_ROLE);
        return projectMemberRole;
    }

    @BeforeEach
    public void initTest() {
        projectMemberRole = createEntity(em);
    }

    @Test
    @Transactional
    void createProjectMemberRole() throws Exception {
        int databaseSizeBeforeCreate = projectMemberRoleRepository.findAll().size();
        // Create the ProjectMemberRole
        ProjectMemberRoleDTO projectMemberRoleDTO = projectMemberRoleMapper.toDto(projectMemberRole);
        restProjectMemberRoleMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ProjectMemberRole in the database
        List<ProjectMemberRole> projectMemberRoleList = projectMemberRoleRepository.findAll();
        assertThat(projectMemberRoleList).hasSize(databaseSizeBeforeCreate + 1);
        ProjectMemberRole testProjectMemberRole = projectMemberRoleList.get(projectMemberRoleList.size() - 1);
        assertThat(testProjectMemberRole.getCreatedTimestamp()).isEqualTo(DEFAULT_CREATED_TIMESTAMP);
        assertThat(testProjectMemberRole.getProjectMemberRole()).isEqualTo(DEFAULT_PROJECT_MEMBER_ROLE);

        // Validate the ProjectMemberRole in Elasticsearch
        verify(mockProjectMemberRoleSearchRepository, times(1)).save(testProjectMemberRole);
    }

    @Test
    @Transactional
    void createProjectMemberRoleWithExistingId() throws Exception {
        // Create the ProjectMemberRole with an existing ID
        projectMemberRole.setId(1L);
        ProjectMemberRoleDTO projectMemberRoleDTO = projectMemberRoleMapper.toDto(projectMemberRole);

        int databaseSizeBeforeCreate = projectMemberRoleRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectMemberRoleMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberRole in the database
        List<ProjectMemberRole> projectMemberRoleList = projectMemberRoleRepository.findAll();
        assertThat(projectMemberRoleList).hasSize(databaseSizeBeforeCreate);

        // Validate the ProjectMemberRole in Elasticsearch
        verify(mockProjectMemberRoleSearchRepository, times(0)).save(projectMemberRole);
    }

    @Test
    @Transactional
    void checkCreatedTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectMemberRoleRepository.findAll().size();
        // set the field null
        projectMemberRole.setCreatedTimestamp(null);

        // Create the ProjectMemberRole, which fails.
        ProjectMemberRoleDTO projectMemberRoleDTO = projectMemberRoleMapper.toDto(projectMemberRole);

        restProjectMemberRoleMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleDTO))
            )
            .andExpect(status().isBadRequest());

        List<ProjectMemberRole> projectMemberRoleList = projectMemberRoleRepository.findAll();
        assertThat(projectMemberRoleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkProjectMemberRoleIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectMemberRoleRepository.findAll().size();
        // set the field null
        projectMemberRole.setProjectMemberRole(null);

        // Create the ProjectMemberRole, which fails.
        ProjectMemberRoleDTO projectMemberRoleDTO = projectMemberRoleMapper.toDto(projectMemberRole);

        restProjectMemberRoleMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleDTO))
            )
            .andExpect(status().isBadRequest());

        List<ProjectMemberRole> projectMemberRoleList = projectMemberRoleRepository.findAll();
        assertThat(projectMemberRoleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProjectMemberRoles() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        // Get all the projectMemberRoleList
        restProjectMemberRoleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMemberRole.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdTimestamp").value(hasItem(sameInstant(DEFAULT_CREATED_TIMESTAMP))))
            .andExpect(jsonPath("$.[*].projectMemberRole").value(hasItem(DEFAULT_PROJECT_MEMBER_ROLE.toString())));
    }

    @Test
    @Transactional
    void getProjectMemberRole() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        // Get the projectMemberRole
        restProjectMemberRoleMockMvc
            .perform(get(ENTITY_API_URL_ID, projectMemberRole.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(projectMemberRole.getId().intValue()))
            .andExpect(jsonPath("$.createdTimestamp").value(sameInstant(DEFAULT_CREATED_TIMESTAMP)))
            .andExpect(jsonPath("$.projectMemberRole").value(DEFAULT_PROJECT_MEMBER_ROLE.toString()));
    }

    @Test
    @Transactional
    void getProjectMemberRolesByIdFiltering() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        Long id = projectMemberRole.getId();

        defaultProjectMemberRoleShouldBeFound("id.equals=" + id);
        defaultProjectMemberRoleShouldNotBeFound("id.notEquals=" + id);

        defaultProjectMemberRoleShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProjectMemberRoleShouldNotBeFound("id.greaterThan=" + id);

        defaultProjectMemberRoleShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProjectMemberRoleShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProjectMemberRolesByCreatedTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        // Get all the projectMemberRoleList where createdTimestamp equals to DEFAULT_CREATED_TIMESTAMP
        defaultProjectMemberRoleShouldBeFound("createdTimestamp.equals=" + DEFAULT_CREATED_TIMESTAMP);

        // Get all the projectMemberRoleList where createdTimestamp equals to UPDATED_CREATED_TIMESTAMP
        defaultProjectMemberRoleShouldNotBeFound("createdTimestamp.equals=" + UPDATED_CREATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberRolesByCreatedTimestampIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        // Get all the projectMemberRoleList where createdTimestamp not equals to DEFAULT_CREATED_TIMESTAMP
        defaultProjectMemberRoleShouldNotBeFound("createdTimestamp.notEquals=" + DEFAULT_CREATED_TIMESTAMP);

        // Get all the projectMemberRoleList where createdTimestamp not equals to UPDATED_CREATED_TIMESTAMP
        defaultProjectMemberRoleShouldBeFound("createdTimestamp.notEquals=" + UPDATED_CREATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberRolesByCreatedTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        // Get all the projectMemberRoleList where createdTimestamp in DEFAULT_CREATED_TIMESTAMP or UPDATED_CREATED_TIMESTAMP
        defaultProjectMemberRoleShouldBeFound("createdTimestamp.in=" + DEFAULT_CREATED_TIMESTAMP + "," + UPDATED_CREATED_TIMESTAMP);

        // Get all the projectMemberRoleList where createdTimestamp equals to UPDATED_CREATED_TIMESTAMP
        defaultProjectMemberRoleShouldNotBeFound("createdTimestamp.in=" + UPDATED_CREATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberRolesByCreatedTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        // Get all the projectMemberRoleList where createdTimestamp is not null
        defaultProjectMemberRoleShouldBeFound("createdTimestamp.specified=true");

        // Get all the projectMemberRoleList where createdTimestamp is null
        defaultProjectMemberRoleShouldNotBeFound("createdTimestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectMemberRolesByCreatedTimestampIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        // Get all the projectMemberRoleList where createdTimestamp is greater than or equal to DEFAULT_CREATED_TIMESTAMP
        defaultProjectMemberRoleShouldBeFound("createdTimestamp.greaterThanOrEqual=" + DEFAULT_CREATED_TIMESTAMP);

        // Get all the projectMemberRoleList where createdTimestamp is greater than or equal to UPDATED_CREATED_TIMESTAMP
        defaultProjectMemberRoleShouldNotBeFound("createdTimestamp.greaterThanOrEqual=" + UPDATED_CREATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberRolesByCreatedTimestampIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        // Get all the projectMemberRoleList where createdTimestamp is less than or equal to DEFAULT_CREATED_TIMESTAMP
        defaultProjectMemberRoleShouldBeFound("createdTimestamp.lessThanOrEqual=" + DEFAULT_CREATED_TIMESTAMP);

        // Get all the projectMemberRoleList where createdTimestamp is less than or equal to SMALLER_CREATED_TIMESTAMP
        defaultProjectMemberRoleShouldNotBeFound("createdTimestamp.lessThanOrEqual=" + SMALLER_CREATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberRolesByCreatedTimestampIsLessThanSomething() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        // Get all the projectMemberRoleList where createdTimestamp is less than DEFAULT_CREATED_TIMESTAMP
        defaultProjectMemberRoleShouldNotBeFound("createdTimestamp.lessThan=" + DEFAULT_CREATED_TIMESTAMP);

        // Get all the projectMemberRoleList where createdTimestamp is less than UPDATED_CREATED_TIMESTAMP
        defaultProjectMemberRoleShouldBeFound("createdTimestamp.lessThan=" + UPDATED_CREATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberRolesByCreatedTimestampIsGreaterThanSomething() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        // Get all the projectMemberRoleList where createdTimestamp is greater than DEFAULT_CREATED_TIMESTAMP
        defaultProjectMemberRoleShouldNotBeFound("createdTimestamp.greaterThan=" + DEFAULT_CREATED_TIMESTAMP);

        // Get all the projectMemberRoleList where createdTimestamp is greater than SMALLER_CREATED_TIMESTAMP
        defaultProjectMemberRoleShouldBeFound("createdTimestamp.greaterThan=" + SMALLER_CREATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberRolesByProjectMemberRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        // Get all the projectMemberRoleList where projectMemberRole equals to DEFAULT_PROJECT_MEMBER_ROLE
        defaultProjectMemberRoleShouldBeFound("projectMemberRole.equals=" + DEFAULT_PROJECT_MEMBER_ROLE);

        // Get all the projectMemberRoleList where projectMemberRole equals to UPDATED_PROJECT_MEMBER_ROLE
        defaultProjectMemberRoleShouldNotBeFound("projectMemberRole.equals=" + UPDATED_PROJECT_MEMBER_ROLE);
    }

    @Test
    @Transactional
    void getAllProjectMemberRolesByProjectMemberRoleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        // Get all the projectMemberRoleList where projectMemberRole not equals to DEFAULT_PROJECT_MEMBER_ROLE
        defaultProjectMemberRoleShouldNotBeFound("projectMemberRole.notEquals=" + DEFAULT_PROJECT_MEMBER_ROLE);

        // Get all the projectMemberRoleList where projectMemberRole not equals to UPDATED_PROJECT_MEMBER_ROLE
        defaultProjectMemberRoleShouldBeFound("projectMemberRole.notEquals=" + UPDATED_PROJECT_MEMBER_ROLE);
    }

    @Test
    @Transactional
    void getAllProjectMemberRolesByProjectMemberRoleIsInShouldWork() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        // Get all the projectMemberRoleList where projectMemberRole in DEFAULT_PROJECT_MEMBER_ROLE or UPDATED_PROJECT_MEMBER_ROLE
        defaultProjectMemberRoleShouldBeFound("projectMemberRole.in=" + DEFAULT_PROJECT_MEMBER_ROLE + "," + UPDATED_PROJECT_MEMBER_ROLE);

        // Get all the projectMemberRoleList where projectMemberRole equals to UPDATED_PROJECT_MEMBER_ROLE
        defaultProjectMemberRoleShouldNotBeFound("projectMemberRole.in=" + UPDATED_PROJECT_MEMBER_ROLE);
    }

    @Test
    @Transactional
    void getAllProjectMemberRolesByProjectMemberRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        // Get all the projectMemberRoleList where projectMemberRole is not null
        defaultProjectMemberRoleShouldBeFound("projectMemberRole.specified=true");

        // Get all the projectMemberRoleList where projectMemberRole is null
        defaultProjectMemberRoleShouldNotBeFound("projectMemberRole.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectMemberRoleShouldBeFound(String filter) throws Exception {
        restProjectMemberRoleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMemberRole.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdTimestamp").value(hasItem(sameInstant(DEFAULT_CREATED_TIMESTAMP))))
            .andExpect(jsonPath("$.[*].projectMemberRole").value(hasItem(DEFAULT_PROJECT_MEMBER_ROLE.toString())));

        // Check, that the count call also returns 1
        restProjectMemberRoleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProjectMemberRoleShouldNotBeFound(String filter) throws Exception {
        restProjectMemberRoleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProjectMemberRoleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProjectMemberRole() throws Exception {
        // Get the projectMemberRole
        restProjectMemberRoleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProjectMemberRole() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        int databaseSizeBeforeUpdate = projectMemberRoleRepository.findAll().size();

        // Update the projectMemberRole
        ProjectMemberRole updatedProjectMemberRole = projectMemberRoleRepository.findById(projectMemberRole.getId()).get();
        // Disconnect from session so that the updates on updatedProjectMemberRole are not directly saved in db
        em.detach(updatedProjectMemberRole);
        updatedProjectMemberRole.createdTimestamp(UPDATED_CREATED_TIMESTAMP).projectMemberRole(UPDATED_PROJECT_MEMBER_ROLE);
        ProjectMemberRoleDTO projectMemberRoleDTO = projectMemberRoleMapper.toDto(updatedProjectMemberRole);

        restProjectMemberRoleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectMemberRoleDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMemberRole in the database
        List<ProjectMemberRole> projectMemberRoleList = projectMemberRoleRepository.findAll();
        assertThat(projectMemberRoleList).hasSize(databaseSizeBeforeUpdate);
        ProjectMemberRole testProjectMemberRole = projectMemberRoleList.get(projectMemberRoleList.size() - 1);
        assertThat(testProjectMemberRole.getCreatedTimestamp()).isEqualTo(UPDATED_CREATED_TIMESTAMP);
        assertThat(testProjectMemberRole.getProjectMemberRole()).isEqualTo(UPDATED_PROJECT_MEMBER_ROLE);

        // Validate the ProjectMemberRole in Elasticsearch
        verify(mockProjectMemberRoleSearchRepository).save(testProjectMemberRole);
    }

    @Test
    @Transactional
    void putNonExistingProjectMemberRole() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRoleRepository.findAll().size();
        projectMemberRole.setId(count.incrementAndGet());

        // Create the ProjectMemberRole
        ProjectMemberRoleDTO projectMemberRoleDTO = projectMemberRoleMapper.toDto(projectMemberRole);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMemberRoleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectMemberRoleDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberRole in the database
        List<ProjectMemberRole> projectMemberRoleList = projectMemberRoleRepository.findAll();
        assertThat(projectMemberRoleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberRole in Elasticsearch
        verify(mockProjectMemberRoleSearchRepository, times(0)).save(projectMemberRole);
    }

    @Test
    @Transactional
    void putWithIdMismatchProjectMemberRole() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRoleRepository.findAll().size();
        projectMemberRole.setId(count.incrementAndGet());

        // Create the ProjectMemberRole
        ProjectMemberRoleDTO projectMemberRoleDTO = projectMemberRoleMapper.toDto(projectMemberRole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberRoleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberRole in the database
        List<ProjectMemberRole> projectMemberRoleList = projectMemberRoleRepository.findAll();
        assertThat(projectMemberRoleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberRole in Elasticsearch
        verify(mockProjectMemberRoleSearchRepository, times(0)).save(projectMemberRole);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProjectMemberRole() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRoleRepository.findAll().size();
        projectMemberRole.setId(count.incrementAndGet());

        // Create the ProjectMemberRole
        ProjectMemberRoleDTO projectMemberRoleDTO = projectMemberRoleMapper.toDto(projectMemberRole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberRoleMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectMemberRole in the database
        List<ProjectMemberRole> projectMemberRoleList = projectMemberRoleRepository.findAll();
        assertThat(projectMemberRoleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberRole in Elasticsearch
        verify(mockProjectMemberRoleSearchRepository, times(0)).save(projectMemberRole);
    }

    @Test
    @Transactional
    void partialUpdateProjectMemberRoleWithPatch() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        int databaseSizeBeforeUpdate = projectMemberRoleRepository.findAll().size();

        // Update the projectMemberRole using partial update
        ProjectMemberRole partialUpdatedProjectMemberRole = new ProjectMemberRole();
        partialUpdatedProjectMemberRole.setId(projectMemberRole.getId());

        restProjectMemberRoleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectMemberRole.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectMemberRole))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMemberRole in the database
        List<ProjectMemberRole> projectMemberRoleList = projectMemberRoleRepository.findAll();
        assertThat(projectMemberRoleList).hasSize(databaseSizeBeforeUpdate);
        ProjectMemberRole testProjectMemberRole = projectMemberRoleList.get(projectMemberRoleList.size() - 1);
        assertThat(testProjectMemberRole.getCreatedTimestamp()).isEqualTo(DEFAULT_CREATED_TIMESTAMP);
        assertThat(testProjectMemberRole.getProjectMemberRole()).isEqualTo(DEFAULT_PROJECT_MEMBER_ROLE);
    }

    @Test
    @Transactional
    void fullUpdateProjectMemberRoleWithPatch() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        int databaseSizeBeforeUpdate = projectMemberRoleRepository.findAll().size();

        // Update the projectMemberRole using partial update
        ProjectMemberRole partialUpdatedProjectMemberRole = new ProjectMemberRole();
        partialUpdatedProjectMemberRole.setId(projectMemberRole.getId());

        partialUpdatedProjectMemberRole.createdTimestamp(UPDATED_CREATED_TIMESTAMP).projectMemberRole(UPDATED_PROJECT_MEMBER_ROLE);

        restProjectMemberRoleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectMemberRole.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectMemberRole))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMemberRole in the database
        List<ProjectMemberRole> projectMemberRoleList = projectMemberRoleRepository.findAll();
        assertThat(projectMemberRoleList).hasSize(databaseSizeBeforeUpdate);
        ProjectMemberRole testProjectMemberRole = projectMemberRoleList.get(projectMemberRoleList.size() - 1);
        assertThat(testProjectMemberRole.getCreatedTimestamp()).isEqualTo(UPDATED_CREATED_TIMESTAMP);
        assertThat(testProjectMemberRole.getProjectMemberRole()).isEqualTo(UPDATED_PROJECT_MEMBER_ROLE);
    }

    @Test
    @Transactional
    void patchNonExistingProjectMemberRole() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRoleRepository.findAll().size();
        projectMemberRole.setId(count.incrementAndGet());

        // Create the ProjectMemberRole
        ProjectMemberRoleDTO projectMemberRoleDTO = projectMemberRoleMapper.toDto(projectMemberRole);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMemberRoleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, projectMemberRoleDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberRole in the database
        List<ProjectMemberRole> projectMemberRoleList = projectMemberRoleRepository.findAll();
        assertThat(projectMemberRoleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberRole in Elasticsearch
        verify(mockProjectMemberRoleSearchRepository, times(0)).save(projectMemberRole);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProjectMemberRole() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRoleRepository.findAll().size();
        projectMemberRole.setId(count.incrementAndGet());

        // Create the ProjectMemberRole
        ProjectMemberRoleDTO projectMemberRoleDTO = projectMemberRoleMapper.toDto(projectMemberRole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberRoleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberRole in the database
        List<ProjectMemberRole> projectMemberRoleList = projectMemberRoleRepository.findAll();
        assertThat(projectMemberRoleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberRole in Elasticsearch
        verify(mockProjectMemberRoleSearchRepository, times(0)).save(projectMemberRole);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProjectMemberRole() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRoleRepository.findAll().size();
        projectMemberRole.setId(count.incrementAndGet());

        // Create the ProjectMemberRole
        ProjectMemberRoleDTO projectMemberRoleDTO = projectMemberRoleMapper.toDto(projectMemberRole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberRoleMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectMemberRole in the database
        List<ProjectMemberRole> projectMemberRoleList = projectMemberRoleRepository.findAll();
        assertThat(projectMemberRoleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberRole in Elasticsearch
        verify(mockProjectMemberRoleSearchRepository, times(0)).save(projectMemberRole);
    }

    @Test
    @Transactional
    void deleteProjectMemberRole() throws Exception {
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);

        int databaseSizeBeforeDelete = projectMemberRoleRepository.findAll().size();

        // Delete the projectMemberRole
        restProjectMemberRoleMockMvc
            .perform(delete(ENTITY_API_URL_ID, projectMemberRole.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProjectMemberRole> projectMemberRoleList = projectMemberRoleRepository.findAll();
        assertThat(projectMemberRoleList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ProjectMemberRole in Elasticsearch
        verify(mockProjectMemberRoleSearchRepository, times(1)).deleteById(projectMemberRole.getId());
    }

    @Test
    @Transactional
    void searchProjectMemberRole() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        projectMemberRoleRepository.saveAndFlush(projectMemberRole);
        when(mockProjectMemberRoleSearchRepository.search("id:" + projectMemberRole.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(projectMemberRole), PageRequest.of(0, 1), 1));

        // Search the projectMemberRole
        restProjectMemberRoleMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + projectMemberRole.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMemberRole.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdTimestamp").value(hasItem(sameInstant(DEFAULT_CREATED_TIMESTAMP))))
            .andExpect(jsonPath("$.[*].projectMemberRole").value(hasItem(DEFAULT_PROJECT_MEMBER_ROLE.toString())));
    }
}
