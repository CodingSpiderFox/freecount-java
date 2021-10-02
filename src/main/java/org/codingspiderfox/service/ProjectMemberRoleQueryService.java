package org.codingspiderfox.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.codingspiderfox.domain.*; // for static metamodels
import org.codingspiderfox.domain.ProjectMemberRole;
import org.codingspiderfox.repository.ProjectMemberRoleRepository;
import org.codingspiderfox.repository.search.ProjectMemberRoleSearchRepository;
import org.codingspiderfox.service.criteria.ProjectMemberRoleCriteria;
import org.codingspiderfox.service.dto.ProjectMemberRoleDTO;
import org.codingspiderfox.service.mapper.ProjectMemberRoleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ProjectMemberRole} entities in the database.
 * The main input is a {@link ProjectMemberRoleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProjectMemberRoleDTO} or a {@link Page} of {@link ProjectMemberRoleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProjectMemberRoleQueryService extends QueryService<ProjectMemberRole> {

    private final Logger log = LoggerFactory.getLogger(ProjectMemberRoleQueryService.class);

    private final ProjectMemberRoleRepository projectMemberRoleRepository;

    private final ProjectMemberRoleMapper projectMemberRoleMapper;

    private final ProjectMemberRoleSearchRepository projectMemberRoleSearchRepository;

    public ProjectMemberRoleQueryService(
        ProjectMemberRoleRepository projectMemberRoleRepository,
        ProjectMemberRoleMapper projectMemberRoleMapper,
        ProjectMemberRoleSearchRepository projectMemberRoleSearchRepository
    ) {
        this.projectMemberRoleRepository = projectMemberRoleRepository;
        this.projectMemberRoleMapper = projectMemberRoleMapper;
        this.projectMemberRoleSearchRepository = projectMemberRoleSearchRepository;
    }

    /**
     * Return a {@link List} of {@link ProjectMemberRoleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProjectMemberRoleDTO> findByCriteria(ProjectMemberRoleCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProjectMemberRole> specification = createSpecification(criteria);
        return projectMemberRoleMapper.toDto(projectMemberRoleRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProjectMemberRoleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProjectMemberRoleDTO> findByCriteria(ProjectMemberRoleCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProjectMemberRole> specification = createSpecification(criteria);
        return projectMemberRoleRepository.findAll(specification, page).map(projectMemberRoleMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProjectMemberRoleCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProjectMemberRole> specification = createSpecification(criteria);
        return projectMemberRoleRepository.count(specification);
    }

    /**
     * Function to convert {@link ProjectMemberRoleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProjectMemberRole> createSpecification(ProjectMemberRoleCriteria criteria) {
        Specification<ProjectMemberRole> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ProjectMemberRole_.id));
            }
            if (criteria.getCreatedTimestamp() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getCreatedTimestamp(), ProjectMemberRole_.createdTimestamp));
            }
            if (criteria.getProjectMemberRole() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getProjectMemberRole(), ProjectMemberRole_.projectMemberRole));
            }
        }
        return specification;
    }
}
