package org.codingspiderfox.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.codingspiderfox.domain.FinanceAccount;
import org.codingspiderfox.repository.FinanceAccountRepository;
import org.codingspiderfox.repository.UserRepository;
import org.codingspiderfox.repository.search.FinanceAccountSearchRepository;
import org.codingspiderfox.service.FinanceAccountService;
import org.codingspiderfox.service.dto.FinanceAccountDTO;
import org.codingspiderfox.service.mapper.FinanceAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link FinanceAccount}.
 */
@Service
@Transactional
public class FinanceAccountServiceImpl implements FinanceAccountService {

    private final Logger log = LoggerFactory.getLogger(FinanceAccountServiceImpl.class);

    private final FinanceAccountRepository financeAccountRepository;

    private final FinanceAccountMapper financeAccountMapper;

    private final FinanceAccountSearchRepository financeAccountSearchRepository;

    private final UserRepository userRepository;

    public FinanceAccountServiceImpl(
        FinanceAccountRepository financeAccountRepository,
        FinanceAccountMapper financeAccountMapper,
        FinanceAccountSearchRepository financeAccountSearchRepository,
        UserRepository userRepository
    ) {
        this.financeAccountRepository = financeAccountRepository;
        this.financeAccountMapper = financeAccountMapper;
        this.financeAccountSearchRepository = financeAccountSearchRepository;
        this.userRepository = userRepository;
    }

    @Override
    public FinanceAccountDTO save(FinanceAccountDTO financeAccountDTO) {
        log.debug("Request to save FinanceAccount : {}", financeAccountDTO);
        FinanceAccount financeAccount = financeAccountMapper.toEntity(financeAccountDTO);
        String userId = financeAccountDTO.getOwner().getId();
        userRepository.findById(userId).ifPresent(financeAccount::owner);
        financeAccount = financeAccountRepository.save(financeAccount);
        FinanceAccountDTO result = financeAccountMapper.toDto(financeAccount);
        financeAccountSearchRepository.save(financeAccount);
        return result;
    }

    @Override
    public Optional<FinanceAccountDTO> partialUpdate(FinanceAccountDTO financeAccountDTO) {
        log.debug("Request to partially update FinanceAccount : {}", financeAccountDTO);

        return financeAccountRepository
            .findById(financeAccountDTO.getId())
            .map(existingFinanceAccount -> {
                financeAccountMapper.partialUpdate(existingFinanceAccount, financeAccountDTO);

                return existingFinanceAccount;
            })
            .map(financeAccountRepository::save)
            .map(savedFinanceAccount -> {
                financeAccountSearchRepository.save(savedFinanceAccount);

                return savedFinanceAccount;
            })
            .map(financeAccountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinanceAccountDTO> findAll(Pageable pageable) {
        log.debug("Request to get all FinanceAccounts");
        return financeAccountRepository.findAll(pageable).map(financeAccountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FinanceAccountDTO> findOne(String id) {
        log.debug("Request to get FinanceAccount : {}", id);
        return financeAccountRepository.findById(id).map(financeAccountMapper::toDto);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete FinanceAccount : {}", id);
        financeAccountRepository.deleteById(id);
        financeAccountSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinanceAccountDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of FinanceAccounts for query {}", query);
        return financeAccountSearchRepository.search(query, pageable).map(financeAccountMapper::toDto);
    }
}
