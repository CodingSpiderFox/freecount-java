package org.codingspiderfox.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.codingspiderfox.repository.ProjectSettingsRepository;
import org.codingspiderfox.service.ProjectSettingsQueryService;
import org.codingspiderfox.service.ProjectSettingsService;
import org.codingspiderfox.service.criteria.ProjectSettingsCriteria;
import org.codingspiderfox.service.dto.ProjectSettingsDTO;
import org.codingspiderfox.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.codingspiderfox.domain.ProjectSettings}.
 */
@RestController
@RequestMapping("/api")
public class ProjectSettingsResource {

    private final Logger log = LoggerFactory.getLogger(ProjectSettingsResource.class);

    private static final String ENTITY_NAME = "projectSettings";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectSettingsService projectSettingsService;

    private final ProjectSettingsRepository projectSettingsRepository;

    private final ProjectSettingsQueryService projectSettingsQueryService;

    public ProjectSettingsResource(
        ProjectSettingsService projectSettingsService,
        ProjectSettingsRepository projectSettingsRepository,
        ProjectSettingsQueryService projectSettingsQueryService
    ) {
        this.projectSettingsService = projectSettingsService;
        this.projectSettingsRepository = projectSettingsRepository;
        this.projectSettingsQueryService = projectSettingsQueryService;
    }

    /**
     * {@code POST  /project-settings} : Create a new projectSettings.
     *
     * @param projectSettingsDTO the projectSettingsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectSettingsDTO, or with status {@code 400 (Bad Request)} if the projectSettings has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/project-settings")
    public ResponseEntity<ProjectSettingsDTO> createProjectSettings(@Valid @RequestBody ProjectSettingsDTO projectSettingsDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProjectSettings : {}", projectSettingsDTO);
        if (projectSettingsDTO.getId() != null) {
            throw new BadRequestAlertException("A new projectSettings cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(projectSettingsDTO.getProject())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        ProjectSettingsDTO result = projectSettingsService.save(projectSettingsDTO);
        return ResponseEntity
            .created(new URI("/api/project-settings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /project-settings/:id} : Updates an existing projectSettings.
     *
     * @param id the id of the projectSettingsDTO to save.
     * @param projectSettingsDTO the projectSettingsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectSettingsDTO,
     * or with status {@code 400 (Bad Request)} if the projectSettingsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectSettingsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/project-settings/{id}")
    public ResponseEntity<ProjectSettingsDTO> updateProjectSettings(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectSettingsDTO projectSettingsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProjectSettings : {}, {}", id, projectSettingsDTO);
        if (projectSettingsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectSettingsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectSettingsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProjectSettingsDTO result = projectSettingsService.save(projectSettingsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectSettingsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /project-settings/:id} : Partial updates given fields of an existing projectSettings, field will ignore if it is null
     *
     * @param id the id of the projectSettingsDTO to save.
     * @param projectSettingsDTO the projectSettingsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectSettingsDTO,
     * or with status {@code 400 (Bad Request)} if the projectSettingsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projectSettingsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectSettingsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/project-settings/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProjectSettingsDTO> partialUpdateProjectSettings(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectSettingsDTO projectSettingsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProjectSettings partially : {}, {}", id, projectSettingsDTO);
        if (projectSettingsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectSettingsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectSettingsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProjectSettingsDTO> result = projectSettingsService.partialUpdate(projectSettingsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectSettingsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /project-settings} : get all the projectSettings.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectSettings in body.
     */
    @GetMapping("/project-settings")
    public ResponseEntity<List<ProjectSettingsDTO>> getAllProjectSettings(ProjectSettingsCriteria criteria, Pageable pageable) {
        log.debug("REST request to get ProjectSettings by criteria: {}", criteria);
        Page<ProjectSettingsDTO> page = projectSettingsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /project-settings/count} : count all the projectSettings.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/project-settings/count")
    public ResponseEntity<Long> countProjectSettings(ProjectSettingsCriteria criteria) {
        log.debug("REST request to count ProjectSettings by criteria: {}", criteria);
        return ResponseEntity.ok().body(projectSettingsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /project-settings/:id} : get the "id" projectSettings.
     *
     * @param id the id of the projectSettingsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectSettingsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/project-settings/{id}")
    public ResponseEntity<ProjectSettingsDTO> getProjectSettings(@PathVariable Long id) {
        log.debug("REST request to get ProjectSettings : {}", id);
        Optional<ProjectSettingsDTO> projectSettingsDTO = projectSettingsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectSettingsDTO);
    }

    /**
     * {@code DELETE  /project-settings/:id} : delete the "id" projectSettings.
     *
     * @param id the id of the projectSettingsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/project-settings/{id}")
    public ResponseEntity<Void> deleteProjectSettings(@PathVariable Long id) {
        log.debug("REST request to delete ProjectSettings : {}", id);
        projectSettingsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/project-settings?query=:query} : search for the projectSettings corresponding
     * to the query.
     *
     * @param query the query of the projectSettings search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/project-settings")
    public ResponseEntity<List<ProjectSettingsDTO>> searchProjectSettings(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of ProjectSettings for query {}", query);
        Page<ProjectSettingsDTO> page = projectSettingsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
