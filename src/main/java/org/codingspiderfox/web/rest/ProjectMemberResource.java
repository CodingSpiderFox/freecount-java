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
import org.codingspiderfox.repository.ProjectMemberRepository;
import org.codingspiderfox.service.ProjectMemberCommandHandler;
import org.codingspiderfox.service.ProjectMemberQueryService;
import org.codingspiderfox.service.ProjectMemberService;
import org.codingspiderfox.service.criteria.ProjectMemberCriteria;
import org.codingspiderfox.service.dto.CreateProjectMemberDTO;
import org.codingspiderfox.service.dto.ProjectMemberDTO;
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
 * REST controller for managing {@link org.codingspiderfox.domain.ProjectMember}.
 */
@RestController
@RequestMapping("/api")
public class ProjectMemberResource {

    private final Logger log = LoggerFactory.getLogger(ProjectMemberResource.class);

    private static final String ENTITY_NAME = "projectMember";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectMemberService projectMemberService;

    private final ProjectMemberRepository projectMemberRepository;

    private final ProjectMemberQueryService projectMemberQueryService;

    private final ProjectMemberCommandHandler projectMemberCommandHandler;

    public ProjectMemberResource(
        ProjectMemberService projectMemberService,
        ProjectMemberRepository projectMemberRepository,
        ProjectMemberQueryService projectMemberQueryService,
        ProjectMemberCommandHandler projectMemberCommandHandler
    ) {
        this.projectMemberService = projectMemberService;
        this.projectMemberRepository = projectMemberRepository;
        this.projectMemberQueryService = projectMemberQueryService;
        this.projectMemberCommandHandler = projectMemberCommandHandler;
    }

    /**
     * {@code POST  /project-members} : Create a new projectMember.
     *
     * @param projectMemberDTO the projectMemberDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectMemberDTO, or with status {@code 400 (Bad Request)} if the projectMember has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/project-members")
    public ResponseEntity<ProjectMemberDTO> createProjectMember(@Valid @RequestBody CreateProjectMemberDTO projectMemberDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProjectMember : {}", projectMemberDTO);
        if (projectMemberDTO.getId() != null) {
            throw new BadRequestAlertException("A new projectMember cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(projectMemberDTO.getProjectId())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        ProjectMemberDTO result = projectMemberCommandHandler.createProjectMember(projectMemberDTO);
        return ResponseEntity
            .created(new URI("/api/project-members/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /project-members/:id} : Updates an existing projectMember.
     *
     * @param id the id of the projectMemberDTO to save.
     * @param projectMemberDTO the projectMemberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectMemberDTO,
     * or with status {@code 400 (Bad Request)} if the projectMemberDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectMemberDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/project-members/{id}")
    public ResponseEntity<ProjectMemberDTO> updateProjectMember(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectMemberDTO projectMemberDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProjectMember : {}, {}", id, projectMemberDTO);
        if (projectMemberDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectMemberDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectMemberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProjectMemberDTO result = projectMemberService.save(projectMemberDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectMemberDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /project-members/:id} : Partial updates given fields of an existing projectMember, field will ignore if it is null
     *
     * @param id the id of the projectMemberDTO to save.
     * @param projectMemberDTO the projectMemberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectMemberDTO,
     * or with status {@code 400 (Bad Request)} if the projectMemberDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projectMemberDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectMemberDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/project-members/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProjectMemberDTO> partialUpdateProjectMember(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectMemberDTO projectMemberDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProjectMember partially : {}, {}", id, projectMemberDTO);
        if (projectMemberDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectMemberDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectMemberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProjectMemberDTO> result = projectMemberService.partialUpdate(projectMemberDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectMemberDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /project-members} : get all the projectMembers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectMembers in body.
     */
    @GetMapping("/project-members")
    public ResponseEntity<List<ProjectMemberDTO>> getAllProjectMembers(ProjectMemberCriteria criteria, Pageable pageable) {
        log.debug("REST request to get ProjectMembers by criteria: {}", criteria);
        Page<ProjectMemberDTO> page = projectMemberQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /project-members/count} : count all the projectMembers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/project-members/count")
    public ResponseEntity<Long> countProjectMembers(ProjectMemberCriteria criteria) {
        log.debug("REST request to count ProjectMembers by criteria: {}", criteria);
        return ResponseEntity.ok().body(projectMemberQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /project-members/:id} : get the "id" projectMember.
     *
     * @param id the id of the projectMemberDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectMemberDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/project-members/{id}")
    public ResponseEntity<ProjectMemberDTO> getProjectMember(@PathVariable Long id) {
        log.debug("REST request to get ProjectMember : {}", id);
        Optional<ProjectMemberDTO> projectMemberDTO = projectMemberService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectMemberDTO);
    }

    /**
     * {@code DELETE  /project-members/:id} : delete the "id" projectMember.
     *
     * @param id the id of the projectMemberDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/project-members/{id}")
    public ResponseEntity<Void> deleteProjectMember(@PathVariable Long id) {
        log.debug("REST request to delete ProjectMember : {}", id);
        projectMemberService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/project-members?query=:query} : search for the projectMember corresponding
     * to the query.
     *
     * @param query the query of the projectMember search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/project-members")
    public ResponseEntity<List<ProjectMemberDTO>> searchProjectMembers(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of ProjectMembers for query {}", query);
        Page<ProjectMemberDTO> page = projectMemberService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
