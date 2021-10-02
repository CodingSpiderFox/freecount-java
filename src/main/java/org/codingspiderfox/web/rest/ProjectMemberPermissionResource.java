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
import org.codingspiderfox.repository.ProjectMemberPermissionRepository;
import org.codingspiderfox.service.ProjectMemberPermissionQueryService;
import org.codingspiderfox.service.ProjectMemberPermissionService;
import org.codingspiderfox.service.criteria.ProjectMemberPermissionCriteria;
import org.codingspiderfox.service.dto.ProjectMemberPermissionDTO;
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
 * REST controller for managing {@link org.codingspiderfox.domain.ProjectMemberPermission}.
 */
@RestController
@RequestMapping("/api")
public class ProjectMemberPermissionResource {

    private final Logger log = LoggerFactory.getLogger(ProjectMemberPermissionResource.class);

    private static final String ENTITY_NAME = "projectMemberPermission";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectMemberPermissionService projectMemberPermissionService;

    private final ProjectMemberPermissionRepository projectMemberPermissionRepository;

    private final ProjectMemberPermissionQueryService projectMemberPermissionQueryService;

    public ProjectMemberPermissionResource(
        ProjectMemberPermissionService projectMemberPermissionService,
        ProjectMemberPermissionRepository projectMemberPermissionRepository,
        ProjectMemberPermissionQueryService projectMemberPermissionQueryService
    ) {
        this.projectMemberPermissionService = projectMemberPermissionService;
        this.projectMemberPermissionRepository = projectMemberPermissionRepository;
        this.projectMemberPermissionQueryService = projectMemberPermissionQueryService;
    }

    /**
     * {@code POST  /project-member-permissions} : Create a new projectMemberPermission.
     *
     * @param projectMemberPermissionDTO the projectMemberPermissionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectMemberPermissionDTO, or with status {@code 400 (Bad Request)} if the projectMemberPermission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/project-member-permissions")
    public ResponseEntity<ProjectMemberPermissionDTO> createProjectMemberPermission(
        @Valid @RequestBody ProjectMemberPermissionDTO projectMemberPermissionDTO
    ) throws URISyntaxException {
        log.debug("REST request to save ProjectMemberPermission : {}", projectMemberPermissionDTO);
        if (projectMemberPermissionDTO.getId() != null) {
            throw new BadRequestAlertException("A new projectMemberPermission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProjectMemberPermissionDTO result = projectMemberPermissionService.save(projectMemberPermissionDTO);
        return ResponseEntity
            .created(new URI("/api/project-member-permissions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /project-member-permissions/:id} : Updates an existing projectMemberPermission.
     *
     * @param id the id of the projectMemberPermissionDTO to save.
     * @param projectMemberPermissionDTO the projectMemberPermissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectMemberPermissionDTO,
     * or with status {@code 400 (Bad Request)} if the projectMemberPermissionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectMemberPermissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/project-member-permissions/{id}")
    public ResponseEntity<ProjectMemberPermissionDTO> updateProjectMemberPermission(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectMemberPermissionDTO projectMemberPermissionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProjectMemberPermission : {}, {}", id, projectMemberPermissionDTO);
        if (projectMemberPermissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectMemberPermissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectMemberPermissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProjectMemberPermissionDTO result = projectMemberPermissionService.save(projectMemberPermissionDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectMemberPermissionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /project-member-permissions/:id} : Partial updates given fields of an existing projectMemberPermission, field will ignore if it is null
     *
     * @param id the id of the projectMemberPermissionDTO to save.
     * @param projectMemberPermissionDTO the projectMemberPermissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectMemberPermissionDTO,
     * or with status {@code 400 (Bad Request)} if the projectMemberPermissionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projectMemberPermissionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectMemberPermissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/project-member-permissions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProjectMemberPermissionDTO> partialUpdateProjectMemberPermission(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectMemberPermissionDTO projectMemberPermissionDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProjectMemberPermission partially : {}, {}", id, projectMemberPermissionDTO);
        if (projectMemberPermissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectMemberPermissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectMemberPermissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProjectMemberPermissionDTO> result = projectMemberPermissionService.partialUpdate(projectMemberPermissionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectMemberPermissionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /project-member-permissions} : get all the projectMemberPermissions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectMemberPermissions in body.
     */
    @GetMapping("/project-member-permissions")
    public ResponseEntity<List<ProjectMemberPermissionDTO>> getAllProjectMemberPermissions(
        ProjectMemberPermissionCriteria criteria,
        Pageable pageable
    ) {
        log.debug("REST request to get ProjectMemberPermissions by criteria: {}", criteria);
        Page<ProjectMemberPermissionDTO> page = projectMemberPermissionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /project-member-permissions/count} : count all the projectMemberPermissions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/project-member-permissions/count")
    public ResponseEntity<Long> countProjectMemberPermissions(ProjectMemberPermissionCriteria criteria) {
        log.debug("REST request to count ProjectMemberPermissions by criteria: {}", criteria);
        return ResponseEntity.ok().body(projectMemberPermissionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /project-member-permissions/:id} : get the "id" projectMemberPermission.
     *
     * @param id the id of the projectMemberPermissionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectMemberPermissionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/project-member-permissions/{id}")
    public ResponseEntity<ProjectMemberPermissionDTO> getProjectMemberPermission(@PathVariable Long id) {
        log.debug("REST request to get ProjectMemberPermission : {}", id);
        Optional<ProjectMemberPermissionDTO> projectMemberPermissionDTO = projectMemberPermissionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectMemberPermissionDTO);
    }

    /**
     * {@code DELETE  /project-member-permissions/:id} : delete the "id" projectMemberPermission.
     *
     * @param id the id of the projectMemberPermissionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/project-member-permissions/{id}")
    public ResponseEntity<Void> deleteProjectMemberPermission(@PathVariable Long id) {
        log.debug("REST request to delete ProjectMemberPermission : {}", id);
        projectMemberPermissionService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/project-member-permissions?query=:query} : search for the projectMemberPermission corresponding
     * to the query.
     *
     * @param query the query of the projectMemberPermission search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/project-member-permissions")
    public ResponseEntity<List<ProjectMemberPermissionDTO>> searchProjectMemberPermissions(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of ProjectMemberPermissions for query {}", query);
        Page<ProjectMemberPermissionDTO> page = projectMemberPermissionService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
