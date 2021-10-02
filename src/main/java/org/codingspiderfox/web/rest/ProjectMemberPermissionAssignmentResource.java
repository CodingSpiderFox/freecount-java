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
import org.codingspiderfox.repository.ProjectMemberPermissionAssignmentRepository;
import org.codingspiderfox.service.ProjectMemberPermissionAssignmentQueryService;
import org.codingspiderfox.service.ProjectMemberPermissionAssignmentService;
import org.codingspiderfox.service.criteria.ProjectMemberPermissionAssignmentCriteria;
import org.codingspiderfox.service.dto.ProjectMemberPermissionAssignmentDTO;
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
 * REST controller for managing {@link org.codingspiderfox.domain.ProjectMemberPermissionAssignment}.
 */
@RestController
@RequestMapping("/api")
public class ProjectMemberPermissionAssignmentResource {

    private final Logger log = LoggerFactory.getLogger(ProjectMemberPermissionAssignmentResource.class);

    private static final String ENTITY_NAME = "projectMemberPermissionAssignment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectMemberPermissionAssignmentService projectMemberPermissionAssignmentService;

    private final ProjectMemberPermissionAssignmentRepository projectMemberPermissionAssignmentRepository;

    private final ProjectMemberPermissionAssignmentQueryService projectMemberPermissionAssignmentQueryService;

    public ProjectMemberPermissionAssignmentResource(
        ProjectMemberPermissionAssignmentService projectMemberPermissionAssignmentService,
        ProjectMemberPermissionAssignmentRepository projectMemberPermissionAssignmentRepository,
        ProjectMemberPermissionAssignmentQueryService projectMemberPermissionAssignmentQueryService
    ) {
        this.projectMemberPermissionAssignmentService = projectMemberPermissionAssignmentService;
        this.projectMemberPermissionAssignmentRepository = projectMemberPermissionAssignmentRepository;
        this.projectMemberPermissionAssignmentQueryService = projectMemberPermissionAssignmentQueryService;
    }

