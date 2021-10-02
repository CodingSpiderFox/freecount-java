package org.codingspiderfox.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.codingspiderfox.domain.*; // for static metamodels
import org.codingspiderfox.domain.ProjectMemberPermission;
import org.codingspiderfox.repository.ProjectMemberPermissionRepository;
import org.codingspiderfox.repository.search.ProjectMemberPermissionSearchRepository;
import org.codingspiderfox.service.criteria.ProjectMemberPermissionCriteria;
import org.codingspiderfox.service.dto.ProjectMemberPermissionDTO;
import org.codingspiderfox.service.mapper.ProjectMemberPermissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ProjectMemberPermission} entities in the database.
 * The main input is a {@link ProjectMemberPermissionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProjectMemberPermissionDTO} or a {@link Page} of {@link ProjectMemberPermissionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProjectMemberPermissionQueryService extends QueryService<ProjectMemberPermission> {

    private final Logger log = LoggerFactory.getLogger(ProjectMemberPermissionQueryService.class);

    private final ProjectMemberPermissionRepository projectMemberPermissionRepository;

    private final ProjectMemberPermissionMapper projectMemberPermissionMapper;

    private final ProjectMemberPermissionSearchRepository projectMemberPermissionSearchRepository;

    public ProjectMemberPermissionQueryService(
        ProjectMemberPermissionRepository projectMemberPermissionRepository,
        ProjectMemberPermissionMapper projectMemberPermissionMapper,
        ProjectMemberPermissionSearchRepository projectMemberPermissionSearchRepository
    ) {
        this.projectMemberPermissionRepository = projectMemberPermissionRepository;
        this.projectMemberPermissionMapper = projectMemberPermissionMapper;
        this.projectMemberPermissionSearchRepository = projectMemberPermissionSearchRepository;
    }

    /**
     * Return a {@link List} of {@link ProjectMemberPermissionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProjectMemberPermissionDTO> findByCriteria(ProjectMemberPermissionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProjectMemberPermission> specification = createSpecification(criteria);
        return projectMemberPermissionMapper.toDto(projectMemberPermissionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProjectMemberPermissionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProjectMemberPermissionDTO> findByCriteria(ProjectMemberPermissionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProjectMemberPermission> specification = createSpecification(criteria);
        return projectMemberPermissionRepository.findAll(specification, page).map(projectMemberPermissionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProjectMemberPermissionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProjectMemberPermission> specification = createSpecification(criteria);
        return projectMemberPermissionRepository.count(specification);
    }

    /**
     * Function to convert {@link ProjectMemberPermissionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProjectMemberPermission> createSpecification(ProjectMemberPermissionCriteria criteria) {
        Specification<ProjectMemberPermission> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ProjectMemberPermission_.id));
            }
            if (criteria.getCreatedTimestamp() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getCreatedTimestamp(), ProjectMemberPermission_.createdTimestamp));
            }
            if (criteria.getProjectMemberPermission() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getProjectMemberPermission(), ProjectMemberPermission_.projectMemberPermission)
                    );
            }
        }
        return specification;
    }
}
