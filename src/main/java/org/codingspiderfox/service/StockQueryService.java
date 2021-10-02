package org.codingspiderfox.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.codingspiderfox.domain.*; // for static metamodels
import org.codingspiderfox.domain.Stock;
import org.codingspiderfox.repository.StockRepository;
import org.codingspiderfox.repository.search.StockSearchRepository;
import org.codingspiderfox.service.criteria.StockCriteria;
import org.codingspiderfox.service.dto.StockDTO;
import org.codingspiderfox.service.mapper.StockMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Stock} entities in the database.
 * The main input is a {@link StockCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StockDTO} or a {@link Page} of {@link StockDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StockQueryService extends QueryService<Stock> {

    private final Logger log = LoggerFactory.getLogger(StockQueryService.class);

    private final StockRepository stockRepository;

    private final StockMapper stockMapper;

    private final StockSearchRepository stockSearchRepository;

    public StockQueryService(StockRepository stockRepository, StockMapper stockMapper, StockSearchRepository stockSearchRepository) {
        this.stockRepository = stockRepository;
        this.stockMapper = stockMapper;
        this.stockSearchRepository = stockSearchRepository;
    }

    /**
     * Return a {@link List} of {@link StockDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StockDTO> findByCriteria(StockCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Stock> specification = createSpecification(criteria);
        return stockMapper.toDto(stockRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link StockDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StockDTO> findByCriteria(StockCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Stock> specification = createSpecification(criteria);
        return stockRepository.findAll(specification, page).map(stockMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StockCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Stock> specification = createSpecification(criteria);
        return stockRepository.count(specification);
    }

    /**
     * Function to convert {@link StockCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Stock> createSpecification(StockCriteria criteria) {
        Specification<Stock> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Stock_.id));
            }
            if (criteria.getAddedTimestamp() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAddedTimestamp(), Stock_.addedTimestamp));
            }
            if (criteria.getStorageLocation() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStorageLocation(), Stock_.storageLocation));
            }
            if (criteria.getCalculatedExpiryTimestamp() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getCalculatedExpiryTimestamp(), Stock_.calculatedExpiryTimestamp));
            }
            if (criteria.getManualSetExpiryTimestamp() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getManualSetExpiryTimestamp(), Stock_.manualSetExpiryTimestamp));
            }
            if (criteria.getProductId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getProductId(), root -> root.join(Stock_.product, JoinType.LEFT).get(Product_.id))
                    );
            }
        }
        return specification;
    }
}
