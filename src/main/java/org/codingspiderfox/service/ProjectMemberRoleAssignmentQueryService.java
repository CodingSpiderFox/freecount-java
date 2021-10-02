package org.codingspiderfox.service;

import java.util.Arrays;
import java.util.List;
import javax.persistence.criteria.JoinType;

import org.codingspiderfox.domain.*; // for static metamodels
import org.codingspiderfox.domain.ProjectMemberRoleAssignment;
import org.codingspiderfox.domain.enumeration.ProjectMemberPermissionEnum;
import org.codingspiderfox.domain.enumeration.ProjectMemberRoleEnum;
import org.codingspiderfox.repository.ProjectMemberRoleAssignmentRepository;
import org.codingspiderfox.repository.search.ProjectMemberRoleAssignmentSearchRepository;
import org.codingspiderfox.service.criteria.ProjectMemberPermissionCriteria;
import org.codingspiderfox.service.criteria.ProjectMemberRoleAssignmentCriteria;
import org.codingspiderfox.service.criteria.ProjectMemberRoleCriteria;
import org.codingspiderfox.service.dto.ProjectMemberRoleAssignmentDTO;
import org.codingspiderfox.service.mapper.ProjectMemberRoleAssignmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Service for executing complex queries for {@link ProjectMemberRoleAssignment} entities in the database.
 * The main input is a {@link ProjectMemberRoleAssignmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProjectMemberRoleAssignmentDTO} or a {@link Page} of {@link ProjectMemberRoleAssignmentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProjectMemberRoleAssignmentQueryService extends QueryService<ProjectMemberRoleAssignment> {

    private final Logger log = LoggerFactory.getLogger(ProjectMemberRoleAssignmentQueryService.class);

    private final ProjectMemberRoleAssignmentRepository projectMemberRoleAssignmentRepository;

    private final ProjectMemberRoleAssignmentMapper projectMemberRoleAssignmentMapper;

    private final ProjectMemberRoleAssignmentSearchRepository projectMemberRoleAssignmentSearchRepository;

    public ProjectMemberRoleAssignmentQueryService(
        ProjectMemberRoleAssignmentRepository projectMemberRoleAssignmentRepository,
        ProjectMemberRoleAssignmentMapper projectMemberRoleAssignmentMapper,
        ProjectMemberRoleAssignmentSearchRepository projectMemberRoleAssignmentSearchRepository
    ) {
        this.projectMemberRoleAssignmentRepository = projectMemberRoleAssignmentRepository;
        this.projectMemberRoleAssignmentMapper = projectMemberRoleAssignmentMapper;
        this.projectMemberRoleAssignmentSearchRepository = projectMemberRoleAssignmentSearchRepository;
    }

    /**
     * Return a {@link List} of {@link ProjectMemberRoleAssignmentDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProjectMemberRoleAssignmentDTO> findByCriteria(ProjectMemberRoleAssignmentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProjectMemberRoleAssignment> specification = createSpecification(criteria);
        return projectMemberRoleAssignmentMapper.toDto(projectMemberRoleAssignmentRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProjectMemberRoleAssignmentDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProjectMemberRoleAssignmentDTO> findByCriteria(ProjectMemberRoleAssignmentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProjectMemberRoleAssignment> specification = createSpecification(criteria);
        return projectMemberRoleAssignmentRepository.findAll(specification, page).map(projectMemberRoleAssignmentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProjectMemberRoleAssignmentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProjectMemberRoleAssignment> specification = createSpecification(criteria);
        return projectMemberRoleAssignmentRepository.count(specification);
    }

    @Transactional(readOnly = true)
    public Boolean hasRoleAssignmentForProjectAndUserThatAllowsAddingMembersToProject(String currentUserLogin, Long projectId) {
        LongFilter projectIdFilter = new LongFilter();
        projectIdFilter.setEquals(projectId);
        Specification<ProjectMemberRoleAssignment> specification = Specification.where(null);
        specification = specification.and(buildSpecification(projectIdFilter, root -> root.join(
            ProjectMemberRoleAssignment_.projectMember, JoinType.LEFT).join(ProjectMember_.project).get(Project_.id)
        ));
        StringFilter userLoginFilter = new StringFilter();
        userLoginFilter.setEquals(currentUserLogin);
        specification = specification.and(buildSpecification(userLoginFilter, root ->
            root.join(ProjectMemberRoleAssignment_.projectMember, JoinType.LEFT).join(ProjectMember_.user).get(User_.login)));

        ProjectMemberRoleCriteria.ProjectMemberRoleEnumFilter rolesFilter =
            new ProjectMemberRoleCriteria.ProjectMemberRoleEnumFilter();
        rolesFilter.setIn(Arrays.asList(ProjectMemberRoleEnum.PROJECT_ADMIN));

        specification = specification.and(buildSpecification(rolesFilter, root -> root.join(ProjectMemberRoleAssignment_.projectMemberRoles).get(ProjectMemberRole_.projectMemberRole)));

        return !projectMemberRoleAssignmentRepository.findAll(specification).isEmpty();
    }

    /**
     * Function to convert {@link ProjectMemberRoleAssignmentCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProjectMemberRoleAssignment> createSpecification(ProjectMemberRoleAssignmentCriteria criteria) {
        Specification<ProjectMemberRoleAssignment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ProjectMemberRoleAssignment_.id));
            }
            if (criteria.getAssignmentTimestamp() != null) {
                specification =
                    specification.and(
                        buildRangeSpecification(criteria.getAssignmentTimestamp(), ProjectMemberRoleAssignment_.assignmentTimestamp)
                    );
            }
            if (criteria.getProjectMemberId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProjectMemberId(),
                            root -> root.join(ProjectMemberRoleAssignment_.projectMember, JoinType.LEFT).get(ProjectMember_.id)
                        )
                    );
            }
            if (criteria.getProjectMemberRoleId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProjectMemberRoleId(),
                            root -> root.join(ProjectMemberRoleAssignment_.projectMemberRoles, JoinType.LEFT).get(ProjectMemberRole_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
