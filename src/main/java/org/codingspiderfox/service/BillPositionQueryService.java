package org.codingspiderfox.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.codingspiderfox.domain.Bill;
import org.codingspiderfox.domain.BillPosition;
import org.codingspiderfox.domain.BillPosition_;
import org.codingspiderfox.domain.Bill_;
import org.codingspiderfox.domain.Project_;
import org.codingspiderfox.repository.BillPositionRepository;
import org.codingspiderfox.repository.search.BillPositionSearchRepository;
import org.codingspiderfox.service.criteria.BillCriteria;
import org.codingspiderfox.service.criteria.BillPositionCriteria;
import org.codingspiderfox.service.dto.BillDTO;
import org.codingspiderfox.service.dto.BillPositionDTO;
import org.codingspiderfox.service.mapper.BillPositionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.LongFilter;

/**
 * Service for executing complex queries for {@link Bill} entities in the database.
 * The main input is a {@link BillCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BillDTO} or a {@link Page} of {@link BillDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BillPositionQueryService extends QueryService<BillPosition> {

    private final Logger log = LoggerFactory.getLogger(BillPositionQueryService.class);

    private final BillPositionRepository billPositionRepository;

    private final BillPositionMapper billPositionMapper;

    private final BillPositionSearchRepository billPositionSearchRepository;

    public BillPositionQueryService(
        BillPositionRepository billPositionRepository,
        BillPositionMapper billPositionMapper,
        BillPositionSearchRepository billPositionSearchRepository
    ) {
        this.billPositionRepository = billPositionRepository;
        this.billPositionMapper = billPositionMapper;
        this.billPositionSearchRepository = billPositionSearchRepository;
    }

    /**
     * Return a {@link List} of {@link BillDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BillPositionDTO> findByCriteria(BillPositionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<BillPosition> specification = createSpecification(criteria);
        return billPositionMapper.toDto(billPositionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BillPositionDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BillPositionDTO> findByCriteria(BillPositionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BillPosition> specification = createSpecification(criteria);
        return billPositionRepository.findAll(specification, page).map(billPositionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BillPositionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<BillPosition> specification = createSpecification(criteria);
        return billPositionRepository.count(specification);
    }

    @Transactional(readOnly = true)
    public List<BillPosition> findByBillId(Long billId) {
        LongFilter billIdFilter = new LongFilter();
        billIdFilter.setEquals(billId);
        BillPositionCriteria criteria = new BillPositionCriteria();
        criteria.setBillId(billIdFilter);
        final Specification<BillPosition> specification = createSpecification(criteria);
        return billPositionRepository.findAll(specification);
    }

    /**
     * Function to convert {@link BillPositionCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BillPosition> createSpecification(BillPositionCriteria criteria) {
        Specification<BillPosition> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BillPosition_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), BillPosition_.title));
            }
            if (criteria.getCost() != null) {
                specification = specification.and(buildSpecification(criteria.getCost(), BillPosition_.cost));
            }
            if (criteria.getBillId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getBillId(), root -> root.join(BillPosition_.bill, JoinType.LEFT).get(Bill_.id))
                    );
            }
        }
        return specification;
    }
}
