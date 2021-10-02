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
import org.codingspiderfox.domain.ProjectMemberPermission;
import org.codingspiderfox.domain.ProjectMemberPermissionAssignment;
import org.codingspiderfox.repository.ProjectMemberPermissionAssignmentRepository;
import org.codingspiderfox.repository.search.ProjectMemberPermissionAssignmentSearchRepository;
import org.codingspiderfox.service.ProjectMemberPermissionAssignmentService;
import org.codingspiderfox.service.criteria.ProjectMemberPermissionAssignmentCriteria;
import org.codingspiderfox.service.dto.ProjectMemberPermissionAssignmentDTO;
import org.codingspiderfox.service.mapper.ProjectMemberPermissionAssignmentMapper;
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
 * Integration tests for the {@link ProjectMemberPermissionAssignmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProjectMemberPermissionAssignmentResourceIT {

    private static final ZonedDateTime DEFAULT_ASSIGNMENT_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ASSIGNMENT_TIMESTAMP = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_ASSIGNMENT_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/project-member-permission-assignments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/project-member-permission-assignments";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjectMemberPermissionAssignmentRepository projectMemberPermissionAssignmentRepository;

    @Mock
    private ProjectMemberPermissionAssignmentRepository projectMemberPermissionAssignmentRepositoryMock;

    @Autowired
    private ProjectMemberPermissionAssignmentMapper projectMemberPermissionAssignmentMapper;

    @Mock
    private ProjectMemberPermissionAssignmentService projectMemberPermissionAssignmentServiceMock;

    /**
     * This repository is mocked in the org.codingspiderfox.repository.search test package.
     *
     * @see org.codingspiderfox.repository.search.ProjectMemberPermissionAssignmentSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProjectMemberPermissionAssignmentSearchRepository mockProjectMemberPermissionAssignmentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjectMemberPermissionAssignmentMockMvc;

    private ProjectMemberPermissionAssignment projectMemberPermissionAssignment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectMemberPermissionAssignment createEntity(EntityManager em) {
        ProjectMemberPermissionAssignment projectMemberPermissionAssignment = new ProjectMemberPermissionAssignment()
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
        projectMemberPermissionAssignment.setProjectMember(projectMember);
        // Add required entity
        ProjectMemberPermission projectMemberPermission;
        if (TestUtil.findAll(em, ProjectMemberPermission.class).isEmpty()) {
            projectMemberPermission = ProjectMemberPermissionResourceIT.createEntity(em);
            em.persist(projectMemberPermission);
            em.flush();
        } else {
            projectMemberPermission = TestUtil.findAll(em, ProjectMemberPermission.class).get(0);
        }
        projectMemberPermissionAssignment.getProjectMemberPermissions().add(projectMemberPermission);
        return projectMemberPermissionAssignment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectMemberPermissionAssignment createUpdatedEntity(EntityManager em) {
        ProjectMemberPermissionAssignment projectMemberPermissionAssignment = new ProjectMemberPermissionAssignment()
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
        projectMemberPermissionAssignment.setProjectMember(projectMember);
        // Add required entity
        ProjectMemberPermission projectMemberPermission;
        if (TestUtil.findAll(em, ProjectMemberPermission.class).isEmpty()) {
            projectMemberPermission = ProjectMemberPermissionResourceIT.createUpdatedEntity(em);
            em.persist(projectMemberPermission);
            em.flush();
        } else {
            projectMemberPermission = TestUtil.findAll(em, ProjectMemberPermission.class).get(0);
        }
        projectMemberPermissionAssignment.getProjectMemberPermissions().add(projectMemberPermission);
        return projectMemberPermissionAssignment;
    }

    @BeforeEach
    public void initTest() {
        projectMemberPermissionAssignment = createEntity(em);
    }

    @Test
    @Transactional
    void createProjectMemberPermissionAssignment() throws Exception {
        int databaseSizeBeforeCreate = projectMemberPermissionAssignmentRepository.findAll().size();
        // Create the ProjectMemberPermissionAssignment
        ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO = projectMemberPermissionAssignmentMapper.toDto(
            projectMemberPermissionAssignment
        );
        restProjectMemberPermissionAssignmentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionAssignmentDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ProjectMemberPermissionAssignment in the database
        List<ProjectMemberPermissionAssignment> projectMemberPermissionAssignmentList = projectMemberPermissionAssignmentRepository.findAll();
        assertThat(projectMemberPermissionAssignmentList).hasSize(databaseSizeBeforeCreate + 1);
        ProjectMemberPermissionAssignment testProjectMemberPermissionAssignment = projectMemberPermissionAssignmentList.get(
            projectMemberPermissionAssignmentList.size() - 1
        );
        assertThat(testProjectMemberPermissionAssignment.getAssignmentTimestamp()).isEqualTo(DEFAULT_ASSIGNMENT_TIMESTAMP);

        // Validate the id for MapsId, the ids must be same
        assertThat(testProjectMemberPermissionAssignment.getId())
            .isEqualTo(testProjectMemberPermissionAssignment.getProjectMember().getId());

        // Validate the ProjectMemberPermissionAssignment in Elasticsearch
        verify(mockProjectMemberPermissionAssignmentSearchRepository, times(1)).save(testProjectMemberPermissionAssignment);
    }

    @Test
    @Transactional
    void createProjectMemberPermissionAssignmentWithExistingId() throws Exception {
        // Create the ProjectMemberPermissionAssignment with an existing ID
        projectMemberPermissionAssignment.setId(1L);
        ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO = projectMemberPermissionAssignmentMapper.toDto(
            projectMemberPermissionAssignment
        );

        int databaseSizeBeforeCreate = projectMemberPermissionAssignmentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectMemberPermissionAssignmentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberPermissionAssignment in the database
        List<ProjectMemberPermissionAssignment> projectMemberPermissionAssignmentList = projectMemberPermissionAssignmentRepository.findAll();
        assertThat(projectMemberPermissionAssignmentList).hasSize(databaseSizeBeforeCreate);

        // Validate the ProjectMemberPermissionAssignment in Elasticsearch
        verify(mockProjectMemberPermissionAssignmentSearchRepository, times(0)).save(projectMemberPermissionAssignment);
    }

    @Test
    @Transactional
    void updateProjectMemberPermissionAssignmentMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);
        int databaseSizeBeforeCreate = projectMemberPermissionAssignmentRepository.findAll().size();

        // Add a new parent entity
        ProjectMember projectMember = ProjectMemberResourceIT.createUpdatedEntity(em);
        em.persist(projectMember);
        em.flush();

        // Load the projectMemberPermissionAssignment
        ProjectMemberPermissionAssignment updatedProjectMemberPermissionAssignment = projectMemberPermissionAssignmentRepository
            .findById(projectMemberPermissionAssignment.getId())
            .get();
        assertThat(updatedProjectMemberPermissionAssignment).isNotNull();
        // Disconnect from session so that the updates on updatedProjectMemberPermissionAssignment are not directly saved in db
        em.detach(updatedProjectMemberPermissionAssignment);

        // Update the ProjectMember with new association value
        updatedProjectMemberPermissionAssignment.setProjectMember(projectMember);
        ProjectMemberPermissionAssignmentDTO updatedProjectMemberPermissionAssignmentDTO = projectMemberPermissionAssignmentMapper.toDto(
            updatedProjectMemberPermissionAssignment
        );
        assertThat(updatedProjectMemberPermissionAssignmentDTO).isNotNull();

        // Update the entity
        restProjectMemberPermissionAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProjectMemberPermissionAssignmentDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProjectMemberPermissionAssignmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMemberPermissionAssignment in the database
        List<ProjectMemberPermissionAssignment> projectMemberPermissionAssignmentList = projectMemberPermissionAssignmentRepository.findAll();
        assertThat(projectMemberPermissionAssignmentList).hasSize(databaseSizeBeforeCreate);
        ProjectMemberPermissionAssignment testProjectMemberPermissionAssignment = projectMemberPermissionAssignmentList.get(
            projectMemberPermissionAssignmentList.size() - 1
        );

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testProjectMemberPermissionAssignment.getId()).isEqualTo(testProjectMemberPermissionAssignment.getProjectMember().getId());

        // Validate the ProjectMemberPermissionAssignment in Elasticsearch
        verify(mockProjectMemberPermissionAssignmentSearchRepository).save(projectMemberPermissionAssignment);
    }

    @Test
    @Transactional
    void checkAssignmentTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectMemberPermissionAssignmentRepository.findAll().size();
        // set the field null
        projectMemberPermissionAssignment.setAssignmentTimestamp(null);

        // Create the ProjectMemberPermissionAssignment, which fails.
        ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO = projectMemberPermissionAssignmentMapper.toDto(
            projectMemberPermissionAssignment
        );

        restProjectMemberPermissionAssignmentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        List<ProjectMemberPermissionAssignment> projectMemberPermissionAssignmentList = projectMemberPermissionAssignmentRepository.findAll();
        assertThat(projectMemberPermissionAssignmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionAssignments() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);

        // Get all the projectMemberPermissionAssignmentList
        restProjectMemberPermissionAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMemberPermissionAssignment.getId().intValue())))
            .andExpect(jsonPath("$.[*].assignmentTimestamp").value(hasItem(sameInstant(DEFAULT_ASSIGNMENT_TIMESTAMP))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectMemberPermissionAssignmentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(projectMemberPermissionAssignmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProjectMemberPermissionAssignmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(projectMemberPermissionAssignmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectMemberPermissionAssignmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(projectMemberPermissionAssignmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProjectMemberPermissionAssignmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(projectMemberPermissionAssignmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getProjectMemberPermissionAssignment() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);

        // Get the projectMemberPermissionAssignment
        restProjectMemberPermissionAssignmentMockMvc
            .perform(get(ENTITY_API_URL_ID, projectMemberPermissionAssignment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(projectMemberPermissionAssignment.getId().intValue()))
            .andExpect(jsonPath("$.assignmentTimestamp").value(sameInstant(DEFAULT_ASSIGNMENT_TIMESTAMP)));
    }

    @Test
    @Transactional
    void getProjectMemberPermissionAssignmentsByIdFiltering() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);

        Long id = projectMemberPermissionAssignment.getId();

        defaultProjectMemberPermissionAssignmentShouldBeFound("id.equals=" + id);
        defaultProjectMemberPermissionAssignmentShouldNotBeFound("id.notEquals=" + id);

        defaultProjectMemberPermissionAssignmentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProjectMemberPermissionAssignmentShouldNotBeFound("id.greaterThan=" + id);

        defaultProjectMemberPermissionAssignmentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProjectMemberPermissionAssignmentShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionAssignmentsByAssignmentTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);

        // Get all the projectMemberPermissionAssignmentList where assignmentTimestamp equals to DEFAULT_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberPermissionAssignmentShouldBeFound("assignmentTimestamp.equals=" + DEFAULT_ASSIGNMENT_TIMESTAMP);

        // Get all the projectMemberPermissionAssignmentList where assignmentTimestamp equals to UPDATED_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberPermissionAssignmentShouldNotBeFound("assignmentTimestamp.equals=" + UPDATED_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionAssignmentsByAssignmentTimestampIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);

        // Get all the projectMemberPermissionAssignmentList where assignmentTimestamp not equals to DEFAULT_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberPermissionAssignmentShouldNotBeFound("assignmentTimestamp.notEquals=" + DEFAULT_ASSIGNMENT_TIMESTAMP);

        // Get all the projectMemberPermissionAssignmentList where assignmentTimestamp not equals to UPDATED_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberPermissionAssignmentShouldBeFound("assignmentTimestamp.notEquals=" + UPDATED_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionAssignmentsByAssignmentTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);

        // Get all the projectMemberPermissionAssignmentList where assignmentTimestamp in DEFAULT_ASSIGNMENT_TIMESTAMP or UPDATED_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberPermissionAssignmentShouldBeFound(
            "assignmentTimestamp.in=" + DEFAULT_ASSIGNMENT_TIMESTAMP + "," + UPDATED_ASSIGNMENT_TIMESTAMP
        );

        // Get all the projectMemberPermissionAssignmentList where assignmentTimestamp equals to UPDATED_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberPermissionAssignmentShouldNotBeFound("assignmentTimestamp.in=" + UPDATED_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionAssignmentsByAssignmentTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);

        // Get all the projectMemberPermissionAssignmentList where assignmentTimestamp is not null
        defaultProjectMemberPermissionAssignmentShouldBeFound("assignmentTimestamp.specified=true");

        // Get all the projectMemberPermissionAssignmentList where assignmentTimestamp is null
        defaultProjectMemberPermissionAssignmentShouldNotBeFound("assignmentTimestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionAssignmentsByAssignmentTimestampIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);

        // Get all the projectMemberPermissionAssignmentList where assignmentTimestamp is greater than or equal to DEFAULT_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberPermissionAssignmentShouldBeFound("assignmentTimestamp.greaterThanOrEqual=" + DEFAULT_ASSIGNMENT_TIMESTAMP);

        // Get all the projectMemberPermissionAssignmentList where assignmentTimestamp is greater than or equal to UPDATED_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberPermissionAssignmentShouldNotBeFound("assignmentTimestamp.greaterThanOrEqual=" + UPDATED_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionAssignmentsByAssignmentTimestampIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);

        // Get all the projectMemberPermissionAssignmentList where assignmentTimestamp is less than or equal to DEFAULT_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberPermissionAssignmentShouldBeFound("assignmentTimestamp.lessThanOrEqual=" + DEFAULT_ASSIGNMENT_TIMESTAMP);

        // Get all the projectMemberPermissionAssignmentList where assignmentTimestamp is less than or equal to SMALLER_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberPermissionAssignmentShouldNotBeFound("assignmentTimestamp.lessThanOrEqual=" + SMALLER_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionAssignmentsByAssignmentTimestampIsLessThanSomething() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);

        // Get all the projectMemberPermissionAssignmentList where assignmentTimestamp is less than DEFAULT_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberPermissionAssignmentShouldNotBeFound("assignmentTimestamp.lessThan=" + DEFAULT_ASSIGNMENT_TIMESTAMP);

        // Get all the projectMemberPermissionAssignmentList where assignmentTimestamp is less than UPDATED_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberPermissionAssignmentShouldBeFound("assignmentTimestamp.lessThan=" + UPDATED_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionAssignmentsByAssignmentTimestampIsGreaterThanSomething() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);

        // Get all the projectMemberPermissionAssignmentList where assignmentTimestamp is greater than DEFAULT_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberPermissionAssignmentShouldNotBeFound("assignmentTimestamp.greaterThan=" + DEFAULT_ASSIGNMENT_TIMESTAMP);

        // Get all the projectMemberPermissionAssignmentList where assignmentTimestamp is greater than SMALLER_ASSIGNMENT_TIMESTAMP
        defaultProjectMemberPermissionAssignmentShouldBeFound("assignmentTimestamp.greaterThan=" + SMALLER_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionAssignmentsByProjectMemberIsEqualToSomething() throws Exception {
        // Get already existing entity
        ProjectMember projectMember = projectMemberPermissionAssignment.getProjectMember();
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);
        Long projectMemberId = projectMember.getId();

        // Get all the projectMemberPermissionAssignmentList where projectMember equals to projectMemberId
        defaultProjectMemberPermissionAssignmentShouldBeFound("projectMemberId.equals=" + projectMemberId);

        // Get all the projectMemberPermissionAssignmentList where projectMember equals to (projectMemberId + 1)
        defaultProjectMemberPermissionAssignmentShouldNotBeFound("projectMemberId.equals=" + (projectMemberId + 1));
    }

    @Test
    @Transactional
    void getAllProjectMemberPermissionAssignmentsByProjectMemberPermissionIsEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);
        ProjectMemberPermission projectMemberPermission;
        if (TestUtil.findAll(em, ProjectMemberPermission.class).isEmpty()) {
            projectMemberPermission = ProjectMemberPermissionResourceIT.createEntity(em);
            em.persist(projectMemberPermission);
            em.flush();
        } else {
            projectMemberPermission = TestUtil.findAll(em, ProjectMemberPermission.class).get(0);
        }
        em.persist(projectMemberPermission);
        em.flush();
        projectMemberPermissionAssignment.addProjectMemberPermission(projectMemberPermission);
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);
        Long projectMemberPermissionId = projectMemberPermission.getId();

        // Get all the projectMemberPermissionAssignmentList where projectMemberPermission equals to projectMemberPermissionId
        defaultProjectMemberPermissionAssignmentShouldBeFound("projectMemberPermissionId.equals=" + projectMemberPermissionId);

        // Get all the projectMemberPermissionAssignmentList where projectMemberPermission equals to (projectMemberPermissionId + 1)
        defaultProjectMemberPermissionAssignmentShouldNotBeFound("projectMemberPermissionId.equals=" + (projectMemberPermissionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectMemberPermissionAssignmentShouldBeFound(String filter) throws Exception {
        restProjectMemberPermissionAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMemberPermissionAssignment.getId().intValue())))
            .andExpect(jsonPath("$.[*].assignmentTimestamp").value(hasItem(sameInstant(DEFAULT_ASSIGNMENT_TIMESTAMP))));

        // Check, that the count call also returns 1
        restProjectMemberPermissionAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProjectMemberPermissionAssignmentShouldNotBeFound(String filter) throws Exception {
        restProjectMemberPermissionAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProjectMemberPermissionAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProjectMemberPermissionAssignment() throws Exception {
        // Get the projectMemberPermissionAssignment
        restProjectMemberPermissionAssignmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProjectMemberPermissionAssignment() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);

        int databaseSizeBeforeUpdate = projectMemberPermissionAssignmentRepository.findAll().size();

        // Update the projectMemberPermissionAssignment
        ProjectMemberPermissionAssignment updatedProjectMemberPermissionAssignment = projectMemberPermissionAssignmentRepository
            .findById(projectMemberPermissionAssignment.getId())
            .get();
        // Disconnect from session so that the updates on updatedProjectMemberPermissionAssignment are not directly saved in db
        em.detach(updatedProjectMemberPermissionAssignment);
        updatedProjectMemberPermissionAssignment.assignmentTimestamp(UPDATED_ASSIGNMENT_TIMESTAMP);
        ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO = projectMemberPermissionAssignmentMapper.toDto(
            updatedProjectMemberPermissionAssignment
        );

        restProjectMemberPermissionAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectMemberPermissionAssignmentDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionAssignmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMemberPermissionAssignment in the database
        List<ProjectMemberPermissionAssignment> projectMemberPermissionAssignmentList = projectMemberPermissionAssignmentRepository.findAll();
        assertThat(projectMemberPermissionAssignmentList).hasSize(databaseSizeBeforeUpdate);
        ProjectMemberPermissionAssignment testProjectMemberPermissionAssignment = projectMemberPermissionAssignmentList.get(
            projectMemberPermissionAssignmentList.size() - 1
        );
        assertThat(testProjectMemberPermissionAssignment.getAssignmentTimestamp()).isEqualTo(UPDATED_ASSIGNMENT_TIMESTAMP);

        // Validate the ProjectMemberPermissionAssignment in Elasticsearch
        verify(mockProjectMemberPermissionAssignmentSearchRepository).save(testProjectMemberPermissionAssignment);
    }

    @Test
    @Transactional
    void putNonExistingProjectMemberPermissionAssignment() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberPermissionAssignmentRepository.findAll().size();
        projectMemberPermissionAssignment.setId(count.incrementAndGet());

        // Create the ProjectMemberPermissionAssignment
        ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO = projectMemberPermissionAssignmentMapper.toDto(
            projectMemberPermissionAssignment
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMemberPermissionAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectMemberPermissionAssignmentDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberPermissionAssignment in the database
        List<ProjectMemberPermissionAssignment> projectMemberPermissionAssignmentList = projectMemberPermissionAssignmentRepository.findAll();
        assertThat(projectMemberPermissionAssignmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberPermissionAssignment in Elasticsearch
        verify(mockProjectMemberPermissionAssignmentSearchRepository, times(0)).save(projectMemberPermissionAssignment);
    }

    @Test
    @Transactional
    void putWithIdMismatchProjectMemberPermissionAssignment() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberPermissionAssignmentRepository.findAll().size();
        projectMemberPermissionAssignment.setId(count.incrementAndGet());

        // Create the ProjectMemberPermissionAssignment
        ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO = projectMemberPermissionAssignmentMapper.toDto(
            projectMemberPermissionAssignment
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberPermissionAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberPermissionAssignment in the database
        List<ProjectMemberPermissionAssignment> projectMemberPermissionAssignmentList = projectMemberPermissionAssignmentRepository.findAll();
        assertThat(projectMemberPermissionAssignmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberPermissionAssignment in Elasticsearch
        verify(mockProjectMemberPermissionAssignmentSearchRepository, times(0)).save(projectMemberPermissionAssignment);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProjectMemberPermissionAssignment() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberPermissionAssignmentRepository.findAll().size();
        projectMemberPermissionAssignment.setId(count.incrementAndGet());

        // Create the ProjectMemberPermissionAssignment
        ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO = projectMemberPermissionAssignmentMapper.toDto(
            projectMemberPermissionAssignment
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberPermissionAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionAssignmentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectMemberPermissionAssignment in the database
        List<ProjectMemberPermissionAssignment> projectMemberPermissionAssignmentList = projectMemberPermissionAssignmentRepository.findAll();
        assertThat(projectMemberPermissionAssignmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberPermissionAssignment in Elasticsearch
        verify(mockProjectMemberPermissionAssignmentSearchRepository, times(0)).save(projectMemberPermissionAssignment);
    }

    @Test
    @Transactional
    void partialUpdateProjectMemberPermissionAssignmentWithPatch() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);

        int databaseSizeBeforeUpdate = projectMemberPermissionAssignmentRepository.findAll().size();

        // Update the projectMemberPermissionAssignment using partial update
        ProjectMemberPermissionAssignment partialUpdatedProjectMemberPermissionAssignment = new ProjectMemberPermissionAssignment();
        partialUpdatedProjectMemberPermissionAssignment.setId(projectMemberPermissionAssignment.getId());

        restProjectMemberPermissionAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectMemberPermissionAssignment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectMemberPermissionAssignment))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMemberPermissionAssignment in the database
        List<ProjectMemberPermissionAssignment> projectMemberPermissionAssignmentList = projectMemberPermissionAssignmentRepository.findAll();
        assertThat(projectMemberPermissionAssignmentList).hasSize(databaseSizeBeforeUpdate);
        ProjectMemberPermissionAssignment testProjectMemberPermissionAssignment = projectMemberPermissionAssignmentList.get(
            projectMemberPermissionAssignmentList.size() - 1
        );
        assertThat(testProjectMemberPermissionAssignment.getAssignmentTimestamp()).isEqualTo(DEFAULT_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void fullUpdateProjectMemberPermissionAssignmentWithPatch() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);

        int databaseSizeBeforeUpdate = projectMemberPermissionAssignmentRepository.findAll().size();

        // Update the projectMemberPermissionAssignment using partial update
        ProjectMemberPermissionAssignment partialUpdatedProjectMemberPermissionAssignment = new ProjectMemberPermissionAssignment();
        partialUpdatedProjectMemberPermissionAssignment.setId(projectMemberPermissionAssignment.getId());

        partialUpdatedProjectMemberPermissionAssignment.assignmentTimestamp(UPDATED_ASSIGNMENT_TIMESTAMP);

        restProjectMemberPermissionAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectMemberPermissionAssignment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectMemberPermissionAssignment))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMemberPermissionAssignment in the database
        List<ProjectMemberPermissionAssignment> projectMemberPermissionAssignmentList = projectMemberPermissionAssignmentRepository.findAll();
        assertThat(projectMemberPermissionAssignmentList).hasSize(databaseSizeBeforeUpdate);
        ProjectMemberPermissionAssignment testProjectMemberPermissionAssignment = projectMemberPermissionAssignmentList.get(
            projectMemberPermissionAssignmentList.size() - 1
        );
        assertThat(testProjectMemberPermissionAssignment.getAssignmentTimestamp()).isEqualTo(UPDATED_ASSIGNMENT_TIMESTAMP);
    }

    @Test
    @Transactional
    void patchNonExistingProjectMemberPermissionAssignment() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberPermissionAssignmentRepository.findAll().size();
        projectMemberPermissionAssignment.setId(count.incrementAndGet());

        // Create the ProjectMemberPermissionAssignment
        ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO = projectMemberPermissionAssignmentMapper.toDto(
            projectMemberPermissionAssignment
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMemberPermissionAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, projectMemberPermissionAssignmentDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberPermissionAssignment in the database
        List<ProjectMemberPermissionAssignment> projectMemberPermissionAssignmentList = projectMemberPermissionAssignmentRepository.findAll();
        assertThat(projectMemberPermissionAssignmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberPermissionAssignment in Elasticsearch
        verify(mockProjectMemberPermissionAssignmentSearchRepository, times(0)).save(projectMemberPermissionAssignment);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProjectMemberPermissionAssignment() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberPermissionAssignmentRepository.findAll().size();
        projectMemberPermissionAssignment.setId(count.incrementAndGet());

        // Create the ProjectMemberPermissionAssignment
        ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO = projectMemberPermissionAssignmentMapper.toDto(
            projectMemberPermissionAssignment
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberPermissionAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMemberPermissionAssignment in the database
        List<ProjectMemberPermissionAssignment> projectMemberPermissionAssignmentList = projectMemberPermissionAssignmentRepository.findAll();
        assertThat(projectMemberPermissionAssignmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberPermissionAssignment in Elasticsearch
        verify(mockProjectMemberPermissionAssignmentSearchRepository, times(0)).save(projectMemberPermissionAssignment);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProjectMemberPermissionAssignment() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberPermissionAssignmentRepository.findAll().size();
        projectMemberPermissionAssignment.setId(count.incrementAndGet());

        // Create the ProjectMemberPermissionAssignment
        ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO = projectMemberPermissionAssignmentMapper.toDto(
            projectMemberPermissionAssignment
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberPermissionAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberPermissionAssignmentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectMemberPermissionAssignment in the database
        List<ProjectMemberPermissionAssignment> projectMemberPermissionAssignmentList = projectMemberPermissionAssignmentRepository.findAll();
        assertThat(projectMemberPermissionAssignmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMemberPermissionAssignment in Elasticsearch
        verify(mockProjectMemberPermissionAssignmentSearchRepository, times(0)).save(projectMemberPermissionAssignment);
    }

    @Test
    @Transactional
    void deleteProjectMemberPermissionAssignment() throws Exception {
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);

        int databaseSizeBeforeDelete = projectMemberPermissionAssignmentRepository.findAll().size();

        // Delete the projectMemberPermissionAssignment
        restProjectMemberPermissionAssignmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, projectMemberPermissionAssignment.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProjectMemberPermissionAssignment> projectMemberPermissionAssignmentList = projectMemberPermissionAssignmentRepository.findAll();
        assertThat(projectMemberPermissionAssignmentList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ProjectMemberPermissionAssignment in Elasticsearch
        verify(mockProjectMemberPermissionAssignmentSearchRepository, times(1)).deleteById(projectMemberPermissionAssignment.getId());
    }

    @Test
    @Transactional
    void searchProjectMemberPermissionAssignment() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        projectMemberPermissionAssignmentRepository.saveAndFlush(projectMemberPermissionAssignment);
        when(
            mockProjectMemberPermissionAssignmentSearchRepository.search(
                "id:" + projectMemberPermissionAssignment.getId(),
                PageRequest.of(0, 20)
            )
        )
            .thenReturn(new PageImpl<>(Collections.singletonList(projectMemberPermissionAssignment), PageRequest.of(0, 1), 1));

        // Search the projectMemberPermissionAssignment
        restProjectMemberPermissionAssignmentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + projectMemberPermissionAssignment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMemberPermissionAssignment.getId().intValue())))
            .andExpect(jsonPath("$.[*].assignmentTimestamp").value(hasItem(sameInstant(DEFAULT_ASSIGNMENT_TIMESTAMP))));
    }
}
