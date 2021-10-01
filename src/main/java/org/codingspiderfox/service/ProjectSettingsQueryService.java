package org.codingspiderfox.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.codingspiderfox.domain.*; // for static metamodels
import org.codingspiderfox.domain.ProjectSettings;
import org.codingspiderfox.repository.ProjectSettingsRepository;
import org.codingspiderfox.repository.search.ProjectSettingsSearchRepository;
import org.codingspiderfox.service.criteria.ProjectSettingsCriteria;
import org.codingspiderfox.service.dto.ProjectSettingsDTO;
import org.codingspiderfox.service.mapper.ProjectSettingsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ProjectSettings} entities in the database.
 * The main input is a {@link ProjectSettingsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProjectSettingsDTO} or a {@link Page} of {@link ProjectSettingsDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProjectSettingsQueryService extends QueryService<ProjectSettings> {

    private final Logger log = LoggerFactory.getLogger(ProjectSettingsQueryService.class);

    private final ProjectSettingsRepository projectSettingsRepository;

    private final ProjectSettingsMapper projectSettingsMapper;

    private final ProjectSettingsSearchRepository projectSettingsSearchRepository;

    public ProjectSettingsQueryService(
        ProjectSettingsRepository projectSettingsRepository,
        ProjectSettingsMapper projectSettingsMapper,
        ProjectSettingsSearchRepository projectSettingsSearchRepository
    ) {
        this.projectSettingsRepository = projectSettingsRepository;
        this.projectSettingsMapper = projectSettingsMapper;
        this.projectSettingsSearchRepository = projectSettingsSearchRepository;
    }

    /**
     * Return a {@link List} of {@link ProjectSettingsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProjectSettingsDTO> findByCriteria(ProjectSettingsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProjectSettings> specification = createSpecification(criteria);
        return projectSettingsMapper.toDto(projectSettingsRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProjectSettingsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProjectSettingsDTO> findByCriteria(ProjectSettingsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProjectSettings> specification = createSpecification(criteria);
        return projectSettingsRepository.findAll(specification, page).map(projectSettingsMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProjectSettingsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProjectSettings> specification = createSpecification(criteria);
        return projectSettingsRepository.count(specification);
    }

    /**
     * Function to convert {@link ProjectSettingsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProjectSettings> createSpecification(ProjectSettingsCriteria criteria) {
        Specification<ProjectSettings> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ProjectSettings_.id));
            }
            if (criteria.getMustProvideBillCopyByDefault() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getMustProvideBillCopyByDefault(), ProjectSettings_.mustProvideBillCopyByDefault)
                    );
            }
            if (criteria.getProjectId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProjectId(),
                            root -> root.join(ProjectSettings_.project, JoinType.LEFT).get(Project_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
