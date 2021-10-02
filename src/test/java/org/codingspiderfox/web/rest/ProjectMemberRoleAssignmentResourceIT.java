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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.codingspiderfox.IntegrationTest;
import org.codingspiderfox.domain.ProjectMember;
import org.codingspiderfox.domain.ProjectMemberRole;
import org.codingspiderfox.domain.ProjectMemberRoleAssignment;
import org.codingspiderfox.repository.ProjectMemberRoleAssignmentRepository;
import org.codingspiderfox.repository.search.ProjectMemberRoleAssignmentSearchRepository;
import org.codingspiderfox.service.ProjectMemberRoleAssignmentService;
import org.codingspiderfox.service.criteria.ProjectMemberRoleAssignmentCriteria;
import org.codingspiderfox.service.dto.ProjectMemberRoleAssignmentDTO;
import org.codingspiderfox.service.mapper.ProjectMemberRoleAssignmentMapper;
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
 * Integration tests for the {@link ProjectMemberRoleAssignmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProjectMemberRoleAssignmentResourceIT {

    private static final ZonedDateTime DEFAULT_ASSIGNMENT_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ASSIGNMENT_TIMESTAMP = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_ASSIGNMENT_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/project-member-role-assignments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/project-member-role-assignments";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjectMemberRoleAssignmentRepository projectMemberRoleAssignmentRepository;

    @Mock
    private ProjectMemberRoleAssignmentRepository projectMemberRoleAssignmentRepositoryMock;

    @Autowired
    private ProjectMemberRoleAssignmentMapper projectMemberRoleAssignmentMapper;

    @Mock
    private ProjectMemberRoleAssignmentService projectMemberRoleAssignmentServiceMock;

    /**
     * This repository is mocked in the org.codingspiderfox.repository.search test package.
     *
     * @see org.codingspiderfox.repository.search.ProjectMemberRoleAssignmentSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProjectMemberRoleAssignmentSearchRepository mockProjectMemberRoleAssignmentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjectMemberRoleAssignmentMockMvc;

    private ProjectMemberRoleAssignment projectMemberRoleAssignment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectMemberRoleAssignment createEntity(EntityManager em) {
        ProjectMemberRoleAssignment projectMemberRoleAssignment = new ProjectMemberRoleAssignment()
            .assignmentTimestamp(DEFAULT_ASSIGNMENT_TIMESTAMP);
        // Add required entity
        ProjectMember projectMember;
        if (TestUtil.findAll(em, ProjectMember.class).isEmpty()) {
            projectMember = ProjectMemberResourceIT.createEntity(em);
            em.persist(projectMember);
            em.flush();
        } else {
            projectMember = TestUtil.findAll(em, ProjectMember.class).get(0);
        }
        projectMemberRoleAssignment.setProjectMember(projectMember);
        // Add required entity
        ProjectMemberRole projectMemberRole;
        if (TestUtil.findAll(em, ProjectMemberRole.class).isEmpty()) {
            projectMemberRole = ProjectMemberRoleResourceIT.createEntity(em);
            em.persist(projectMemberRole);
            em.flush();
        } else {
            projectMemberRole = TestUtil.findAll(em, ProjectMemberRole.class).get(0);
        }
        projectMemberRoleAssignment.getProjectMemberRoles().add(projectMemberRole);
        return projectMemberRoleAssignment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectMemberRoleAssignment createUpdatedEntity(EntityManager em) {
        ProjectMemberRoleAssignment projectMemberRoleAssignment = new ProjectMemberRoleAssignment()
            .assignmentTimestamp(UPDATED_ASSIGNMENT_TIMESTAMP);
        // Add required entity
        ProjectMember projectMember;
        if (TestUtil.findAll(em, ProjectMember.class).isEmpty()) {
            projectMember = ProjectMemberResourceIT.createUpdatedEntity(em);
            em.persist(projectMember);
            em.flush();
        } else {
            projectMember = TestUtil.findAll(em, ProjectMember.class).get(0);
        }
        projectMemberRoleAssignment.setProjectMember(projectMember);
        // Add required entity
        ProjectMemberRole projectMemberRole;
        if (TestUtil.findAll(em, ProjectMemberRole.class).isEmpty()) {
            projectMemberRole = ProjectMemberRoleResourceIT.createUpdatedEntity(em);
            em.persist(projectMemberRole);
            em.flush();
        } else {
            projectMemberRole = TestUtil.findAll(em, ProjectMemberRole.class).get(0);
        }
        projectMemberRoleAssignment.getProjectMemberRoles().add(projectMemberRole);
        return projectMemberRoleAssignment;
    }

    @BeforeEach
    public void initTest() {
        projectMemberRoleAssignment = createEntity(em);
    }

    @Test
    @Transactional
    void createProjectMemberRoleAssignment() throws Exception {
        int databaseSizeBeforeCreate = projectMemberRoleAssignmentRepository.findAll().size();
        // Create the ProjectMemberRoleAssignment
        ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO = projectMemberRoleAssignmentMapper.toDto(
            projectMemberRoleAssignment
        );
        restProjectMemberRoleAssignmentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleAssignmentDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ProjectMemberRoleAssignment in the database
        List<ProjectMemberRoleAssignment> projectMemberRoleAssignmentList = projectMemberRoleAssignmentRepository.findAll();
        assertThat(projectMemberRoleAssignmentList).hasSize(databaseSizeBeforeCreate + 1);
        ProjectMemberRoleAssignment testProjectMemberRoleAssignment = projectMemberRoleAssignmentList.get(
            projectMemberRoleAssignmentList.size() - 1
        );
        assertThat(testProjectMemberRoleAssignment.getAssignmentTimestamp()).isEqualTo(DEFAULT_ASSIGNMENT_TIMESTAMP);

        // Validate the id for MapsId, the ids must be same
        assertThat(testProjectMemberRoleAssignment.getId()).isEqualTo(testProjectMemberRoleAssignment.getProjectMember().getId());

        // Validate the ProjectMemberRoleAssignment in Elasticsearch
        verify(mockProjectMemberRoleAssignmentSearchRepository, times(1)).save(testProjectMemberRoleAssignment);
    }

    @Test
    @Transactional
    void createProjectMemberRoleAssignmentWithExistingId() throws Exception {
        // Create the ProjectMemberRoleAssignment with an existing ID
        projectMemberRoleAssignment.setId(1L);
        ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO = projectMemberRoleAssignmentMapper.toDto(
            projectMemberRoleAssignment
        );

        int databaseSizeBeforeCreate = projectMemberRoleAssignmentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectMemberRoleAssignmentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberRoleAssignment in the database
        List<ProjectMemberRoleAssignment> projectMemberRoleAssignmentList = projectMemberRoleAssignmentRepository.findAll();
        assertThat(projectMemberRoleAssignmentList).hasSize(databaseSizeBeforeCreate);

        // Validate the ProjectMemberRoleAssignment in Elasticsearch
        verify(mockProjectMemberRoleAssignmentSearchRepository, times(0)).save(projectMemberRoleAssignment);
    }

    @Test
    @Transactional
    void updateProjectMemberRoleAssignmentMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);
        int databaseSizeBeforeCreate = projectMemberRoleAssignmentRepository.findAll().size();

        // Add a new parent entity
        ProjectMember projectMember = ProjectMemberResourceIT.createUpdatedEntity(em);
        em.persist(projectMember);
        em.flush();

        // Load the projectMemberRoleAssignment
        ProjectMemberRoleAssignment updatedProjectMemberRoleAssignment = projectMemberRoleAssignmentRepository
            .findById(projectMemberRoleAssignment.getId())
            .get();
        assertThat(updatedProjectMemberRoleAssignment).isNotNull();
        // Disconnect from session so that the updates on updatedProjectMemberRoleAssignment are not directly saved in db
        em.detach(updatedProjectMemberRoleAssignment);

        // Update the ProjectMember with new association value
        updatedProjectMemberRoleAssignment.setProjectMember(projectMember);
        ProjectMemberRoleAssignmentDTO updatedProjectMemberRoleAssignmentDTO = projectMemberRoleAssignmentMapper.toDto(
            updatedProjectMemberRoleAssignment
        );
        assertThat(updatedProjectMemberRoleAssignmentDTO).isNotNull();

        // Update the entity
        restProjectMemberRoleAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProjectMemberRoleAssignmentDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProjectMemberRoleAssignmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMemberRoleAssignment in the database
        List<ProjectMemberRoleAssignment> projectMemberRoleAssignmentList = projectMemberRoleAssignmentRepository.findAll();
        assertThat(projectMemberRoleAssignmentList).hasSize(databaseSizeBeforeCreate);
        ProjectMemberRoleAssignment testProjectMemberRoleAssignment = projectMemberRoleAssignmentList.get(
            projectMemberRoleAssignmentList.size() - 1
        );

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testProjectMemberRoleAssignment.getId()).isEqualTo(testProjectMemberRoleAssignment.getProjectMember().getId());

        // Validate the ProjectMemberRoleAssignment in Elasticsearch
        verify(mockProjectMemberRoleAssignmentSearchRepository).save(projectMemberRoleAssignment);
    }

    @Test
    @Transactional
    void checkAssignmentTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectMemberRoleAssignmentRepository.findAll().size();
        // set the field null
        projectMemberRoleAssignment.setAssignmentTimestamp(null);

        // Create the ProjectMemberRoleAssignment, which fails.
        ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO = projectMemberRoleAssignmentMapper.toDto(
            projectMemberRoleAssignment
        );

        restProjectMemberRoleAssignmentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        List<ProjectMemberRoleAssignment> projectMemberRoleAssignmentList = projectMemberRoleAssignmentRepository.findAll();
        assertThat(projectMemberRoleAssignmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProjectMemberRoleAssignments() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);

        // Get all the projectMemberRoleAssignmentList
        restProjectMemberRoleAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMemberRoleAssignment.getId().intValue())))
            .andExpect(jsonPath("$.[*].assignmentTimestamp").value(hasItem(sameInstant(DEFAULT_ASSIGNMENT_TIMESTAMP))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectMemberRoleAssignmentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(projectMemberRoleAssignmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProjectMemberRoleAssignmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(projectMemberRoleAssignmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectMemberRoleAssignmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(projectMemberRoleAssignmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProjectMemberRoleAssignmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(projectMemberRoleAssignmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getProjectMemberRoleAssignment() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);

        // Get the projectMemberRoleAssignment
        restProjectMemberRoleAssignmentMockMvc
            .perform(get(ENTITY_API_URL_ID, projectMemberRoleAssignment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(projectMemberRoleAssignment.getId().intValue()))
            .andExpect(jsonPath("$.assignmentTimestamp").value(sameInstant(DEFAULT_ASSIGNMENT_TIMESTAMP)));
    }

    @Test
    @Transactional
    void getProjectMemberRoleAssignmentsByIdFiltering() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);

        Long id = projectMemberRoleAssignment.getId();

        defaultProjectMemberRoleAssignmentShouldBeFound("id.equals=" + id);
        defaultProjectMemberRoleAssignmentShouldNotBeFound("id.notEquals=" + id);

        defaultProjectMemberRoleAssignmentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProjectMemberRoleAssignmentShouldNotBeFound("id.greaterThan=" + id);

        defaultProjectMemberRoleAssignmentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProjectMemberRoleAssignmentShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProjectMemberRoleAssignmentsByAssignmentTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);

        // Get all the projectMemberRoleAssignmentList where assignmentTimestamp equals to DEFAULT_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberRoleAssignmentShouldBeFound("assignmentTimestamp.equals=" + DEFAULT_ASSIGNMENT_TIMESTAMP);

        // Get all the projectMemberRoleAssignmentList where assignmentTimestamp equals to UPDATED_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberRoleAssignmentShouldNotBeFound("assignmentTimestamp.equals=" + UPDATED_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberRoleAssignmentsByAssignmentTimestampIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);

        // Get all the projectMemberRoleAssignmentList where assignmentTimestamp not equals to DEFAULT_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberRoleAssignmentShouldNotBeFound("assignmentTimestamp.notEquals=" + DEFAULT_ASSIGNMENT_TIMESTAMP);

        // Get all the projectMemberRoleAssignmentList where assignmentTimestamp not equals to UPDATED_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberRoleAssignmentShouldBeFound("assignmentTimestamp.notEquals=" + UPDATED_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberRoleAssignmentsByAssignmentTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);

        // Get all the projectMemberRoleAssignmentList where assignmentTimestamp in DEFAULT_ASSIGNMENT_TIMESTAMP or UPDATED_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberRoleAssignmentShouldBeFound(
            "assignmentTimestamp.in=" + DEFAULT_ASSIGNMENT_TIMESTAMP + "," + UPDATED_ASSIGNMENT_TIMESTAMP
        );

        // Get all the projectMemberRoleAssignmentList where assignmentTimestamp equals to UPDATED_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberRoleAssignmentShouldNotBeFound("assignmentTimestamp.in=" + UPDATED_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberRoleAssignmentsByAssignmentTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);

        // Get all the projectMemberRoleAssignmentList where assignmentTimestamp is not null
        defaultProjectMemberRoleAssignmentShouldBeFound("assignmentTimestamp.specified=true");

        // Get all the projectMemberRoleAssignmentList where assignmentTimestamp is null
        defaultProjectMemberRoleAssignmentShouldNotBeFound("assignmentTimestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectMemberRoleAssignmentsByAssignmentTimestampIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);

        // Get all the projectMemberRoleAssignmentList where assignmentTimestamp is greater than or equal to DEFAULT_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberRoleAssignmentShouldBeFound("assignmentTimestamp.greaterThanOrEqual=" + DEFAULT_ASSIGNMENT_TIMESTAMP);

        // Get all the projectMemberRoleAssignmentList where assignmentTimestamp is greater than or equal to UPDATED_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberRoleAssignmentShouldNotBeFound("assignmentTimestamp.greaterThanOrEqual=" + UPDATED_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberRoleAssignmentsByAssignmentTimestampIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);

        // Get all the projectMemberRoleAssignmentList where assignmentTimestamp is less than or equal to DEFAULT_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberRoleAssignmentShouldBeFound("assignmentTimestamp.lessThanOrEqual=" + DEFAULT_ASSIGNMENT_TIMESTAMP);

        // Get all the projectMemberRoleAssignmentList where assignmentTimestamp is less than or equal to SMALLER_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberRoleAssignmentShouldNotBeFound("assignmentTimestamp.lessThanOrEqual=" + SMALLER_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberRoleAssignmentsByAssignmentTimestampIsLessThanSomething() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);

        // Get all the projectMemberRoleAssignmentList where assignmentTimestamp is less than DEFAULT_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberRoleAssignmentShouldNotBeFound("assignmentTimestamp.lessThan=" + DEFAULT_ASSIGNMENT_TIMESTAMP);

        // Get all the projectMemberRoleAssignmentList where assignmentTimestamp is less than UPDATED_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberRoleAssignmentShouldBeFound("assignmentTimestamp.lessThan=" + UPDATED_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberRoleAssignmentsByAssignmentTimestampIsGreaterThanSomething() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);

        // Get all the projectMemberRoleAssignmentList where assignmentTimestamp is greater than DEFAULT_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberRoleAssignmentShouldNotBeFound("assignmentTimestamp.greaterThan=" + DEFAULT_ASSIGNMENT_TIMESTAMP);

        // Get all the projectMemberRoleAssignmentList where assignmentTimestamp is greater than SMALLER_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberRoleAssignmentShouldBeFound("assignmentTimestamp.greaterThan=" + SMALLER_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberRoleAssignmentsByProjectMemberIsEqualToSomething() throws Exception {
        // Get already existing entity
        ProjectMember projectMember = projectMemberRoleAssignment.getProjectMember();
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);
        Long projectMemberId = projectMember.getId();

        // Get all the projectMemberRoleAssignmentList where projectMember equals to projectMemberId
        defaultProjectMemberRoleAssignmentShouldBeFound("projectMemberId.equals=" + projectMemberId);

        // Get all the projectMemberRoleAssignmentList where projectMember equals to (projectMemberId + 1)
        defaultProjectMemberRoleAssignmentShouldNotBeFound("projectMemberId.equals=" + (projectMemberId + 1));
    }

    @Test
    @Transactional
    void getAllProjectMemberRoleAssignmentsByProjectMemberRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);
        ProjectMemberRole projectMemberRole;
        if (TestUtil.findAll(em, ProjectMemberRole.class).isEmpty()) {
            projectMemberRole = ProjectMemberRoleResourceIT.createEntity(em);
            em.persist(projectMemberRole);
            em.flush();
        } else {
            projectMemberRole = TestUtil.findAll(em, ProjectMemberRole.class).get(0);
        }
        em.persist(projectMemberRole);
        em.flush();
        projectMemberRoleAssignment.addProjectMemberRole(projectMemberRole);
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);
        Long projectMemberRoleId = projectMemberRole.getId();

        // Get all the projectMemberRoleAssignmentList where projectMemberRole equals to projectMemberRoleId
        defaultProjectMemberRoleAssignmentShouldBeFound("projectMemberRoleId.equals=" + projectMemberRoleId);

        // Get all the projectMemberRoleAssignmentList where projectMemberRole equals to (projectMemberRoleId + 1)
        defaultProjectMemberRoleAssignmentShouldNotBeFound("projectMemberRoleId.equals=" + (projectMemberRoleId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectMemberRoleAssignmentShouldBeFound(String filter) throws Exception {
        restProjectMemberRoleAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMemberRoleAssignment.getId().intValue())))
            .andExpect(jsonPath("$.[*].assignmentTimestamp").value(hasItem(sameInstant(DEFAULT_ASSIGNMENT_TIMESTAMP))));

        // Check, that the count call also returns 1
        restProjectMemberRoleAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProjectMemberRoleAssignmentShouldNotBeFound(String filter) throws Exception {
        restProjectMemberRoleAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProjectMemberRoleAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProjectMemberRoleAssignment() throws Exception {
        // Get the projectMemberRoleAssignment
        restProjectMemberRoleAssignmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProjectMemberRoleAssignment() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);

        int databaseSizeBeforeUpdate = projectMemberRoleAssignmentRepository.findAll().size();

        // Update the projectMemberRoleAssignment
        ProjectMemberRoleAssignment updatedProjectMemberRoleAssignment = projectMemberRoleAssignmentRepository
            .findById(projectMemberRoleAssignment.getId())
            .get();
        // Disconnect from session so that the updates on updatedProjectMemberRoleAssignment are not directly saved in db
        em.detach(updatedProjectMemberRoleAssignment);
        updatedProjectMemberRoleAssignment.assignmentTimestamp(UPDATED_ASSIGNMENT_TIMESTAMP);
        ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO = projectMemberRoleAssignmentMapper.toDto(
            updatedProjectMemberRoleAssignment
        );

        restProjectMemberRoleAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectMemberRoleAssignmentDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleAssignmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMemberRoleAssignment in the database
        List<ProjectMemberRoleAssignment> projectMemberRoleAssignmentList = projectMemberRoleAssignmentRepository.findAll();
        assertThat(projectMemberRoleAssignmentList).hasSize(databaseSizeBeforeUpdate);
        ProjectMemberRoleAssignment testProjectMemberRoleAssignment = projectMemberRoleAssignmentList.get(
            projectMemberRoleAssignmentList.size() - 1
        );
        assertThat(testProjectMemberRoleAssignment.getAssignmentTimestamp()).isEqualTo(UPDATED_ASSIGNMENT_TIMESTAMP);

        // Validate the ProjectMemberRoleAssignment in Elasticsearch
        verify(mockProjectMemberRoleAssignmentSearchRepository).save(testProjectMemberRoleAssignment);
    }

    @Test
    @Transactional
    void putNonExistingProjectMemberRoleAssignment() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRoleAssignmentRepository.findAll().size();
        projectMemberRoleAssignment.setId(count.incrementAndGet());

        // Create the ProjectMemberRoleAssignment
        ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO = projectMemberRoleAssignmentMapper.toDto(
            projectMemberRoleAssignment
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMemberRoleAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectMemberRoleAssignmentDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberRoleAssignment in the database
        List<ProjectMemberRoleAssignment> projectMemberRoleAssignmentList = projectMemberRoleAssignmentRepository.findAll();
        assertThat(projectMemberRoleAssignmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberRoleAssignment in Elasticsearch
        verify(mockProjectMemberRoleAssignmentSearchRepository, times(0)).save(projectMemberRoleAssignment);
    }

    @Test
    @Transactional
    void putWithIdMismatchProjectMemberRoleAssignment() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRoleAssignmentRepository.findAll().size();
        projectMemberRoleAssignment.setId(count.incrementAndGet());

        // Create the ProjectMemberRoleAssignment
        ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO = projectMemberRoleAssignmentMapper.toDto(
            projectMemberRoleAssignment
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberRoleAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberRoleAssignment in the database
        List<ProjectMemberRoleAssignment> projectMemberRoleAssignmentList = projectMemberRoleAssignmentRepository.findAll();
        assertThat(projectMemberRoleAssignmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberRoleAssignment in Elasticsearch
        verify(mockProjectMemberRoleAssignmentSearchRepository, times(0)).save(projectMemberRoleAssignment);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProjectMemberRoleAssignment() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRoleAssignmentRepository.findAll().size();
        projectMemberRoleAssignment.setId(count.incrementAndGet());

        // Create the ProjectMemberRoleAssignment
        ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO = projectMemberRoleAssignmentMapper.toDto(
            projectMemberRoleAssignment
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberRoleAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleAssignmentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectMemberRoleAssignment in the database
        List<ProjectMemberRoleAssignment> projectMemberRoleAssignmentList = projectMemberRoleAssignmentRepository.findAll();
        assertThat(projectMemberRoleAssignmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberRoleAssignment in Elasticsearch
        verify(mockProjectMemberRoleAssignmentSearchRepository, times(0)).save(projectMemberRoleAssignment);
    }

    @Test
    @Transactional
    void partialUpdateProjectMemberRoleAssignmentWithPatch() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);

        int databaseSizeBeforeUpdate = projectMemberRoleAssignmentRepository.findAll().size();

        // Update the projectMemberRoleAssignment using partial update
        ProjectMemberRoleAssignment partialUpdatedProjectMemberRoleAssignment = new ProjectMemberRoleAssignment();
        partialUpdatedProjectMemberRoleAssignment.setId(projectMemberRoleAssignment.getId());

        partialUpdatedProjectMemberRoleAssignment.assignmentTimestamp(UPDATED_ASSIGNMENT_TIMESTAMP);

        restProjectMemberRoleAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectMemberRoleAssignment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectMemberRoleAssignment))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMemberRoleAssignment in the database
        List<ProjectMemberRoleAssignment> projectMemberRoleAssignmentList = projectMemberRoleAssignmentRepository.findAll();
        assertThat(projectMemberRoleAssignmentList).hasSize(databaseSizeBeforeUpdate);
        ProjectMemberRoleAssignment testProjectMemberRoleAssignment = projectMemberRoleAssignmentList.get(
            projectMemberRoleAssignmentList.size() - 1
        );
        assertThat(testProjectMemberRoleAssignment.getAssignmentTimestamp()).isEqualTo(UPDATED_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void fullUpdateProjectMemberRoleAssignmentWithPatch() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);

        int databaseSizeBeforeUpdate = projectMemberRoleAssignmentRepository.findAll().size();

        // Update the projectMemberRoleAssignment using partial update
        ProjectMemberRoleAssignment partialUpdatedProjectMemberRoleAssignment = new ProjectMemberRoleAssignment();
        partialUpdatedProjectMemberRoleAssignment.setId(projectMemberRoleAssignment.getId());

        partialUpdatedProjectMemberRoleAssignment.assignmentTimestamp(UPDATED_ASSIGNMENT_TIMESTAMP);

        restProjectMemberRoleAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectMemberRoleAssignment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectMemberRoleAssignment))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMemberRoleAssignment in the database
        List<ProjectMemberRoleAssignment> projectMemberRoleAssignmentList = projectMemberRoleAssignmentRepository.findAll();
        assertThat(projectMemberRoleAssignmentList).hasSize(databaseSizeBeforeUpdate);
        ProjectMemberRoleAssignment testProjectMemberRoleAssignment = projectMemberRoleAssignmentList.get(
            projectMemberRoleAssignmentList.size() - 1
        );
        assertThat(testProjectMemberRoleAssignment.getAssignmentTimestamp()).isEqualTo(UPDATED_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void patchNonExistingProjectMemberRoleAssignment() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRoleAssignmentRepository.findAll().size();
        projectMemberRoleAssignment.setId(count.incrementAndGet());

        // Create the ProjectMemberRoleAssignment
        ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO = projectMemberRoleAssignmentMapper.toDto(
            projectMemberRoleAssignment
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMemberRoleAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, projectMemberRoleAssignmentDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberRoleAssignment in the database
        List<ProjectMemberRoleAssignment> projectMemberRoleAssignmentList = projectMemberRoleAssignmentRepository.findAll();
        assertThat(projectMemberRoleAssignmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberRoleAssignment in Elasticsearch
        verify(mockProjectMemberRoleAssignmentSearchRepository, times(0)).save(projectMemberRoleAssignment);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProjectMemberRoleAssignment() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRoleAssignmentRepository.findAll().size();
        projectMemberRoleAssignment.setId(count.incrementAndGet());

        // Create the ProjectMemberRoleAssignment
        ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO = projectMemberRoleAssignmentMapper.toDto(
            projectMemberRoleAssignment
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberRoleAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberRoleAssignment in the database
        List<ProjectMemberRoleAssignment> projectMemberRoleAssignmentList = projectMemberRoleAssignmentRepository.findAll();
        assertThat(projectMemberRoleAssignmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberRoleAssignment in Elasticsearch
        verify(mockProjectMemberRoleAssignmentSearchRepository, times(0)).save(projectMemberRoleAssignment);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProjectMemberRoleAssignment() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRoleAssignmentRepository.findAll().size();
        projectMemberRoleAssignment.setId(count.incrementAndGet());

        // Create the ProjectMemberRoleAssignment
        ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO = projectMemberRoleAssignmentMapper.toDto(
            projectMemberRoleAssignment
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberRoleAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberRoleAssignmentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectMemberRoleAssignment in the database
        List<ProjectMemberRoleAssignment> projectMemberRoleAssignmentList = projectMemberRoleAssignmentRepository.findAll();
        assertThat(projectMemberRoleAssignmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberRoleAssignment in Elasticsearch
        verify(mockProjectMemberRoleAssignmentSearchRepository, times(0)).save(projectMemberRoleAssignment);
    }

    @Test
    @Transactional
    void deleteProjectMemberRoleAssignment() throws Exception {
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);

        int databaseSizeBeforeDelete = projectMemberRoleAssignmentRepository.findAll().size();

        // Delete the projectMemberRoleAssignment
        restProjectMemberRoleAssignmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, projectMemberRoleAssignment.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProjectMemberRoleAssignment> projectMemberRoleAssignmentList = projectMemberRoleAssignmentRepository.findAll();
        assertThat(projectMemberRoleAssignmentList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ProjectMemberRoleAssignment in Elasticsearch
        verify(mockProjectMemberRoleAssignmentSearchRepository, times(1)).deleteById(projectMemberRoleAssignment.getId());
    }

    @Test
    @Transactional
    void searchProjectMemberRoleAssignment() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        projectMemberRoleAssignmentRepository.saveAndFlush(projectMemberRoleAssignment);
        when(mockProjectMemberRoleAssignmentSearchRepository.search("id:" + projectMemberRoleAssignment.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(projectMemberRoleAssignment), PageRequest.of(0, 1), 1));

        // Search the projectMemberRoleAssignment
        restProjectMemberRoleAssignmentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + projectMemberRoleAssignment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMemberRoleAssignment.getId().intValue())))
            .andExpect(jsonPath("$.[*].assignmentTimestamp").value(hasItem(sameInstant(DEFAULT_ASSIGNMENT_TIMESTAMP))));
    }
}
