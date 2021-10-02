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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.codingspiderfox.IntegrationTest;
import org.codingspiderfox.domain.Project;
import org.codingspiderfox.domain.ProjectMember;
import org.codingspiderfox.domain.User;
import org.codingspiderfox.domain.enumeration.ProjectMemberRole;
import org.codingspiderfox.domain.enumeration.ProjectPermission;
import org.codingspiderfox.repository.ProjectMemberRepository;
import org.codingspiderfox.repository.search.ProjectMemberSearchRepository;
import org.codingspiderfox.service.criteria.ProjectMemberCriteria;
import org.codingspiderfox.service.dto.ProjectMemberDTO;
import org.codingspiderfox.service.mapper.ProjectMemberMapper;
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
 * Integration tests for the {@link ProjectMemberResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProjectMemberResourceIT {

    private static final List<ProjectPermission> DEFAULT_ADDITIONAL_PROJECT_PERMISSIONS = Arrays.asList(ProjectPermission.CLOSE_PROJECT);
    private static final List<ProjectPermission> UPDATED_ADDITIONAL_PROJECT_PERMISSIONS = Arrays.asList(ProjectPermission.CLOSE_BILL);

    private static final List<ProjectMemberRole> DEFAULT_ROLE_IN_PROJECT = Arrays.asList(ProjectMemberRole.PROJECT_ADMIN);
    private static final List<ProjectMemberRole> UPDATED_ROLE_IN_PROJECT = Arrays.asList(ProjectMemberRole.BILL_CONTRIBUTOR);

    private static final ZonedDateTime DEFAULT_ADDED_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ADDED_TIMESTAMP = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_ADDED_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/project-members";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/project-members";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    /**
     * This repository is mocked in the org.codingspiderfox.repository.search test package.
     *
     * @see org.codingspiderfox.repository.search.ProjectMemberSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProjectMemberSearchRepository mockProjectMemberSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjectMemberMockMvc;

    private ProjectMember projectMember;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectMember createEntity(EntityManager em) {
        ProjectMember projectMember = new ProjectMember()
            .additionalProjectPermissions(DEFAULT_ADDITIONAL_PROJECT_PERMISSIONS)
            .roleInProject(DEFAULT_ROLE_IN_PROJECT)
            .addedTimestamp(DEFAULT_ADDED_TIMESTAMP);
        // Add required entity
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = ProjectResourceIT.createEntity(em);
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        projectMember.setProject(project);
        return projectMember;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectMember createUpdatedEntity(EntityManager em) {
        ProjectMember projectMember = new ProjectMember()
            .additionalProjectPermissions(UPDATED_ADDITIONAL_PROJECT_PERMISSIONS)
            .roleInProject(UPDATED_ROLE_IN_PROJECT)
            .addedTimestamp(UPDATED_ADDED_TIMESTAMP);
        // Add required entity
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = ProjectResourceIT.createUpdatedEntity(em);
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        projectMember.setProject(project);
        return projectMember;
    }

    @BeforeEach
    public void initTest() {
        projectMember = createEntity(em);
    }

    @Test
    @Transactional
    void createProjectMember() throws Exception {
        int databaseSizeBeforeCreate = projectMemberRepository.findAll().size();
        // Create the ProjectMember
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);
        restProjectMemberMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ProjectMember in the database
        List<ProjectMember> projectMemberList = projectMemberRepository.findAll();
        assertThat(projectMemberList).hasSize(databaseSizeBeforeCreate + 1);
        ProjectMember testProjectMember = projectMemberList.get(projectMemberList.size() - 1);
        assertThat(testProjectMember.getAdditionalProjectPermissions()).isEqualTo(DEFAULT_ADDITIONAL_PROJECT_PERMISSIONS);
        assertThat(testProjectMember.getRoleInProject()).isEqualTo(DEFAULT_ROLE_IN_PROJECT);
        assertThat(testProjectMember.getAddedTimestamp()).isEqualTo(DEFAULT_ADDED_TIMESTAMP);

        // Validate the id for MapsId, the ids must be same
        assertThat(testProjectMember.getId()).isEqualTo(testProjectMember.getProject().getId());

        // Validate the ProjectMember in Elasticsearch
        verify(mockProjectMemberSearchRepository, times(1)).save(testProjectMember);
    }

    @Test
    @Transactional
    void createProjectMemberWithExistingId() throws Exception {
        // Create the ProjectMember with an existing ID
        projectMember.setId(1L);
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        int databaseSizeBeforeCreate = projectMemberRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectMemberMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMember in the database
        List<ProjectMember> projectMemberList = projectMemberRepository.findAll();
        assertThat(projectMemberList).hasSize(databaseSizeBeforeCreate);

        // Validate the ProjectMember in Elasticsearch
        verify(mockProjectMemberSearchRepository, times(0)).save(projectMember);
    }

    @Test
    @Transactional
    void updateProjectMemberMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);
        int databaseSizeBeforeCreate = projectMemberRepository.findAll().size();

        // Load the projectMember
        ProjectMember updatedProjectMember = projectMemberRepository.findById(projectMember.getId()).get();
        assertThat(updatedProjectMember).isNotNull();
        // Disconnect from session so that the updates on updatedProjectMember are not directly saved in db
        em.detach(updatedProjectMember);

        Project project = new Project();
        // Update the Project with new association value
        updatedProjectMember.setProject(project);
        ProjectMemberDTO updatedProjectMemberDTO = projectMemberMapper.toDto(updatedProjectMember);
        assertThat(updatedProjectMemberDTO).isNotNull();

        // Update the entity
        restProjectMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProjectMemberDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProjectMemberDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMember in the database
        List<ProjectMember> projectMemberList = projectMemberRepository.findAll();
        assertThat(projectMemberList).hasSize(databaseSizeBeforeCreate);
        ProjectMember testProjectMember = projectMemberList.get(projectMemberList.size() - 1);

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testProjectMember.getId()).isEqualTo(testProjectMember.getProject().getId());

        // Validate the ProjectMember in Elasticsearch
        verify(mockProjectMemberSearchRepository).save(projectMember);
    }

    @Test
    @Transactional
    void checkAdditionalProjectPermissionsIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectMemberRepository.findAll().size();
        // set the field null
        projectMember.setAdditionalProjectPermissions(null);

        // Create the ProjectMember, which fails.
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        restProjectMemberMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberDTO))
            )
            .andExpect(status().isBadRequest());

        List<ProjectMember> projectMemberList = projectMemberRepository.findAll();
        assertThat(projectMemberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRoleInProjectIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectMemberRepository.findAll().size();
        // set the field null
        projectMember.setRoleInProject(null);

        // Create the ProjectMember, which fails.
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        restProjectMemberMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberDTO))
            )
            .andExpect(status().isBadRequest());

        List<ProjectMember> projectMemberList = projectMemberRepository.findAll();
        assertThat(projectMemberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAddedTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectMemberRepository.findAll().size();
        // set the field null
        projectMember.setAddedTimestamp(null);

        // Create the ProjectMember, which fails.
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        restProjectMemberMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberDTO))
            )
            .andExpect(status().isBadRequest());

        List<ProjectMember> projectMemberList = projectMemberRepository.findAll();
        assertThat(projectMemberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProjectMembers() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList
        restProjectMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].additionalProjectPermissions").value(hasItem(DEFAULT_ADDITIONAL_PROJECT_PERMISSIONS.toString())))
            .andExpect(jsonPath("$.[*].roleInProject").value(hasItem(DEFAULT_ROLE_IN_PROJECT.toString())))
            .andExpect(jsonPath("$.[*].addedTimestamp").value(hasItem(sameInstant(DEFAULT_ADDED_TIMESTAMP))));
    }

    @Test
    @Transactional
    void getProjectMember() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get the projectMember
        restProjectMemberMockMvc
            .perform(get(ENTITY_API_URL_ID, projectMember.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(projectMember.getId().intValue()))
            .andExpect(jsonPath("$.additionalProjectPermissions").value(DEFAULT_ADDITIONAL_PROJECT_PERMISSIONS.toString()))
            .andExpect(jsonPath("$.roleInProject").value(DEFAULT_ROLE_IN_PROJECT.toString()))
            .andExpect(jsonPath("$.addedTimestamp").value(sameInstant(DEFAULT_ADDED_TIMESTAMP)));
    }

    @Test
    @Transactional
    void getProjectMembersByIdFiltering() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        Long id = projectMember.getId();

        defaultProjectMemberShouldBeFound("id.equals=" + id);
        defaultProjectMemberShouldNotBeFound("id.notEquals=" + id);

        defaultProjectMemberShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProjectMemberShouldNotBeFound("id.greaterThan=" + id);

        defaultProjectMemberShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProjectMemberShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProjectMembersByAdditionalProjectPermissionsIsEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where additionalProjectPermissions equals to DEFAULT_ADDITIONAL_PROJECT_PERMISSIONS
        defaultProjectMemberShouldBeFound("additionalProjectPermissions.equals=" + DEFAULT_ADDITIONAL_PROJECT_PERMISSIONS);

        // Get all the projectMemberList where additionalProjectPermissions equals to UPDATED_ADDITIONAL_PROJECT_PERMISSIONS
        defaultProjectMemberShouldNotBeFound("additionalProjectPermissions.equals=" + UPDATED_ADDITIONAL_PROJECT_PERMISSIONS);
    }

    @Test
    @Transactional
    void getAllProjectMembersByAdditionalProjectPermissionsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where additionalProjectPermissions not equals to DEFAULT_ADDITIONAL_PROJECT_PERMISSIONS
        defaultProjectMemberShouldNotBeFound("additionalProjectPermissions.notEquals=" + DEFAULT_ADDITIONAL_PROJECT_PERMISSIONS);

        // Get all the projectMemberList where additionalProjectPermissions not equals to UPDATED_ADDITIONAL_PROJECT_PERMISSIONS
        defaultProjectMemberShouldBeFound("additionalProjectPermissions.notEquals=" + UPDATED_ADDITIONAL_PROJECT_PERMISSIONS);
    }

    @Test
    @Transactional
    void getAllProjectMembersByAdditionalProjectPermissionsIsInShouldWork() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where additionalProjectPermissions in DEFAULT_ADDITIONAL_PROJECT_PERMISSIONS or UPDATED_ADDITIONAL_PROJECT_PERMISSIONS
        defaultProjectMemberShouldBeFound(
            "additionalProjectPermissions.in=" + DEFAULT_ADDITIONAL_PROJECT_PERMISSIONS + "," + UPDATED_ADDITIONAL_PROJECT_PERMISSIONS
        );

        // Get all the projectMemberList where additionalProjectPermissions equals to UPDATED_ADDITIONAL_PROJECT_PERMISSIONS
        defaultProjectMemberShouldNotBeFound("additionalProjectPermissions.in=" + UPDATED_ADDITIONAL_PROJECT_PERMISSIONS);
    }

    @Test
    @Transactional
    void getAllProjectMembersByAdditionalProjectPermissionsIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where additionalProjectPermissions is not null
        defaultProjectMemberShouldBeFound("additionalProjectPermissions.specified=true");

        // Get all the projectMemberList where additionalProjectPermissions is null
        defaultProjectMemberShouldNotBeFound("additionalProjectPermissions.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectMembersByRoleInProjectIsEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where roleInProject equals to DEFAULT_ROLE_IN_PROJECT
        defaultProjectMemberShouldBeFound("roleInProject.equals=" + DEFAULT_ROLE_IN_PROJECT);

        // Get all the projectMemberList where roleInProject equals to UPDATED_ROLE_IN_PROJECT
        defaultProjectMemberShouldNotBeFound("roleInProject.equals=" + UPDATED_ROLE_IN_PROJECT);
    }

    @Test
    @Transactional
    void getAllProjectMembersByRoleInProjectIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where roleInProject not equals to DEFAULT_ROLE_IN_PROJECT
        defaultProjectMemberShouldNotBeFound("roleInProject.notEquals=" + DEFAULT_ROLE_IN_PROJECT);

        // Get all the projectMemberList where roleInProject not equals to UPDATED_ROLE_IN_PROJECT
        defaultProjectMemberShouldBeFound("roleInProject.notEquals=" + UPDATED_ROLE_IN_PROJECT);
    }

    @Test
    @Transactional
    void getAllProjectMembersByRoleInProjectIsInShouldWork() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where roleInProject in DEFAULT_ROLE_IN_PROJECT or UPDATED_ROLE_IN_PROJECT
        defaultProjectMemberShouldBeFound("roleInProject.in=" + DEFAULT_ROLE_IN_PROJECT + "," + UPDATED_ROLE_IN_PROJECT);

        // Get all the projectMemberList where roleInProject equals to UPDATED_ROLE_IN_PROJECT
        defaultProjectMemberShouldNotBeFound("roleInProject.in=" + UPDATED_ROLE_IN_PROJECT);
    }

    @Test
    @Transactional
    void getAllProjectMembersByRoleInProjectIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where roleInProject is not null
        defaultProjectMemberShouldBeFound("roleInProject.specified=true");

        // Get all the projectMemberList where roleInProject is null
        defaultProjectMemberShouldNotBeFound("roleInProject.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectMembersByAddedTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where addedTimestamp equals to DEFAULT_ADDED_TIMESTAMP
        defaultProjectMemberShouldBeFound("addedTimestamp.equals=" + DEFAULT_ADDED_TIMESTAMP);

        // Get all the projectMemberList where addedTimestamp equals to UPDATED_ADDED_TIMESTAMP
        defaultProjectMemberShouldNotBeFound("addedTimestamp.equals=" + UPDATED_ADDED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMembersByAddedTimestampIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where addedTimestamp not equals to DEFAULT_ADDED_TIMESTAMP
        defaultProjectMemberShouldNotBeFound("addedTimestamp.notEquals=" + DEFAULT_ADDED_TIMESTAMP);

        // Get all the projectMemberList where addedTimestamp not equals to UPDATED_ADDED_TIMESTAMP
        defaultProjectMemberShouldBeFound("addedTimestamp.notEquals=" + UPDATED_ADDED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMembersByAddedTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where addedTimestamp in DEFAULT_ADDED_TIMESTAMP or UPDATED_ADDED_TIMESTAMP
        defaultProjectMemberShouldBeFound("addedTimestamp.in=" + DEFAULT_ADDED_TIMESTAMP + "," + UPDATED_ADDED_TIMESTAMP);

        // Get all the projectMemberList where addedTimestamp equals to UPDATED_ADDED_TIMESTAMP
        defaultProjectMemberShouldNotBeFound("addedTimestamp.in=" + UPDATED_ADDED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMembersByAddedTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where addedTimestamp is not null
        defaultProjectMemberShouldBeFound("addedTimestamp.specified=true");

        // Get all the projectMemberList where addedTimestamp is null
        defaultProjectMemberShouldNotBeFound("addedTimestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectMembersByAddedTimestampIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where addedTimestamp is greater than or equal to DEFAULT_ADDED_TIMESTAMP
        defaultProjectMemberShouldBeFound("addedTimestamp.greaterThanOrEqual=" + DEFAULT_ADDED_TIMESTAMP);

        // Get all the projectMemberList where addedTimestamp is greater than or equal to UPDATED_ADDED_TIMESTAMP
        defaultProjectMemberShouldNotBeFound("addedTimestamp.greaterThanOrEqual=" + UPDATED_ADDED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMembersByAddedTimestampIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where addedTimestamp is less than or equal to DEFAULT_ADDED_TIMESTAMP
        defaultProjectMemberShouldBeFound("addedTimestamp.lessThanOrEqual=" + DEFAULT_ADDED_TIMESTAMP);

        // Get all the projectMemberList where addedTimestamp is less than or equal to SMALLER_ADDED_TIMESTAMP
        defaultProjectMemberShouldNotBeFound("addedTimestamp.lessThanOrEqual=" + SMALLER_ADDED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMembersByAddedTimestampIsLessThanSomething() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where addedTimestamp is less than DEFAULT_ADDED_TIMESTAMP
        defaultProjectMemberShouldNotBeFound("addedTimestamp.lessThan=" + DEFAULT_ADDED_TIMESTAMP);

        // Get all the projectMemberList where addedTimestamp is less than UPDATED_ADDED_TIMESTAMP
        defaultProjectMemberShouldBeFound("addedTimestamp.lessThan=" + UPDATED_ADDED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMembersByAddedTimestampIsGreaterThanSomething() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where addedTimestamp is greater than DEFAULT_ADDED_TIMESTAMP
        defaultProjectMemberShouldNotBeFound("addedTimestamp.greaterThan=" + DEFAULT_ADDED_TIMESTAMP);

        // Get all the projectMemberList where addedTimestamp is greater than SMALLER_ADDED_TIMESTAMP
        defaultProjectMemberShouldBeFound("addedTimestamp.greaterThan=" + SMALLER_ADDED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllProjectMembersByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            user = UserResourceIT.createEntity(em);
            em.persist(user);
            em.flush();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        projectMember.setUser(user);
        projectMemberRepository.saveAndFlush(projectMember);
        String userId = user.getId();

        // Get all the projectMemberList where user equals to userId
        defaultProjectMemberShouldBeFound("userId.equals=" + userId);

        // Get all the projectMemberList where user equals to "invalid-id"
        defaultProjectMemberShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    @Test
    @Transactional
    void getAllProjectMembersByProjectIsEqualToSomething() throws Exception {
        // Get already existing entity
        Project project = projectMember.getProject();
        projectMemberRepository.saveAndFlush(projectMember);
        Long projectId = project.getId();

        // Get all the projectMemberList where project equals to projectId
        defaultProjectMemberShouldBeFound("projectId.equals=" + projectId);

        // Get all the projectMemberList where project equals to (projectId + 1)
        defaultProjectMemberShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectMemberShouldBeFound(String filter) throws Exception {
        restProjectMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].additionalProjectPermissions").value(hasItem(DEFAULT_ADDITIONAL_PROJECT_PERMISSIONS.toString())))
            .andExpect(jsonPath("$.[*].roleInProject").value(hasItem(DEFAULT_ROLE_IN_PROJECT.toString())))
            .andExpect(jsonPath("$.[*].addedTimestamp").value(hasItem(sameInstant(DEFAULT_ADDED_TIMESTAMP))));

        // Check, that the count call also returns 1
        restProjectMemberMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProjectMemberShouldNotBeFound(String filter) throws Exception {
        restProjectMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProjectMemberMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProjectMember() throws Exception {
        // Get the projectMember
        restProjectMemberMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProjectMember() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        int databaseSizeBeforeUpdate = projectMemberRepository.findAll().size();

        // Update the projectMember
        ProjectMember updatedProjectMember = projectMemberRepository.findById(projectMember.getId()).get();
        // Disconnect from session so that the updates on updatedProjectMember are not directly saved in db
        em.detach(updatedProjectMember);
        updatedProjectMember
            .additionalProjectPermissions(UPDATED_ADDITIONAL_PROJECT_PERMISSIONS)
            .roleInProject(UPDATED_ROLE_IN_PROJECT)
            .addedTimestamp(UPDATED_ADDED_TIMESTAMP);
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(updatedProjectMember);

        restProjectMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectMemberDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMember in the database
        List<ProjectMember> projectMemberList = projectMemberRepository.findAll();
        assertThat(projectMemberList).hasSize(databaseSizeBeforeUpdate);
        ProjectMember testProjectMember = projectMemberList.get(projectMemberList.size() - 1);
        assertThat(testProjectMember.getAdditionalProjectPermissions()).isEqualTo(UPDATED_ADDITIONAL_PROJECT_PERMISSIONS);
        assertThat(testProjectMember.getRoleInProject()).isEqualTo(UPDATED_ROLE_IN_PROJECT);
        assertThat(testProjectMember.getAddedTimestamp()).isEqualTo(UPDATED_ADDED_TIMESTAMP);

        // Validate the ProjectMember in Elasticsearch
        verify(mockProjectMemberSearchRepository).save(testProjectMember);
    }

    @Test
    @Transactional
    void putNonExistingProjectMember() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRepository.findAll().size();
        projectMember.setId(count.incrementAndGet());

        // Create the ProjectMember
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectMemberDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMember in the database
        List<ProjectMember> projectMemberList = projectMemberRepository.findAll();
        assertThat(projectMemberList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMember in Elasticsearch
        verify(mockProjectMemberSearchRepository, times(0)).save(projectMember);
    }

    @Test
    @Transactional
    void putWithIdMismatchProjectMember() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRepository.findAll().size();
        projectMember.setId(count.incrementAndGet());

        // Create the ProjectMember
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMember in the database
        List<ProjectMember> projectMemberList = projectMemberRepository.findAll();
        assertThat(projectMemberList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMember in Elasticsearch
        verify(mockProjectMemberSearchRepository, times(0)).save(projectMember);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProjectMember() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRepository.findAll().size();
        projectMember.setId(count.incrementAndGet());

        // Create the ProjectMember
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectMember in the database
        List<ProjectMember> projectMemberList = projectMemberRepository.findAll();
        assertThat(projectMemberList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMember in Elasticsearch
        verify(mockProjectMemberSearchRepository, times(0)).save(projectMember);
    }

    @Test
    @Transactional
    void partialUpdateProjectMemberWithPatch() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        int databaseSizeBeforeUpdate = projectMemberRepository.findAll().size();

        // Update the projectMember using partial update
        ProjectMember partialUpdatedProjectMember = new ProjectMember();
        partialUpdatedProjectMember.setId(projectMember.getId());

        partialUpdatedProjectMember.roleInProject(UPDATED_ROLE_IN_PROJECT);

        restProjectMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectMember.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectMember))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMember in the database
        List<ProjectMember> projectMemberList = projectMemberRepository.findAll();
        assertThat(projectMemberList).hasSize(databaseSizeBeforeUpdate);
        ProjectMember testProjectMember = projectMemberList.get(projectMemberList.size() - 1);
        assertThat(testProjectMember.getAdditionalProjectPermissions()).isEqualTo(DEFAULT_ADDITIONAL_PROJECT_PERMISSIONS);
        assertThat(testProjectMember.getRoleInProject()).isEqualTo(UPDATED_ROLE_IN_PROJECT);
        assertThat(testProjectMember.getAddedTimestamp()).isEqualTo(DEFAULT_ADDED_TIMESTAMP);
    }

    @Test
    @Transactional
    void fullUpdateProjectMemberWithPatch() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        int databaseSizeBeforeUpdate = projectMemberRepository.findAll().size();

        // Update the projectMember using partial update
        ProjectMember partialUpdatedProjectMember = new ProjectMember();
        partialUpdatedProjectMember.setId(projectMember.getId());

        partialUpdatedProjectMember
            .additionalProjectPermissions(UPDATED_ADDITIONAL_PROJECT_PERMISSIONS)
            .roleInProject(UPDATED_ROLE_IN_PROJECT)
            .addedTimestamp(UPDATED_ADDED_TIMESTAMP);

        restProjectMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectMember.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectMember))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMember in the database
        List<ProjectMember> projectMemberList = projectMemberRepository.findAll();
        assertThat(projectMemberList).hasSize(databaseSizeBeforeUpdate);
        ProjectMember testProjectMember = projectMemberList.get(projectMemberList.size() - 1);
        assertThat(testProjectMember.getAdditionalProjectPermissions()).isEqualTo(UPDATED_ADDITIONAL_PROJECT_PERMISSIONS);
        assertThat(testProjectMember.getRoleInProject()).isEqualTo(UPDATED_ROLE_IN_PROJECT);
        assertThat(testProjectMember.getAddedTimestamp()).isEqualTo(UPDATED_ADDED_TIMESTAMP);
    }

    @Test
    @Transactional
    void patchNonExistingProjectMember() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRepository.findAll().size();
        projectMember.setId(count.incrementAndGet());

        // Create the ProjectMember
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, projectMemberDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMember in the database
        List<ProjectMember> projectMemberList = projectMemberRepository.findAll();
        assertThat(projectMemberList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMember in Elasticsearch
        verify(mockProjectMemberSearchRepository, times(0)).save(projectMember);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProjectMember() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRepository.findAll().size();
        projectMember.setId(count.incrementAndGet());

        // Create the ProjectMember
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMember in the database
        List<ProjectMember> projectMemberList = projectMemberRepository.findAll();
        assertThat(projectMemberList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMember in Elasticsearch
        verify(mockProjectMemberSearchRepository, times(0)).save(projectMember);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProjectMember() throws Exception {
        int databaseSizeBeforeUpdate = projectMemberRepository.findAll().size();
        projectMember.setId(count.incrementAndGet());

        // Create the ProjectMember
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectMemberDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectMember in the database
        List<ProjectMember> projectMemberList = projectMemberRepository.findAll();
        assertThat(projectMemberList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectMember in Elasticsearch
        verify(mockProjectMemberSearchRepository, times(0)).save(projectMember);
    }

    @Test
    @Transactional
    void deleteProjectMember() throws Exception {
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);

        int databaseSizeBeforeDelete = projectMemberRepository.findAll().size();

        // Delete the projectMember
        restProjectMemberMockMvc
            .perform(delete(ENTITY_API_URL_ID, projectMember.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProjectMember> projectMemberList = projectMemberRepository.findAll();
        assertThat(projectMemberList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ProjectMember in Elasticsearch
        verify(mockProjectMemberSearchRepository, times(1)).deleteById(projectMember.getId());
    }

    @Test
    @Transactional
    void searchProjectMember() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        projectMemberRepository.saveAndFlush(projectMember);
        when(mockProjectMemberSearchRepository.search("id:" + projectMember.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(projectMember), PageRequest.of(0, 1), 1));

        // Search the projectMember
        restProjectMemberMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + projectMember.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].additionalProjectPermissions").value(hasItem(DEFAULT_ADDITIONAL_PROJECT_PERMISSIONS.toString())))
            .andExpect(jsonPath("$.[*].roleInProject").value(hasItem(DEFAULT_ROLE_IN_PROJECT.toString())))
            .andExpect(jsonPath("$.[*].addedTimestamp").value(hasItem(sameInstant(DEFAULT_ADDED_TIMESTAMP))));
    }
}
