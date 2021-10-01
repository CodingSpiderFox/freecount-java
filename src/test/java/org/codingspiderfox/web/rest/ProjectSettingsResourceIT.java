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
import org.codingspiderfox.domain.Project;
import org.codingspiderfox.domain.ProjectSettings;
import org.codingspiderfox.repository.ProjectSettingsRepository;
import org.codingspiderfox.repository.search.ProjectSettingsSearchRepository;
import org.codingspiderfox.service.criteria.ProjectSettingsCriteria;
import org.codingspiderfox.service.dto.ProjectSettingsDTO;
import org.codingspiderfox.service.mapper.ProjectSettingsMapper;
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
 * Integration tests for the {@link ProjectSettingsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProjectSettingsResourceIT {

    private static final Boolean DEFAULT_MUST_PROVIDE_BILL_COPY_BY_DEFAULT = false;
    private static final Boolean UPDATED_MUST_PROVIDE_BILL_COPY_BY_DEFAULT = true;

    private static final String ENTITY_API_URL = "/api/project-settings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/project-settings";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjectSettingsRepository projectSettingsRepository;

    @Autowired
    private ProjectSettingsMapper projectSettingsMapper;

    /**
     * This repository is mocked in the org.codingspiderfox.repository.search test package.
     *
     * @see org.codingspiderfox.repository.search.ProjectSettingsSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProjectSettingsSearchRepository mockProjectSettingsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjectSettingsMockMvc;

    private ProjectSettings projectSettings;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectSettings createEntity(EntityManager em) {
        ProjectSettings projectSettings = new ProjectSettings().mustProvideBillCopyByDefault(DEFAULT_MUST_PROVIDE_BILL_COPY_BY_DEFAULT);
        // Add required entity
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = ProjectResourceIT.createEntity(em);
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        projectSettings.setProject(project);
        return projectSettings;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectSettings createUpdatedEntity(EntityManager em) {
        ProjectSettings projectSettings = new ProjectSettings().mustProvideBillCopyByDefault(UPDATED_MUST_PROVIDE_BILL_COPY_BY_DEFAULT);
        // Add required entity
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = ProjectResourceIT.createUpdatedEntity(em);
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        projectSettings.setProject(project);
        return projectSettings;
    }

    @BeforeEach
    public void initTest() {
        projectSettings = createEntity(em);
    }

    @Test
    @Transactional
    void createProjectSettings() throws Exception {
        int databaseSizeBeforeCreate = projectSettingsRepository.findAll().size();
        // Create the ProjectSettings
        ProjectSettingsDTO projectSettingsDTO = projectSettingsMapper.toDto(projectSettings);
        restProjectSettingsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectSettingsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ProjectSettings in the database
        List<ProjectSettings> projectSettingsList = projectSettingsRepository.findAll();
        assertThat(projectSettingsList).hasSize(databaseSizeBeforeCreate + 1);
        ProjectSettings testProjectSettings = projectSettingsList.get(projectSettingsList.size() - 1);
        assertThat(testProjectSettings.getMustProvideBillCopyByDefault()).isEqualTo(DEFAULT_MUST_PROVIDE_BILL_COPY_BY_DEFAULT);

        // Validate the id for MapsId, the ids must be same
        assertThat(testProjectSettings.getId()).isEqualTo(testProjectSettings.getProject().getId());

        // Validate the ProjectSettings in Elasticsearch
        verify(mockProjectSettingsSearchRepository, times(1)).save(testProjectSettings);
    }

    @Test
    @Transactional
    void createProjectSettingsWithExistingId() throws Exception {
        // Create the ProjectSettings with an existing ID
        projectSettings.setId(1L);
        ProjectSettingsDTO projectSettingsDTO = projectSettingsMapper.toDto(projectSettings);

        int databaseSizeBeforeCreate = projectSettingsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectSettingsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectSettingsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectSettings in the database
        List<ProjectSettings> projectSettingsList = projectSettingsRepository.findAll();
        assertThat(projectSettingsList).hasSize(databaseSizeBeforeCreate);

        // Validate the ProjectSettings in Elasticsearch
        verify(mockProjectSettingsSearchRepository, times(0)).save(projectSettings);
    }

    @Test
    @Transactional
    void updateProjectSettingsMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        projectSettingsRepository.saveAndFlush(projectSettings);
        int databaseSizeBeforeCreate = projectSettingsRepository.findAll().size();

        // Add a new parent entity
        Project project = ProjectResourceIT.createUpdatedEntity(em);
        em.persist(project);
        em.flush();

        // Load the projectSettings
        ProjectSettings updatedProjectSettings = projectSettingsRepository.findById(projectSettings.getId()).get();
        assertThat(updatedProjectSettings).isNotNull();
        // Disconnect from session so that the updates on updatedProjectSettings are not directly saved in db
        em.detach(updatedProjectSettings);

        // Update the Project with new association value
        updatedProjectSettings.setProject(project);
        ProjectSettingsDTO updatedProjectSettingsDTO = projectSettingsMapper.toDto(updatedProjectSettings);
        assertThat(updatedProjectSettingsDTO).isNotNull();

        // Update the entity
        restProjectSettingsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProjectSettingsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProjectSettingsDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProjectSettings in the database
        List<ProjectSettings> projectSettingsList = projectSettingsRepository.findAll();
        assertThat(projectSettingsList).hasSize(databaseSizeBeforeCreate);
        ProjectSettings testProjectSettings = projectSettingsList.get(projectSettingsList.size() - 1);

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testProjectSettings.getId()).isEqualTo(testProjectSettings.getProject().getId());

        // Validate the ProjectSettings in Elasticsearch
        verify(mockProjectSettingsSearchRepository).save(projectSettings);
    }

    @Test
    @Transactional
    void checkMustProvideBillCopyByDefaultIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectSettingsRepository.findAll().size();
        // set the field null
        projectSettings.setMustProvideBillCopyByDefault(null);

        // Create the ProjectSettings, which fails.
        ProjectSettingsDTO projectSettingsDTO = projectSettingsMapper.toDto(projectSettings);

        restProjectSettingsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectSettingsDTO))
            )
            .andExpect(status().isBadRequest());

        List<ProjectSettings> projectSettingsList = projectSettingsRepository.findAll();
        assertThat(projectSettingsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProjectSettings() throws Exception {
        // Initialize the database
        projectSettingsRepository.saveAndFlush(projectSettings);

        // Get all the projectSettingsList
        restProjectSettingsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectSettings.getId().intValue())))
            .andExpect(
                jsonPath("$.[*].mustProvideBillCopyByDefault").value(hasItem(DEFAULT_MUST_PROVIDE_BILL_COPY_BY_DEFAULT.booleanValue()))
            );
    }

    @Test
    @Transactional
    void getProjectSettings() throws Exception {
        // Initialize the database
        projectSettingsRepository.saveAndFlush(projectSettings);

        // Get the projectSettings
        restProjectSettingsMockMvc
            .perform(get(ENTITY_API_URL_ID, projectSettings.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(projectSettings.getId().intValue()))
            .andExpect(jsonPath("$.mustProvideBillCopyByDefault").value(DEFAULT_MUST_PROVIDE_BILL_COPY_BY_DEFAULT.booleanValue()));
    }

    @Test
    @Transactional
    void getProjectSettingsByIdFiltering() throws Exception {
        // Initialize the database
        projectSettingsRepository.saveAndFlush(projectSettings);

        Long id = projectSettings.getId();

        defaultProjectSettingsShouldBeFound("id.equals=" + id);
        defaultProjectSettingsShouldNotBeFound("id.notEquals=" + id);

        defaultProjectSettingsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProjectSettingsShouldNotBeFound("id.greaterThan=" + id);

        defaultProjectSettingsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProjectSettingsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProjectSettingsByMustProvideBillCopyByDefaultIsEqualToSomething() throws Exception {
        // Initialize the database
        projectSettingsRepository.saveAndFlush(projectSettings);

        // Get all the projectSettingsList where mustProvideBillCopyByDefault equals to DEFAULT_MUST_PROVIDE_BILL_COPY_BY_DEFAULT
        defaultProjectSettingsShouldBeFound("mustProvideBillCopyByDefault.equals=" + DEFAULT_MUST_PROVIDE_BILL_COPY_BY_DEFAULT);

        // Get all the projectSettingsList where mustProvideBillCopyByDefault equals to UPDATED_MUST_PROVIDE_BILL_COPY_BY_DEFAULT
        defaultProjectSettingsShouldNotBeFound("mustProvideBillCopyByDefault.equals=" + UPDATED_MUST_PROVIDE_BILL_COPY_BY_DEFAULT);
    }

    @Test
    @Transactional
    void getAllProjectSettingsByMustProvideBillCopyByDefaultIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectSettingsRepository.saveAndFlush(projectSettings);

        // Get all the projectSettingsList where mustProvideBillCopyByDefault not equals to DEFAULT_MUST_PROVIDE_BILL_COPY_BY_DEFAULT
        defaultProjectSettingsShouldNotBeFound("mustProvideBillCopyByDefault.notEquals=" + DEFAULT_MUST_PROVIDE_BILL_COPY_BY_DEFAULT);

        // Get all the projectSettingsList where mustProvideBillCopyByDefault not equals to UPDATED_MUST_PROVIDE_BILL_COPY_BY_DEFAULT
        defaultProjectSettingsShouldBeFound("mustProvideBillCopyByDefault.notEquals=" + UPDATED_MUST_PROVIDE_BILL_COPY_BY_DEFAULT);
    }

    @Test
    @Transactional
    void getAllProjectSettingsByMustProvideBillCopyByDefaultIsInShouldWork() throws Exception {
        // Initialize the database
        projectSettingsRepository.saveAndFlush(projectSettings);

        // Get all the projectSettingsList where mustProvideBillCopyByDefault in DEFAULT_MUST_PROVIDE_BILL_COPY_BY_DEFAULT or UPDATED_MUST_PROVIDE_BILL_COPY_BY_DEFAULT
        defaultProjectSettingsShouldBeFound(
            "mustProvideBillCopyByDefault.in=" + DEFAULT_MUST_PROVIDE_BILL_COPY_BY_DEFAULT + "," + UPDATED_MUST_PROVIDE_BILL_COPY_BY_DEFAULT
        );

        // Get all the projectSettingsList where mustProvideBillCopyByDefault equals to UPDATED_MUST_PROVIDE_BILL_COPY_BY_DEFAULT
        defaultProjectSettingsShouldNotBeFound("mustProvideBillCopyByDefault.in=" + UPDATED_MUST_PROVIDE_BILL_COPY_BY_DEFAULT);
    }

    @Test
    @Transactional
    void getAllProjectSettingsByMustProvideBillCopyByDefaultIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectSettingsRepository.saveAndFlush(projectSettings);

        // Get all the projectSettingsList where mustProvideBillCopyByDefault is not null
        defaultProjectSettingsShouldBeFound("mustProvideBillCopyByDefault.specified=true");

        // Get all the projectSettingsList where mustProvideBillCopyByDefault is null
        defaultProjectSettingsShouldNotBeFound("mustProvideBillCopyByDefault.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectSettingsByProjectIsEqualToSomething() throws Exception {
        // Get already existing entity
        Project project = projectSettings.getProject();
        projectSettingsRepository.saveAndFlush(projectSettings);
        Long projectId = project.getId();

        // Get all the projectSettingsList where project equals to projectId
        defaultProjectSettingsShouldBeFound("projectId.equals=" + projectId);

        // Get all the projectSettingsList where project equals to (projectId + 1)
        defaultProjectSettingsShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectSettingsShouldBeFound(String filter) throws Exception {
        restProjectSettingsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectSettings.getId().intValue())))
            .andExpect(
                jsonPath("$.[*].mustProvideBillCopyByDefault").value(hasItem(DEFAULT_MUST_PROVIDE_BILL_COPY_BY_DEFAULT.booleanValue()))
            );

        // Check, that the count call also returns 1
        restProjectSettingsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProjectSettingsShouldNotBeFound(String filter) throws Exception {
        restProjectSettingsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProjectSettingsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProjectSettings() throws Exception {
        // Get the projectSettings
        restProjectSettingsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProjectSettings() throws Exception {
        // Initialize the database
        projectSettingsRepository.saveAndFlush(projectSettings);

        int databaseSizeBeforeUpdate = projectSettingsRepository.findAll().size();

        // Update the projectSettings
        ProjectSettings updatedProjectSettings = projectSettingsRepository.findById(projectSettings.getId()).get();
        // Disconnect from session so that the updates on updatedProjectSettings are not directly saved in db
        em.detach(updatedProjectSettings);
        updatedProjectSettings.mustProvideBillCopyByDefault(UPDATED_MUST_PROVIDE_BILL_COPY_BY_DEFAULT);
        ProjectSettingsDTO projectSettingsDTO = projectSettingsMapper.toDto(updatedProjectSettings);

        restProjectSettingsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectSettingsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectSettingsDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProjectSettings in the database
        List<ProjectSettings> projectSettingsList = projectSettingsRepository.findAll();
        assertThat(projectSettingsList).hasSize(databaseSizeBeforeUpdate);
        ProjectSettings testProjectSettings = projectSettingsList.get(projectSettingsList.size() - 1);
        assertThat(testProjectSettings.getMustProvideBillCopyByDefault()).isEqualTo(UPDATED_MUST_PROVIDE_BILL_COPY_BY_DEFAULT);

        // Validate the ProjectSettings in Elasticsearch
        verify(mockProjectSettingsSearchRepository).save(testProjectSettings);
    }

    @Test
    @Transactional
    void putNonExistingProjectSettings() throws Exception {
        int databaseSizeBeforeUpdate = projectSettingsRepository.findAll().size();
        projectSettings.setId(count.incrementAndGet());

        // Create the ProjectSettings
        ProjectSettingsDTO projectSettingsDTO = projectSettingsMapper.toDto(projectSettings);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectSettingsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectSettingsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectSettingsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectSettings in the database
        List<ProjectSettings> projectSettingsList = projectSettingsRepository.findAll();
        assertThat(projectSettingsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectSettings in Elasticsearch
        verify(mockProjectSettingsSearchRepository, times(0)).save(projectSettings);
    }

    @Test
    @Transactional
    void putWithIdMismatchProjectSettings() throws Exception {
        int databaseSizeBeforeUpdate = projectSettingsRepository.findAll().size();
        projectSettings.setId(count.incrementAndGet());

        // Create the ProjectSettings
        ProjectSettingsDTO projectSettingsDTO = projectSettingsMapper.toDto(projectSettings);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectSettingsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectSettingsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectSettings in the database
        List<ProjectSettings> projectSettingsList = projectSettingsRepository.findAll();
        assertThat(projectSettingsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectSettings in Elasticsearch
        verify(mockProjectSettingsSearchRepository, times(0)).save(projectSettings);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProjectSettings() throws Exception {
        int databaseSizeBeforeUpdate = projectSettingsRepository.findAll().size();
        projectSettings.setId(count.incrementAndGet());

        // Create the ProjectSettings
        ProjectSettingsDTO projectSettingsDTO = projectSettingsMapper.toDto(projectSettings);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectSettingsMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectSettingsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectSettings in the database
        List<ProjectSettings> projectSettingsList = projectSettingsRepository.findAll();
        assertThat(projectSettingsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectSettings in Elasticsearch
        verify(mockProjectSettingsSearchRepository, times(0)).save(projectSettings);
    }

    @Test
    @Transactional
    void partialUpdateProjectSettingsWithPatch() throws Exception {
        // Initialize the database
        projectSettingsRepository.saveAndFlush(projectSettings);

        int databaseSizeBeforeUpdate = projectSettingsRepository.findAll().size();

        // Update the projectSettings using partial update
        ProjectSettings partialUpdatedProjectSettings = new ProjectSettings();
        partialUpdatedProjectSettings.setId(projectSettings.getId());

        partialUpdatedProjectSettings.mustProvideBillCopyByDefault(UPDATED_MUST_PROVIDE_BILL_COPY_BY_DEFAULT);

        restProjectSettingsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectSettings.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectSettings))
            )
            .andExpect(status().isOk());

        // Validate the ProjectSettings in the database
        List<ProjectSettings> projectSettingsList = projectSettingsRepository.findAll();
        assertThat(projectSettingsList).hasSize(databaseSizeBeforeUpdate);
        ProjectSettings testProjectSettings = projectSettingsList.get(projectSettingsList.size() - 1);
        assertThat(testProjectSettings.getMustProvideBillCopyByDefault()).isEqualTo(UPDATED_MUST_PROVIDE_BILL_COPY_BY_DEFAULT);
    }

    @Test
    @Transactional
    void fullUpdateProjectSettingsWithPatch() throws Exception {
        // Initialize the database
        projectSettingsRepository.saveAndFlush(projectSettings);

        int databaseSizeBeforeUpdate = projectSettingsRepository.findAll().size();

        // Update the projectSettings using partial update
        ProjectSettings partialUpdatedProjectSettings = new ProjectSettings();
        partialUpdatedProjectSettings.setId(projectSettings.getId());

        partialUpdatedProjectSettings.mustProvideBillCopyByDefault(UPDATED_MUST_PROVIDE_BILL_COPY_BY_DEFAULT);

        restProjectSettingsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectSettings.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjectSettings))
            )
            .andExpect(status().isOk());

        // Validate the ProjectSettings in the database
        List<ProjectSettings> projectSettingsList = projectSettingsRepository.findAll();
        assertThat(projectSettingsList).hasSize(databaseSizeBeforeUpdate);
        ProjectSettings testProjectSettings = projectSettingsList.get(projectSettingsList.size() - 1);
        assertThat(testProjectSettings.getMustProvideBillCopyByDefault()).isEqualTo(UPDATED_MUST_PROVIDE_BILL_COPY_BY_DEFAULT);
    }

    @Test
    @Transactional
    void patchNonExistingProjectSettings() throws Exception {
        int databaseSizeBeforeUpdate = projectSettingsRepository.findAll().size();
        projectSettings.setId(count.incrementAndGet());

        // Create the ProjectSettings
        ProjectSettingsDTO projectSettingsDTO = projectSettingsMapper.toDto(projectSettings);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectSettingsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, projectSettingsDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectSettingsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectSettings in the database
        List<ProjectSettings> projectSettingsList = projectSettingsRepository.findAll();
        assertThat(projectSettingsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectSettings in Elasticsearch
        verify(mockProjectSettingsSearchRepository, times(0)).save(projectSettings);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProjectSettings() throws Exception {
        int databaseSizeBeforeUpdate = projectSettingsRepository.findAll().size();
        projectSettings.setId(count.incrementAndGet());

        // Create the ProjectSettings
        ProjectSettingsDTO projectSettingsDTO = projectSettingsMapper.toDto(projectSettings);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectSettingsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectSettingsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectSettings in the database
        List<ProjectSettings> projectSettingsList = projectSettingsRepository.findAll();
        assertThat(projectSettingsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectSettings in Elasticsearch
        verify(mockProjectSettingsSearchRepository, times(0)).save(projectSettings);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProjectSettings() throws Exception {
        int databaseSizeBeforeUpdate = projectSettingsRepository.findAll().size();
        projectSettings.setId(count.incrementAndGet());

        // Create the ProjectSettings
        ProjectSettingsDTO projectSettingsDTO = projectSettingsMapper.toDto(projectSettings);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectSettingsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projectSettingsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectSettings in the database
        List<ProjectSettings> projectSettingsList = projectSettingsRepository.findAll();
        assertThat(projectSettingsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProjectSettings in Elasticsearch
        verify(mockProjectSettingsSearchRepository, times(0)).save(projectSettings);
    }

    @Test
    @Transactional
    void deleteProjectSettings() throws Exception {
        // Initialize the database
        projectSettingsRepository.saveAndFlush(projectSettings);

        int databaseSizeBeforeDelete = projectSettingsRepository.findAll().size();

        // Delete the projectSettings
        restProjectSettingsMockMvc
            .perform(delete(ENTITY_API_URL_ID, projectSettings.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProjectSettings> projectSettingsList = projectSettingsRepository.findAll();
        assertThat(projectSettingsList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ProjectSettings in Elasticsearch
        verify(mockProjectSettingsSearchRepository, times(1)).deleteById(projectSettings.getId());
    }

    @Test
    @Transactional
    void searchProjectSettings() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        projectSettingsRepository.saveAndFlush(projectSettings);
        when(mockProjectSettingsSearchRepository.search("id:" + projectSettings.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(projectSettings), PageRequest.of(0, 1), 1));

        // Search the projectSettings
        restProjectSettingsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + projectSettings.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectSettings.getId().intValue())))
            .andExpect(
                jsonPath("$.[*].mustProvideBillCopyByDefault").value(hasItem(DEFAULT_MUST_PROVIDE_BILL_COPY_BY_DEFAULT.booleanValue()))
            );
    }
}
