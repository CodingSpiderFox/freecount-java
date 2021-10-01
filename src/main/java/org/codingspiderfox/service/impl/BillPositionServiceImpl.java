package org.codingspiderfox.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.codingspiderfox.domain.BillPosition;
import org.codingspiderfox.repository.BillPositionRepository;
import org.codingspiderfox.repository.search.BillPositionSearchRepository;
import org.codingspiderfox.service.BillPositionService;
import org.codingspiderfox.service.dto.BillPositionDTO;
import org.codingspiderfox.service.mapper.BillPositionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BillPosition}.
 */
@Service
@Transactional
public class BillPositionServiceImpl implements BillPositionService {

    private final Logger log = LoggerFactory.getLogger(BillPositionServiceImpl.class);

    private final BillPositionRepository billPositionRepository;

    private final BillPositionMapper billPositionMapper;

    private final BillPositionSearchRepository billPositionSearchRepository;

    public BillPositionServiceImpl(
        BillPositionRepository billPositionRepository,
        BillPositionMapper billPositionMapper,
        BillPositionSearchRepository billPositionSearchRepository
    ) {
        this.billPositionRepository = billPositionRepository;
        this.billPositionMapper = billPositionMapper;
        this.billPositionSearchRepository = billPositionSearchRepository;
    }

    @Override
    public BillPositionDTO save(BillPositionDTO billPositionDTO) {
        log.debug("Request to save BillPosition : {}", billPositionDTO);
        BillPosition billPosition = billPositionMapper.toEntity(billPositionDTO);
        billPosition = billPositionRepository.save(billPosition);
        BillPositionDTO result = billPositionMapper.toDto(billPosition);
        billPositionSearchRepository.save(billPosition);
        return result;
    }

    @Override
    public Optional<BillPositionDTO> partialUpdate(BillPositionDTO billPositionDTO) {
        log.debug("Request to partially update BillPosition : {}", billPositionDTO);

        return billPositionRepository
            .findById(billPositionDTO.getId())
            .map(existingBillPosition -> {
                billPositionMapper.partialUpdate(existingBillPosition, billPositionDTO);

                return existingBillPosition;
            })
            .map(billPositionRepository::save)
            .map(savedBillPosition -> {
                billPositionSearchRepository.save(savedBillPosition);

                return savedBillPosition;
            })
            .map(billPositionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BillPositionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BillPositions");
        return billPositionRepository.findAll(pageable).map(billPositionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BillPositionDTO> findOne(Long id) {
        log.debug("Request to get BillPosition : {}", id);
        return billPositionRepository.findById(id).map(billPositionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete BillPosition : {}", id);
        billPositionRepository.deleteById(id);
        billPositionSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BillPositionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of BillPositions for query {}", query);
        return billPositionSearchRepository.search(query, pageable).map(billPositionMapper::toDto);
    }
}
