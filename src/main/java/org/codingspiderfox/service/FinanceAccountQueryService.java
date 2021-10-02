package org.codingspiderfox.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.codingspiderfox.domain.*; // for static metamodels
import org.codingspiderfox.domain.FinanceAccount;
import org.codingspiderfox.repository.FinanceAccountRepository;
import org.codingspiderfox.repository.search.FinanceAccountSearchRepository;
import org.codingspiderfox.service.criteria.FinanceAccountCriteria;
import org.codingspiderfox.service.dto.FinanceAccountDTO;
import org.codingspiderfox.service.mapper.FinanceAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link FinanceAccount} entities in the database.
 * The main input is a {@link FinanceAccountCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FinanceAccountDTO} or a {@link Page} of {@link FinanceAccountDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FinanceAccountQueryService extends QueryService<FinanceAccount> {

    private final Logger log = LoggerFactory.getLogger(FinanceAccountQueryService.class);

    private final FinanceAccountRepository financeAccountRepository;

    private final FinanceAccountMapper financeAccountMapper;

    private final FinanceAccountSearchRepository financeAccountSearchRepository;

    public FinanceAccountQueryService(
        FinanceAccountRepository financeAccountRepository,
        FinanceAccountMapper financeAccountMapper,
        FinanceAccountSearchRepository financeAccountSearchRepository
    ) {
        this.financeAccountRepository = financeAccountRepository;
        this.financeAccountMapper = financeAccountMapper;
        this.financeAccountSearchRepository = financeAccountSearchRepository;
    }

    /**
     * Return a {@link List} of {@link FinanceAccountDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FinanceAccountDTO> findByCriteria(FinanceAccountCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<FinanceAccount> specification = createSpecification(criteria);
        return financeAccountMapper.toDto(financeAccountRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link FinanceAccountDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FinanceAccountDTO> findByCriteria(FinanceAccountCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<FinanceAccount> specification = createSpecification(criteria);
        return financeAccountRepository.findAll(specification, page).map(financeAccountMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FinanceAccountCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<FinanceAccount> specification = createSpecification(criteria);
        return financeAccountRepository.count(specification);
    }

    /**
     * Function to convert {@link FinanceAccountCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<FinanceAccount> createSpecification(FinanceAccountCriteria criteria) {
        Specification<FinanceAccount> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getId(), FinanceAccount_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), FinanceAccount_.title));
            }
            if (criteria.getOwnerId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getOwnerId(), root -> root.join(FinanceAccount_.owner, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
