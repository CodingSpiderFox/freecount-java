package org.codingspiderfox.service;

import java.util.Optional;
import org.codingspiderfox.service.dto.FinanceTransactionsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link org.codingspiderfox.domain.FinanceTransactions}.
 */
public interface FinanceTransactionsService {
    /**
     * Save a financeTransactions.
     *
     * @param financeTransactionsDTO the entity to save.
     * @return the persisted entity.
     */
    FinanceTransactionsDTO save(FinanceTransactionsDTO financeTransactionsDTO);

    /**
     * Partially updates a financeTransactions.
     *
     * @param financeTransactionsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FinanceTransactionsDTO> partialUpdate(FinanceTransactionsDTO financeTransactionsDTO);

    /**
     * Get all the financeTransactions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FinanceTransactionsDTO> findAll(Pageable pageable);

    /**
     * Get the "id" financeTransactions.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FinanceTransactionsDTO> findOne(String id);

    /**
     * Delete the "id" financeTransactions.
     *
     * @param id the id of the entity.
     */
    void delete(String id);

    /**
     * Search for the financeTransactions corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FinanceTransactionsDTO> search(String query, Pageable pageable);
}
