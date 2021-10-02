package org.codingspiderfox.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.codingspiderfox.domain.*; // for static metamodels
import org.codingspiderfox.domain.FinanceTransactions;
import org.codingspiderfox.repository.FinanceTransactionsRepository;
import org.codingspiderfox.repository.search.FinanceTransactionsSearchRepository;
import org.codingspiderfox.service.criteria.FinanceTransactionsCriteria;
import org.codingspiderfox.service.dto.FinanceTransactionsDTO;
import org.codingspiderfox.service.mapper.FinanceTransactionsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link FinanceTransactions} entities in the database.
 * The main input is a {@link FinanceTransactionsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FinanceTransactionsDTO} or a {@link Page} of {@link FinanceTransactionsDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FinanceTransactionsQueryService extends QueryService<FinanceTransactions> {

    private final Logger log = LoggerFactory.getLogger(FinanceTransactionsQueryService.class);

    private final FinanceTransactionsRepository financeTransactionsRepository;

    private final FinanceTransactionsMapper financeTransactionsMapper;

    private final FinanceTransactionsSearchRepository financeTransactionsSearchRepository;

    public FinanceTransactionsQueryService(
        FinanceTransactionsRepository financeTransactionsRepository,
        FinanceTransactionsMapper financeTransactionsMapper,
        FinanceTransactionsSearchRepository financeTransactionsSearchRepository
    ) {
        this.financeTransactionsRepository = financeTransactionsRepository;
        this.financeTransactionsMapper = financeTransactionsMapper;
        this.financeTransactionsSearchRepository = financeTransactionsSearchRepository;
    }

    /**
     * Return a {@link List} of {@link FinanceTransactionsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FinanceTransactionsDTO> findByCriteria(FinanceTransactionsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<FinanceTransactions> specification = createSpecification(criteria);
        return financeTransactionsMapper.toDto(financeTransactionsRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link FinanceTransactionsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FinanceTransactionsDTO> findByCriteria(FinanceTransactionsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<FinanceTransactions> specification = createSpecification(criteria);
        return financeTransactionsRepository.findAll(specification, page).map(financeTransactionsMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FinanceTransactionsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<FinanceTransactions> specification = createSpecification(criteria);
        return financeTransactionsRepository.count(specification);
    }

    /**
     * Function to convert {@link FinanceTransactionsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<FinanceTransactions> createSpecification(FinanceTransactionsCriteria criteria) {
        Specification<FinanceTransactions> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getId(), FinanceTransactions_.id));
            }
            if (criteria.getExecutionTimestamp() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getExecutionTimestamp(), FinanceTransactions_.executionTimestamp));
            }
            if (criteria.getAmountAddedToDestinationAccount() != null) {
                specification =
                    specification.and(
                        buildRangeSpecification(
                            criteria.getAmountAddedToDestinationAccount(),
                            FinanceTransactions_.amountAddedToDestinationAccount
                        )
                    );
            }
            if (criteria.getComment() != null) {
                specification = specification.and(buildStringSpecification(criteria.getComment(), FinanceTransactions_.comment));
            }
            if (criteria.getDestinationAccountId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getDestinationAccountId(),
                            root -> root.join(FinanceTransactions_.destinationAccount, JoinType.LEFT).get(FinanceAccount_.id)
                        )
                    );
            }
            if (criteria.getReferenceAccountId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getReferenceAccountId(),
                            root -> root.join(FinanceTransactions_.referenceAccount, JoinType.LEFT).get(FinanceAccount_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
