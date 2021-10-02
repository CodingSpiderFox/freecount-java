package org.codingspiderfox.service;

import java.util.Optional;
import org.codingspiderfox.service.dto.StockDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link org.codingspiderfox.domain.Stock}.
 */
public interface StockService {
    /**
     * Save a stock.
     *
     * @param stockDTO the entity to save.
     * @return the persisted entity.
     */
    StockDTO save(StockDTO stockDTO);

    /**
     * Partially updates a stock.
     *
     * @param stockDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StockDTO> partialUpdate(StockDTO stockDTO);

    /**
     * Get all the stocks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StockDTO> findAll(Pageable pageable);

    /**
     * Get the "id" stock.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StockDTO> findOne(Long id);

    /**
     * Delete the "id" stock.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the stock corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StockDTO> search(String query, Pageable pageable);
}
