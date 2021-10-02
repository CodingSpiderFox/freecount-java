package org.codingspiderfox.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.persistence.criteria.JoinType;

import org.codingspiderfox.domain.*; // for static metamodels
import org.codingspiderfox.domain.ProjectMemberPermissionAssignment;
import org.codingspiderfox.domain.enumeration.ProjectMemberPermissionEnum;
import org.codingspiderfox.repository.ProjectMemberPermissionAssignmentRepository;
import org.codingspiderfox.repository.search.ProjectMemberPermissionAssignmentSearchRepository;
import org.codingspiderfox.service.criteria.ProjectMemberPermissionAssignmentCriteria;
import org.codingspiderfox.service.criteria.ProjectMemberPermissionCriteria;
import org.codingspiderfox.service.dto.ProjectMemberPermissionAssignmentDTO;
import org.codingspiderfox.service.mapper.ProjectMemberPermissionAssignmentMapper;
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
 * Service for executing complex queries for {@link ProjectMemberPermissionAssignment} entities in the database.
 * The main input is a {@link ProjectMemberPermissionAssignmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProjectMemberPermissionAssignmentDTO} or a {@link Page} of {@link ProjectMemberPermissionAssignmentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProjectMemberPermissionAssignmentQueryService extends QueryService<ProjectMemberPermissionAssignment> {

    private final Logger log = LoggerFactory.getLogger(ProjectMemberPermissionAssignmentQueryService.class);

    private final ProjectMemberPermissionAssignmentRepository projectMemberPermissionAssignmentRepository;

    private final ProjectMemberPermissionAssignmentMapper projectMemberPermissionAssignmentMapper;

    private final ProjectMemberPermissionAssignmentSearchRepository projectMemberPermissionAssignmentSearchRepository;

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public ProjectMemberPermissionAssignmentQueryService(
        ProjectMemberPermissionAssignmentRepository projectMemberPermissionAssignmentRepository,
        ProjectMemberPermissionAssignmentMapper projectMemberPermissionAssignmentMapper,
        ProjectMemberPermissionAssignmentSearchRepository projectMemberPermissionAssignmentSearchRepository
    ) {
        this.projectMemberPermissionAssignmentRepository = projectMemberPermissionAssignmentRepository;
        this.projectMemberPermissionAssignmentMapper = projectMemberPermissionAssignmentMapper;
        this.projectMemberPermissionAssignmentSearchRepository = projectMemberPermissionAssignmentSearchRepository;
    }

    /**
     * Return a {@link List} of {@link ProjectMemberPermissionAssignmentDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProjectMemberPermissionAssignmentDTO> findByCriteria(ProjectMemberPermissionAssignmentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProjectMemberPermissionAssignment> specification = createSpecification(criteria);
        return projectMemberPermissionAssignmentMapper.toDto(projectMemberPermissionAssignmentRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProjectMemberPermissionAssignmentDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProjectMemberPermissionAssignmentDTO> findByCriteria(ProjectMemberPermissionAssignmentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProjectMemberPermissionAssignment> specification = createSpecification(criteria);
        return projectMemberPermissionAssignmentRepository.findAll(specification, page).map(projectMemberPermissionAssignmentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProjectMemberPermissionAssignmentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProjectMemberPermissionAssignment> specification = createSpecification(criteria);
        return projectMemberPermissionAssignmentRepository.count(specification);
    }

    @Transactional(readOnly = true)
    public Boolean hasAddMemberPermissionAssignmentForProjectIdAndUserLogin(String currentUserLogin, Long projectId) {
        LongFilter projectIdFilter = new LongFilter();
        projectIdFilter.setEquals(projectId);
        Specification<ProjectMemberPermissionAssignment> specification = Specification.where(null);
        specification = specification.and(buildSpecification(projectIdFilter, root -> root.join(
            ProjectMemberPermissionAssignment_.projectMember, JoinType.LEFT).join(ProjectMember_.project).get(Project_.id)
        ));
        StringFilter userLoginFilter = new StringFilter();
        userLoginFilter.setEquals(currentUserLogin);
        specification = specification.and(buildSpecification(userLoginFilter, root ->
            root.join(ProjectMemberPermissionAssignment_.projectMember, JoinType.LEFT).join(ProjectMember_.user).get(User_.login)));

        ProjectMemberPermissionCriteria.ProjectMemberPermissionEnumFilter permissionFilter =
            new ProjectMemberPermissionCriteria.ProjectMemberPermissionEnumFilter();
        permissionFilter.setIn(Arrays.asList(ProjectMemberPermissionEnum.ADD_MEMBER));

        specification = specification.and(buildSpecification(permissionFilter, root -> root.join(ProjectMemberPermissionAssignment_.projectMemberPermissions).get(ProjectMemberPermission_.projectMemberPermission)));

        return !projectMemberPermissionAssignmentRepository.findAll(specification).isEmpty();
    }

    /**
     * Function to convert {@link ProjectMemberPermissionAssignmentCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProjectMemberPermissionAssignment> createSpecification(ProjectMemberPermissionAssignmentCriteria criteria) {
        Specification<ProjectMemberPermissionAssignment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ProjectMemberPermissionAssignment_.id));
            }
            if (criteria.getAssignmentTimestamp() != null) {
                specification =
                    specification.and(
                        buildRangeSpecification(criteria.getAssignmentTimestamp(), ProjectMemberPermissionAssignment_.assignmentTimestamp)
                    );
            }
            if (criteria.getProjectMemberId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProjectMemberId(),
                            root -> root.join(ProjectMemberPermissionAssignment_.projectMember, JoinType.LEFT).get(ProjectMember_.id)
                        )
                    );
            }
            if (criteria.getProjectMemberPermissionId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProjectMemberPermissionId(),
                            root ->
                                root
                                    .join(ProjectMemberPermissionAssignment_.projectMemberPermissions, JoinType.LEFT)
                                    .get(ProjectMemberPermission_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
