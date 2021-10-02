package org.codingspiderfox.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.codingspiderfox.domain.FinanceTransactions;
import org.codingspiderfox.repository.FinanceAccountRepository;
import org.codingspiderfox.repository.FinanceTransactionsRepository;
import org.codingspiderfox.repository.search.FinanceTransactionsSearchRepository;
import org.codingspiderfox.service.FinanceTransactionsService;
import org.codingspiderfox.service.dto.FinanceTransactionsDTO;
import org.codingspiderfox.service.mapper.FinanceTransactionsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link FinanceTransactions}.
 */
@Service
@Transactional
public class FinanceTransactionsServiceImpl implements FinanceTransactionsService {

    private final Logger log = LoggerFactory.getLogger(FinanceTransactionsServiceImpl.class);

    private final FinanceTransactionsRepository financeTransactionsRepository;

    private final FinanceTransactionsMapper financeTransactionsMapper;

    private final FinanceTransactionsSearchRepository financeTransactionsSearchRepository;

    private final FinanceAccountRepository financeAccountRepository;

    public FinanceTransactionsServiceImpl(
        FinanceTransactionsRepository financeTransactionsRepository,
        FinanceTransactionsMapper financeTransactionsMapper,
        FinanceTransactionsSearchRepository financeTransactionsSearchRepository,
        FinanceAccountRepository financeAccountRepository
    ) {
        this.financeTransactionsRepository = financeTransactionsRepository;
        this.financeTransactionsMapper = financeTransactionsMapper;
        this.financeTransactionsSearchRepository = financeTransactionsSearchRepository;
        this.financeAccountRepository = financeAccountRepository;
    }

    @Override
    public FinanceTransactionsDTO save(FinanceTransactionsDTO financeTransactionsDTO) {
        log.debug("Request to save FinanceTransactions : {}", financeTransactionsDTO);
        FinanceTransactions financeTransactions = financeTransactionsMapper.toEntity(financeTransactionsDTO);
        String financeAccountId = financeTransactionsDTO.getReferenceAccount().getId();
        financeAccountRepository.findById(financeAccountId).ifPresent(financeTransactions::referenceAccount);
        financeTransactions = financeTransactionsRepository.save(financeTransactions);
        FinanceTransactionsDTO result = financeTransactionsMapper.toDto(financeTransactions);
        financeTransactionsSearchRepository.save(financeTransactions);
        return result;
    }

    @Override
    public Optional<FinanceTransactionsDTO> partialUpdate(FinanceTransactionsDTO financeTransactionsDTO) {
        log.debug("Request to partially update FinanceTransactions : {}", financeTransactionsDTO);

        return financeTransactionsRepository
            .findById(financeTransactionsDTO.getId())
            .map(existingFinanceTransactions -> {
                financeTransactionsMapper.partialUpdate(existingFinanceTransactions, financeTransactionsDTO);

                return existingFinanceTransactions;
            })
            .map(financeTransactionsRepository::save)
            .map(savedFinanceTransactions -> {
                financeTransactionsSearchRepository.save(savedFinanceTransactions);

                return savedFinanceTransactions;
            })
            .map(financeTransactionsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinanceTransactionsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all FinanceTransactions");
        return financeTransactionsRepository.findAll(pageable).map(financeTransactionsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FinanceTransactionsDTO> findOne(String id) {
        log.debug("Request to get FinanceTransactions : {}", id);
        return financeTransactionsRepository.findById(id).map(financeTransactionsMapper::toDto);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete FinanceTransactions : {}", id);
        financeTransactionsRepository.deleteById(id);
        financeTransactionsSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinanceTransactionsDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of FinanceTransactions for query {}", query);
        return financeTransactionsSearchRepository.search(query, pageable).map(financeTransactionsMapper::toDto);
    }
}
