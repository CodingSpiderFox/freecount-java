package org.codingspiderfox.service;

import com.carrotsearch.hppc.ByteContainer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.codingspiderfox.domain.*; // for static metamodels
import org.codingspiderfox.domain.ProjectMember;
import org.codingspiderfox.repository.ProjectMemberRepository;
import org.codingspiderfox.repository.search.ProjectMemberSearchRepository;
import org.codingspiderfox.service.criteria.ProjectMemberCriteria;
import org.codingspiderfox.service.dto.ProjectMemberDTO;
import org.codingspiderfox.service.mapper.ProjectMemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ProjectMember} entities in the database.
 * The main input is a {@link ProjectMemberCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProjectMemberDTO} or a {@link Page} of {@link ProjectMemberDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProjectMemberQueryService extends QueryService<ProjectMember> {

    private final Logger log = LoggerFactory.getLogger(ProjectMemberQueryService.class);

    private final ProjectMemberRepository projectMemberRepository;

    private final ProjectMemberMapper projectMemberMapper;

    private final ProjectMemberSearchRepository projectMemberSearchRepository;

    public ProjectMemberQueryService(
        ProjectMemberRepository projectMemberRepository,
        ProjectMemberMapper projectMemberMapper,
        ProjectMemberSearchRepository projectMemberSearchRepository
    ) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectMemberMapper = projectMemberMapper;
        this.projectMemberSearchRepository = projectMemberSearchRepository;
    }

    /**
     * Return a {@link List} of {@link ProjectMemberDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProjectMemberDTO> findByCriteria(ProjectMemberCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProjectMember> specification = createSpecification(criteria);
        return projectMemberMapper.toDto(projectMemberRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProjectMemberDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProjectMemberDTO> findByCriteria(ProjectMemberCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProjectMember> specification = createSpecification(criteria);
        return projectMemberRepository.findAll(specification, page).map(projectMemberMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProjectMemberCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProjectMember> specification = createSpecification(criteria);
        return projectMemberRepository.count(specification);
    }

    /**
     * Function to convert {@link ProjectMemberCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProjectMember> createSpecification(ProjectMemberCriteria criteria) {
        Specification<ProjectMember> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ProjectMember_.id));
            }
            if (criteria.getAddedTimestamp() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAddedTimestamp(), ProjectMember_.addedTimestamp));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(ProjectMember_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getProjectId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProjectId(),
                            root -> root.join(ProjectMember_.project, JoinType.LEFT).get(Project_.id)
                        )
                    );
            }
        }
        return specification;
    }

    public List<ProjectMember> findByAdminUserLoginAndProject(String currentUserLogin, Long projectId) {
        return Collections.emptyList();
    }
}
