package org.codingspiderfox.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.codingspiderfox.domain.Project;
import org.codingspiderfox.domain.User;
import org.codingspiderfox.domain.User_;
import org.codingspiderfox.repository.ProjectRepository;
import org.codingspiderfox.repository.UserRepository;
import org.codingspiderfox.repository.search.ProjectSearchRepository;
import org.codingspiderfox.service.criteria.ProjectCriteria;
import org.codingspiderfox.service.criteria.UserCriteria;
import org.codingspiderfox.service.dto.ProjectDTO;
import org.codingspiderfox.service.mapper.ProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.RangeFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Service for executing complex queries for {@link Project} entities in the database.
 * The main input is a {@link ProjectCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProjectDTO} or a {@link Page} of {@link ProjectDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserQueryService extends QueryService<User> {

    private final Logger log = LoggerFactory.getLogger(UserQueryService.class);

    private final UserRepository userRepository;

    public UserQueryService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> findByIdIn(List<String> userIds) {
        log.debug("find by id in : {}", userIds.stream().map(id -> "" + id).collect(Collectors.joining(",")));
        StringFilter userIdFilter = new StringFilter();
        userIdFilter.setIn(userIds);
        Specification<User> userIdInSpec = buildSpecification(userIdFilter, User_.id);
        return userRepository.findAll(userIdInSpec);
    }

    /**
     * Function to convert {@link UserCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<User> createSpecification(UserCriteria criteria) {
        Specification<User> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), User_.id));
            }
        }
        return specification;
    }
}