    /**
     * {@code POST  /project-member-permission-assignments} : Create a new projectMemberPermissionAssignment.
     *
     * @param projectMemberPermissionAssignmentDTO the projectMemberPermissionAssignmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectMemberPermissionAssignmentDTO, or with status {@code 400 (Bad Request)} if the projectMemberPermissionAssignment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/project-member-permission-assignments")
    public ResponseEntity<ProjectMemberPermissionAssignmentDTO> createProjectMemberPermissionAssignment(
        @Valid @RequestBody ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO
    ) throws URISyntaxException {
        log.debug("REST request to save ProjectMemberPermissionAssignment : {}", projectMemberPermissionAssignmentDTO);
        if (projectMemberPermissionAssignmentDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new projectMemberPermissionAssignment cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        if (Objects.isNull(projectMemberPermissionAssignmentDTO.getProjectMember())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        ProjectMemberPermissionAssignmentDTO result = projectMemberPermissionAssignmentService.save(projectMemberPermissionAssignmentDTO);
        return ResponseEntity
            .created(new URI("/api/project-member-permission-assignments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /project-member-permission-assignments/:id} : Updates an existing projectMemberPermissionAssignment.
     *
     * @param id the id of the projectMemberPermissionAssignmentDTO to save.
     * @param projectMemberPermissionAssignmentDTO the projectMemberPermissionAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectMemberPermissionAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the projectMemberPermissionAssignmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectMemberPermissionAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/project-member-permission-assignments/{id}")
    public ResponseEntity<ProjectMemberPermissionAssignmentDTO> updateProjectMemberPermissionAssignment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProjectMemberPermissionAssignment : {}, {}", id, projectMemberPermissionAssignmentDTO);
        if (projectMemberPermissionAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectMemberPermissionAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectMemberPermissionAssignmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProjectMemberPermissionAssignmentDTO result = projectMemberPermissionAssignmentService.save(projectMemberPermissionAssignmentDTO);
        return ResponseEntity
            .ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    true,
                    ENTITY_NAME,
                    projectMemberPermissionAssignmentDTO.getId().toString()
                )
            )
            .body(result);
    }

    /**
     * {@code PATCH  /project-member-permission-assignments/:id} : Partial updates given fields of an existing projectMemberPermissionAssignment, field will ignore if it is null
     *
     * @param id the id of the projectMemberPermissionAssignmentDTO to save.
     * @param projectMemberPermissionAssignmentDTO the projectMemberPermissionAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectMemberPermissionAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the projectMemberPermissionAssignmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projectMemberPermissionAssignmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectMemberPermissionAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/project-member-permission-assignments/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProjectMemberPermissionAssignmentDTO> partialUpdateProjectMemberPermissionAssignment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to partial update ProjectMemberPermissionAssignment partially : {}, {}",
            id,
            projectMemberPermissionAssignmentDTO
        );
        if (projectMemberPermissionAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectMemberPermissionAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectMemberPermissionAssignmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProjectMemberPermissionAssignmentDTO> result = projectMemberPermissionAssignmentService.partialUpdate(
            projectMemberPermissionAssignmentDTO
        );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectMemberPermissionAssignmentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /project-member-permission-assignments} : get all the projectMemberPermissionAssignments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectMemberPermissionAssignments in body.
     */
    @GetMapping("/project-member-permission-assignments")
    public ResponseEntity<List<ProjectMemberPermissionAssignmentDTO>> getAllProjectMemberPermissionAssignments(
        ProjectMemberPermissionAssignmentCriteria criteria,
        Pageable pageable
    ) {
        log.debug("REST request to get ProjectMemberPermissionAssignments by criteria: {}", criteria);
        Page<ProjectMemberPermissionAssignmentDTO> page = projectMemberPermissionAssignmentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /project-member-permission-assignments/count} : count all the projectMemberPermissionAssignments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/project-member-permission-assignments/count")
    public ResponseEntity<Long> countProjectMemberPermissionAssignments(ProjectMemberPermissionAssignmentCriteria criteria) {
        log.debug("REST request to count ProjectMemberPermissionAssignments by criteria: {}", criteria);
        return ResponseEntity.ok().body(projectMemberPermissionAssignmentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /project-member-permission-assignments/:id} : get the "id" projectMemberPermissionAssignment.
     *
     * @param id the id of the projectMemberPermissionAssignmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectMemberPermissionAssignmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/project-member-permission-assignments/{id}")
    public ResponseEntity<ProjectMemberPermissionAssignmentDTO> getProjectMemberPermissionAssignment(@PathVariable Long id) {
        log.debug("REST request to get ProjectMemberPermissionAssignment : {}", id);
        Optional<ProjectMemberPermissionAssignmentDTO> projectMemberPermissionAssignmentDTO = projectMemberPermissionAssignmentService.findOne(
            id
        );
        return ResponseUtil.wrapOrNotFound(projectMemberPermissionAssignmentDTO);
    }

    /**
     * {@code DELETE  /project-member-permission-assignments/:id} : delete the "id" projectMemberPermissionAssignment.
     *
     * @param id the id of the projectMemberPermissionAssignmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/project-member-permission-assignments/{id}")
    public ResponseEntity<Void> deleteProjectMemberPermissionAssignment(@PathVariable Long id) {
        log.debug("REST request to delete ProjectMemberPermissionAssignment : {}", id);
        projectMemberPermissionAssignmentService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/project-member-permission-assignments?query=:query} : search for the projectMemberPermissionAssignment corresponding
     * to the query.
     *
     * @param query the query of the projectMemberPermissionAssignment search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/project-member-permission-assignments")
    public ResponseEntity<List<ProjectMemberPermissionAssignmentDTO>> searchProjectMemberPermissionAssignments(
        @RequestParam String query,
        Pageable pageable
    ) {
        log.debug("REST request to search for a page of ProjectMemberPermissionAssignments for query {}", query);
        Page<ProjectMemberPermissionAssignmentDTO> page = projectMemberPermissionAssignmentService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
