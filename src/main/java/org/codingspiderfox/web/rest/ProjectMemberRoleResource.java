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
import org.codingspiderfox.repository.ProjectMemberRoleRepository;
import org.codingspiderfox.service.ProjectMemberRoleQueryService;
import org.codingspiderfox.service.ProjectMemberRoleService;
import org.codingspiderfox.service.criteria.ProjectMemberRoleCriteria;
import org.codingspiderfox.service.dto.ProjectMemberRoleDTO;
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
 * REST controller for managing {@link org.codingspiderfox.domain.ProjectMemberRole}.
 */
@RestController
@RequestMapping("/api")
public class ProjectMemberRoleResource {

    private final Logger log = LoggerFactory.getLogger(ProjectMemberRoleResource.class);

    private static final String ENTITY_NAME = "projectMemberRole";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectMemberRoleService projectMemberRoleService;

    private final ProjectMemberRoleRepository projectMemberRoleRepository;

    private final ProjectMemberRoleQueryService projectMemberRoleQueryService;

    public ProjectMemberRoleResource(
        ProjectMemberRoleService projectMemberRoleService,
        ProjectMemberRoleRepository projectMemberRoleRepository,
        ProjectMemberRoleQueryService projectMemberRoleQueryService
    ) {
        this.projectMemberRoleService = projectMemberRoleService;
        this.projectMemberRoleRepository = projectMemberRoleRepository;
        this.projectMemberRoleQueryService = projectMemberRoleQueryService;
    }

    /**
     * {@code POST  /project-member-roles} : Create a new projectMemberRole.
     *
     * @param projectMemberRoleDTO the projectMemberRoleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectMemberRoleDTO, or with status {@code 400 (Bad Request)} if the projectMemberRole has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/project-member-roles")
    public ResponseEntity<ProjectMemberRoleDTO> createProjectMemberRole(@Valid @RequestBody ProjectMemberRoleDTO projectMemberRoleDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProjectMemberRole : {}", projectMemberRoleDTO);
        if (projectMemberRoleDTO.getId() != null) {
            throw new BadRequestAlertException("A new projectMemberRole cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProjectMemberRoleDTO result = projectMemberRoleService.save(projectMemberRoleDTO);
        return ResponseEntity
            .created(new URI("/api/project-member-roles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /project-member-roles/:id} : Updates an existing projectMemberRole.
     *
     * @param id the id of the projectMemberRoleDTO to save.
     * @param projectMemberRoleDTO the projectMemberRoleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectMemberRoleDTO,
     * or with status {@code 400 (Bad Request)} if the projectMemberRoleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectMemberRoleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/project-member-roles/{id}")
    public ResponseEntity<ProjectMemberRoleDTO> updateProjectMemberRole(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectMemberRoleDTO projectMemberRoleDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProjectMemberRole : {}, {}", id, projectMemberRoleDTO);
        if (projectMemberRoleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectMemberRoleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectMemberRoleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProjectMemberRoleDTO result = projectMemberRoleService.save(projectMemberRoleDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectMemberRoleDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /project-member-roles/:id} : Partial updates given fields of an existing projectMemberRole, field will ignore if it is null
     *
     * @param id the id of the projectMemberRoleDTO to save.
     * @param projectMemberRoleDTO the projectMemberRoleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectMemberRoleDTO,
     * or with status {@code 400 (Bad Request)} if the projectMemberRoleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projectMemberRoleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectMemberRoleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/project-member-roles/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProjectMemberRoleDTO> partialUpdateProjectMemberRole(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectMemberRoleDTO projectMemberRoleDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProjectMemberRole partially : {}, {}", id, projectMemberRoleDTO);
        if (projectMemberRoleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectMemberRoleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectMemberRoleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProjectMemberRoleDTO> result = projectMemberRoleService.partialUpdate(projectMemberRoleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectMemberRoleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /project-member-roles} : get all the projectMemberRoles.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectMemberRoles in body.
     */
    @GetMapping("/project-member-roles")
    public ResponseEntity<List<ProjectMemberRoleDTO>> getAllProjectMemberRoles(ProjectMemberRoleCriteria criteria, Pageable pageable) {
        log.debug("REST request to get ProjectMemberRoles by criteria: {}", criteria);
        Page<ProjectMemberRoleDTO> page = projectMemberRoleQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /project-member-roles/count} : count all the projectMemberRoles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/project-member-roles/count")
    public ResponseEntity<Long> countProjectMemberRoles(ProjectMemberRoleCriteria criteria) {
        log.debug("REST request to count ProjectMemberRoles by criteria: {}", criteria);
        return ResponseEntity.ok().body(projectMemberRoleQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /project-member-roles/:id} : get the "id" projectMemberRole.
     *
     * @param id the id of the projectMemberRoleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectMemberRoleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/project-member-roles/{id}")
    public ResponseEntity<ProjectMemberRoleDTO> getProjectMemberRole(@PathVariable Long id) {
        log.debug("REST request to get ProjectMemberRole : {}", id);
        Optional<ProjectMemberRoleDTO> projectMemberRoleDTO = projectMemberRoleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectMemberRoleDTO);
    }

    /**
     * {@code DELETE  /project-member-roles/:id} : delete the "id" projectMemberRole.
     *
     * @param id the id of the projectMemberRoleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/project-member-roles/{id}")
    public ResponseEntity<Void> deleteProjectMemberRole(@PathVariable Long id) {
        log.debug("REST request to delete ProjectMemberRole : {}", id);
        projectMemberRoleService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/project-member-roles?query=:query} : search for the projectMemberRole corresponding
     * to the query.
     *
     * @param query the query of the projectMemberRole search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/project-member-roles")
    public ResponseEntity<List<ProjectMemberRoleDTO>> searchProjectMemberRoles(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of ProjectMemberRoles for query {}", query);
        Page<ProjectMemberRoleDTO> page = projectMemberRoleService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
