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
import org.codingspiderfox.repository.ProjectMemberRoleAssignmentRepository;
import org.codingspiderfox.service.ProjectMemberRoleAssignmentQueryService;
import org.codingspiderfox.service.ProjectMemberRoleAssignmentService;
import org.codingspiderfox.service.criteria.ProjectMemberRoleAssignmentCriteria;
import org.codingspiderfox.service.dto.ProjectMemberRoleAssignmentDTO;
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
 * REST controller for managing {@link org.codingspiderfox.domain.ProjectMemberRoleAssignment}.
 */
@RestController
@RequestMapping("/api")
public class ProjectMemberRoleAssignmentResource {

    private final Logger log = LoggerFactory.getLogger(ProjectMemberRoleAssignmentResource.class);

    private static final String ENTITY_NAME = "projectMemberRoleAssignment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectMemberRoleAssignmentService projectMemberRoleAssignmentService;

    private final ProjectMemberRoleAssignmentRepository projectMemberRoleAssignmentRepository;

    private final ProjectMemberRoleAssignmentQueryService projectMemberRoleAssignmentQueryService;

    public ProjectMemberRoleAssignmentResource(
        ProjectMemberRoleAssignmentService projectMemberRoleAssignmentService,
        ProjectMemberRoleAssignmentRepository projectMemberRoleAssignmentRepository,
        ProjectMemberRoleAssignmentQueryService projectMemberRoleAssignmentQueryService
    ) {
        this.projectMemberRoleAssignmentService = projectMemberRoleAssignmentService;
        this.projectMemberRoleAssignmentRepository = projectMemberRoleAssignmentRepository;
        this.projectMemberRoleAssignmentQueryService = projectMemberRoleAssignmentQueryService;
    }

    /**
     * {@code POST  /project-member-role-assignments} : Create a new projectMemberRoleAssignment.
     *
     * @param projectMemberRoleAssignmentDTO the projectMemberRoleAssignmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectMemberRoleAssignmentDTO, or with status {@code 400 (Bad Request)} if the projectMemberRoleAssignment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/project-member-role-assignments")
    public ResponseEntity<ProjectMemberRoleAssignmentDTO> createProjectMemberRoleAssignment(
        @Valid @RequestBody ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO
    ) throws URISyntaxException {
        log.debug("REST request to save ProjectMemberRoleAssignment : {}", projectMemberRoleAssignmentDTO);
        if (projectMemberRoleAssignmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new projectMemberRoleAssignment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(projectMemberRoleAssignmentDTO.getProjectMember())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        ProjectMemberRoleAssignmentDTO result = projectMemberRoleAssignmentService.save(projectMemberRoleAssignmentDTO);
        return ResponseEntity
            .created(new URI("/api/project-member-role-assignments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /project-member-role-assignments/:id} : Updates an existing projectMemberRoleAssignment.
     *
     * @param id the id of the projectMemberRoleAssignmentDTO to save.
     * @param projectMemberRoleAssignmentDTO the projectMemberRoleAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectMemberRoleAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the projectMemberRoleAssignmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectMemberRoleAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/project-member-role-assignments/{id}")
    public ResponseEntity<ProjectMemberRoleAssignmentDTO> updateProjectMemberRoleAssignment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProjectMemberRoleAssignment : {}, {}", id, projectMemberRoleAssignmentDTO);
        if (projectMemberRoleAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectMemberRoleAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectMemberRoleAssignmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProjectMemberRoleAssignmentDTO result = projectMemberRoleAssignmentService.save(projectMemberRoleAssignmentDTO);
        return ResponseEntity
            .ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectMemberRoleAssignmentDTO.getId().toString())
            )
            .body(result);
    }

    /**
     * {@code PATCH  /project-member-role-assignments/:id} : Partial updates given fields of an existing projectMemberRoleAssignment, field will ignore if it is null
     *
     * @param id the id of the projectMemberRoleAssignmentDTO to save.
     * @param projectMemberRoleAssignmentDTO the projectMemberRoleAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectMemberRoleAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the projectMemberRoleAssignmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projectMemberRoleAssignmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectMemberRoleAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/project-member-role-assignments/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProjectMemberRoleAssignmentDTO> partialUpdateProjectMemberRoleAssignment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProjectMemberRoleAssignment partially : {}, {}", id, projectMemberRoleAssignmentDTO);
        if (projectMemberRoleAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectMemberRoleAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectMemberRoleAssignmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProjectMemberRoleAssignmentDTO> result = projectMemberRoleAssignmentService.partialUpdate(projectMemberRoleAssignmentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectMemberRoleAssignmentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /project-member-role-assignments} : get all the projectMemberRoleAssignments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectMemberRoleAssignments in body.
     */
    @GetMapping("/project-member-role-assignments")
    public ResponseEntity<List<ProjectMemberRoleAssignmentDTO>> getAllProjectMemberRoleAssignments(
        ProjectMemberRoleAssignmentCriteria criteria,
        Pageable pageable
    ) {
        log.debug("REST request to get ProjectMemberRoleAssignments by criteria: {}", criteria);
        Page<ProjectMemberRoleAssignmentDTO> page = projectMemberRoleAssignmentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /project-member-role-assignments/count} : count all the projectMemberRoleAssignments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/project-member-role-assignments/count")
    public ResponseEntity<Long> countProjectMemberRoleAssignments(ProjectMemberRoleAssignmentCriteria criteria) {
        log.debug("REST request to count ProjectMemberRoleAssignments by criteria: {}", criteria);
        return ResponseEntity.ok().body(projectMemberRoleAssignmentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /project-member-role-assignments/:id} : get the "id" projectMemberRoleAssignment.
     *
     * @param id the id of the projectMemberRoleAssignmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectMemberRoleAssignmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/project-member-role-assignments/{id}")
    public ResponseEntity<ProjectMemberRoleAssignmentDTO> getProjectMemberRoleAssignment(@PathVariable Long id) {
        log.debug("REST request to get ProjectMemberRoleAssignment : {}", id);
        Optional<ProjectMemberRoleAssignmentDTO> projectMemberRoleAssignmentDTO = projectMemberRoleAssignmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectMemberRoleAssignmentDTO);
    }

    /**
     * {@code DELETE  /project-member-role-assignments/:id} : delete the "id" projectMemberRoleAssignment.
     *
     * @param id the id of the projectMemberRoleAssignmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/project-member-role-assignments/{id}")
    public ResponseEntity<Void> deleteProjectMemberRoleAssignment(@PathVariable Long id) {
        log.debug("REST request to delete ProjectMemberRoleAssignment : {}", id);
        projectMemberRoleAssignmentService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/project-member-role-assignments?query=:query} : search for the projectMemberRoleAssignment corresponding
     * to the query.
     *
     * @param query the query of the projectMemberRoleAssignment search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/project-member-role-assignments")
    public ResponseEntity<List<ProjectMemberRoleAssignmentDTO>> searchProjectMemberRoleAssignments(
        @RequestParam String query,
        Pageable pageable
    ) {
        log.debug("REST request to search for a page of ProjectMemberRoleAssignments for query {}", query);
        Page<ProjectMemberRoleAssignmentDTO> page = projectMemberRoleAssignmentService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
