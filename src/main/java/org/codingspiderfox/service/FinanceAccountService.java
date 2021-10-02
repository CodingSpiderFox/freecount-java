package org.codingspiderfox.service;

import java.util.Optional;
import org.codingspiderfox.service.dto.FinanceAccountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link org.codingspiderfox.domain.FinanceAccount}.
 */
public interface FinanceAccountService {
    /**
     * Save a financeAccount.
     *
     * @param financeAccountDTO the entity to save.
     * @return the persisted entity.
     */
    FinanceAccountDTO save(FinanceAccountDTO financeAccountDTO);

    /**
     * Partially updates a financeAccount.
     *
     * @param financeAccountDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FinanceAccountDTO> partialUpdate(FinanceAccountDTO financeAccountDTO);

    /**
     * Get all the financeAccounts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FinanceAccountDTO> findAll(Pageable pageable);

    /**
     * Get the "id" financeAccount.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FinanceAccountDTO> findOne(String id);

    /**
     * Delete the "id" financeAccount.
     *
     * @param id the id of the entity.
     */
    void delete(String id);

    /**
     * Search for the financeAccount corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FinanceAccountDTO> search(String query, Pageable pageable);
}
